package com.gurumlab.wish.ui.post

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.DetailDescription
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.PostRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import com.gurumlab.wish.ui.util.URL
import com.gurumlab.wish.ui.util.WishesUpdateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val wishesUpdateManager: WishesUpdateManager
) : ViewModel() {

    var projectTitle by mutableStateOf("")
        private set
    var oneLineDescription by mutableStateOf("")
        private set
    var simpleDescription by mutableStateOf("")
        private set

    var projectDescription by mutableStateOf(TextFieldValue())
        private set
    var featureTitles = mutableStateMapOf(Pair(0, ""))
        private set
    var featureDescriptions = mutableStateMapOf(Pair(0, TextFieldValue()))
        private set
    var selectedImageUris = mutableStateMapOf<Int, List<Uri>>(Pair(0, emptyList()))
        private set
    private val imageDownloadUrls = mutableMapOf<Int, MutableList<String>>()

    private val _postExaminationUiState: MutableStateFlow<PostExaminationUiState> =
        MutableStateFlow(PostExaminationUiState.Nothing)
    val postExaminationUiState = _postExaminationUiState.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    var snackbarMessageRes: MutableState<Int?> = mutableStateOf(null)
        private set

    fun uploadPost() {
        viewModelScope.launch {
            isLoading = true

            val idToken = repository.getFirebaseIdToken()
            if (idToken.isEmpty()) {
                _postExaminationUiState.value = PostExaminationUiState.Failed
                return@launch
            }

            val postId = getPostId()

            val imageUploadedResult =
                uploadImages(postId = postId, imageUris = selectedImageUris.toMap()).await()

            when (imageUploadedResult) {
                UploadState.Success -> {
                    _postExaminationUiState.value =
                        if (repository.uploadPost(idToken = idToken, wish = getWish(postId))) {
                            wishesUpdateManager.updateState()
                            PostExaminationUiState.Success
                        } else {
                            PostExaminationUiState.Failed
                        }
                }

                UploadState.Failed -> {
                    _postExaminationUiState.value = PostExaminationUiState.Failed
                }
            }

            isLoading = false
        }
    }

    private fun uploadImages(postId: String, imageUris: Map<Int, List<Uri>>) =
        viewModelScope.async {
            val uploadResults = imageUris.flatMap { (key, imageUris) ->
                imageUris.map { imageUri ->
                    async {
                        val imageDownloadUrl = repository.uploadImages(
                            postId = postId,
                            featureIndex = key,
                            imageUri = imageUri
                        )
                        imageDownloadUrls.getOrPut(key) { mutableListOf() }.add(imageDownloadUrl)
                        imageDownloadUrl
                    }
                }
            }

            val uploadSuccessList = uploadResults.awaitAll()
            val successUploadCount = uploadSuccessList.count { it.isNotBlank() }
            val isAllUploaded = successUploadCount == imageUris.values.sumOf { it.size }

            if (isAllUploaded) UploadState.Success else UploadState.Failed
        }

    private fun getDetailFeatures(): List<DetailDescription> {
        val features = mutableListOf<DetailDescription>()

        featureTitles.forEach { (index, title) ->
            val description = featureDescriptions[index]!!
            val photos = imageDownloadUrls.getOrDefault(
                key = index,
                defaultValue = emptyList()
            )
            features.add(
                DetailDescription(
                    feature = title,
                    description = description.text,
                    photos = photos
                )
            )
        }

        return features.toList()
    }

    private fun getWish(postId: String): Wish {
        val uid = repository.getCurrentUser()?.uid ?: ""
        val userName = repository.getCurrentUser()?.displayName ?: ""
        val currentDate = DateTimeConverter.getCurrentDate()
        val detailFeatures = getDetailFeatures()

        return Wish(
            postId = postId,
            createdDate = currentDate,
            startedDate = 0,
            completedDate = 0,
            posterId = uid,
            developerId = "",
            posterName = userName,
            developerName = "",
            title = projectTitle,
            representativeImage = imageDownloadUrls[0]?.firstOrNull()
                ?: URL.DEFAULT_WISH_PHOTO,
            status = WishStatus.POSTED.ordinal,
            likes = 0,
            oneLineDescription = oneLineDescription,
            simpleDescription = simpleDescription,
            detailDescription = projectDescription.text,
            detailFeatures = detailFeatures,
            features = featureTitles.values.toList(),
            comment = ""
        )
    }

    private fun getPostId(): String {
        val uid = repository.getCurrentUser()?.uid ?: ""
        val date = DateTimeConverter.getCurrentDate()
        val randomNum = Random.nextInt(0, 9999)
        val formattedNum = String.format(Locale.getDefault(), "%04d", randomNum)
        return "$uid$date$formattedNum"
    }

    fun updateProjectTitle(title: String) {
        projectTitle = title
    }

    fun updateOneLineDescription(description: String) {
        oneLineDescription = description
    }

    fun updateSimpleDescription(description: String) {
        simpleDescription = description
    }

    fun updateProjectDescription(description: TextFieldValue) {
        projectDescription = description
    }

    fun updateFeatureTitles(index: Int, title: String) {
        featureTitles[index] = title
    }

    fun updateFeatureDescriptions(index: Int, description: TextFieldValue) {
        featureDescriptions[index] = description
    }

    fun updateSelectedImageUris(index: Int, uris: List<Uri>) {
        selectedImageUris[index] = uris
    }

    fun isProjectTitleEmpty(): Boolean {
        return projectTitle.isEmpty()
    }

    fun isOneLineDescriptionEmpty(): Boolean {
        return oneLineDescription.isEmpty()
    }

    fun isSimpleDescriptionEmpty(): Boolean {
        return simpleDescription.isEmpty()
    }

    fun isProjectDescriptionEmpty(): Boolean {
        return projectDescription.text.isEmpty()
    }

    fun isAttachedPhoto(): Boolean {
        return selectedImageUris.values.all { it.isEmpty() }
    }

    fun isAnyTitleEmpty(): Boolean {
        return areAnyFieldsEmpty(featureTitles.values.toList())
    }

    fun isAnyDescriptionEmpty(): Boolean {
        return areAnyFieldsEmpty(featureDescriptions.values.toList().map { it.text })
    }

    private fun areAnyFieldsEmpty(list: List<String>): Boolean {
        return list.any { it.isEmpty() } || list.isEmpty()
    }

    fun showSnackbarMessage(messageRes: Int) {
        viewModelScope.launch {
            updateSnackbarMessage(messageRes)
            delay(4000L) //SnackbarDuration.Short
            resetSnackbarMessage()
        }
    }

    private fun updateSnackbarMessage(messageRes: Int) {
        snackbarMessageRes.value = messageRes
    }

    private fun resetSnackbarMessage() {
        snackbarMessageRes.value = null
    }
}

sealed class UploadState {
    data object Success : UploadState()
    data object Failed : UploadState()
}

sealed class PostExaminationUiState {
    data object Nothing : PostExaminationUiState()
    data object Success : PostExaminationUiState()
    data object Failed : PostExaminationUiState()
}