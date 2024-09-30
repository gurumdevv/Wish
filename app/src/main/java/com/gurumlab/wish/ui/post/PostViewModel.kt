package com.gurumlab.wish.ui.post

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.DetailDescription
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.PostRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private var _projectTitle = mutableStateOf("")
    val projectTitle: State<String> = _projectTitle
    private var _oneLineDescription = mutableStateOf("")
    val oneLineDescription: State<String> = _oneLineDescription
    private var _simpleDescription = mutableStateOf("")
    val simpleDescription: State<String> = _simpleDescription

    private var _projectDescription = mutableStateOf("")
    val projectDescription: State<String> = _projectDescription

    private var _itemCount = mutableIntStateOf(1)
    val itemCount: State<Int> = _itemCount
    private val _featureTitles = mutableStateMapOf<Int, String>()
    val featureTitles: Map<Int, String> get() = _featureTitles
    private val _featureDescriptions = mutableStateMapOf<Int, String>()
    val featureDescriptions: Map<Int, String> get() = _featureDescriptions
    private val _selectedImageUris = mutableStateMapOf<Int, List<Uri>>()
    val selectedImageUris: Map<Int, List<Uri>> get() = _selectedImageUris
    private var imageDownloadUrls = mutableMapOf<Int, MutableList<String>>()

    private var isImageUploaded = UploadState.NOTHING.ordinal
    private var _isPostUploaded = mutableIntStateOf(UploadState.NOTHING.ordinal)
    val isPostUploaded = _isPostUploaded

    private var _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun setProjectTitle(title: String) {
        _projectTitle.value = title
    }

    fun setOneLineDescription(description: String) {
        _oneLineDescription.value = description
    }

    fun setSimpleDescription(description: String) {
        _simpleDescription.value = description
    }

    fun setProjectDescription(description: String) {
        _projectDescription.value = description
    }

    fun setItemCount(count: Int) {
        _itemCount.intValue = count
    }

    fun setFeatureTitles(index: Int, title: String) {
        _featureTitles[index] = title
    }

    fun setFeatureDescriptions(index: Int, description: String) {
        _featureDescriptions[index] = description
    }

    fun setSelectedImageUris(index: Int, uris: List<Uri>) {
        _selectedImageUris[index] = uris
    }

    private fun uploadImages(imageUris: Map<Int, List<Uri>>): Job = viewModelScope.launch {
        _isLoading.value = true

        val postId = getPostId()
        val uploadResults = imageUris.flatMap { (key, imageUris) ->
            imageUris.map { imageUri ->
                async {
                    val imageDownloadUrl = repository.uploadImages(
                        imageUri = imageUri,
                        postId = postId,
                        featureIndex = key
                    )
                    imageDownloadUrls.getOrPut(key) { mutableListOf() }.add(imageDownloadUrl)
                    imageDownloadUrl
                }
            }
        }

        val uploadSuccessList = uploadResults.awaitAll()

        val successUploadCount = uploadSuccessList.count { it.isNotEmpty() }
        val isAllUploaded = successUploadCount == imageUris.values.sumOf { it.size }

        isImageUploaded =
            if (isAllUploaded) UploadState.SUCCESS.ordinal else UploadState.FAILED.ordinal
    }

    fun uploadPost() {
        val uploadImagesProcess = uploadImages(selectedImageUris.toMap())

        uploadImagesProcess.invokeOnCompletion {
            when (isImageUploaded) {
                UploadState.SUCCESS.ordinal -> {
                    val features = mutableListOf<DetailDescription>()

                    featureTitles.forEach { (index, title) ->
                        val description = featureDescriptions[index] ?: ""
                        val photos = imageDownloadUrls[index] ?: emptyList()
                        features.add(DetailDescription(title, description, photos))
                    }

                    val wish = Wish(
                        postId = getPostId(),
                        createdDate = DateTimeConverter.getCurrentDate(),
                        startedDate = 0,
                        completedDate = 0,
                        posterId = getPosterUid(),
                        developerId = "",
                        posterName = getPosterName(),
                        developerName = "",
                        title = projectTitle.value,
                        representativeImage = imageDownloadUrls[0]?.firstOrNull() ?: "",
                        status = WishStatus.POSTED.ordinal,
                        likes = 0,
                        oneLineDescription = oneLineDescription.value,
                        simpleDescription = simpleDescription.value,
                        detailDescription = features,
                        features = featureTitles.values.toList(),
                        comment = ""
                    )

                    viewModelScope.launch {
                        if (repository.uploadPost(wish)) {
                            isPostUploaded.intValue = UploadState.SUCCESS.ordinal
                        } else {
                            isPostUploaded.intValue = UploadState.FAILED.ordinal
                            _isLoading.value = false
                        }
                    }
                }

                UploadState.FAILED.ordinal -> {
                    isPostUploaded.intValue = UploadState.FAILED.ordinal
                    _isLoading.value = false
                }
            }
        }
    }

    private fun getPostId(): String {
        val uid = getPosterUid()
        val date = DateTimeConverter.getCurrentDate()
        val randomNum = Random.nextInt(0, 9999)
        val formattedNum = String.format(Locale.getDefault(), "%04d", randomNum)
        return "$uid$date$formattedNum"
    }

    private fun getPosterUid(): String {
        val uid = "userId" //TODO("get user id")
        return uid
    }

    private fun getPosterName(): String {
        val name = "userName" //TODO("get user name")
        return name
    }
}

enum class UploadState {
    NOTHING,
    SUCCESS,
    FAILED
}