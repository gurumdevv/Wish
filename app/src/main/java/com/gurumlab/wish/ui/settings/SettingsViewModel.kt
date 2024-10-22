package com.gurumlab.wish.ui.settings

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.gurumlab.wish.BuildConfig
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.SettingsRepository
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private typealias SignInSuccessListener = (uid: String) -> Unit

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _accountUiState = MutableStateFlow(AccountUiState())
    val accountUiState = _accountUiState.asStateFlow()

    private val _approachingProjectUiState: MutableStateFlow<ApproachingProjectUiState> =
        MutableStateFlow(ApproachingProjectUiState.Loading)
    val approachingProjectUiState = _approachingProjectUiState.asStateFlow()

    private val _myProjectUiState: MutableStateFlow<MyProjectUiState> =
        MutableStateFlow(MyProjectUiState.Loading)
    val myProjectUiState = _myProjectUiState.asStateFlow()

    val userInfo = fetchUserInfo()

    fun logOut() {
        repository.logOut()
        _accountUiState.update { it.copy(isLogOut = true) }
    }

    private fun loadWishes(
        orderBy: String,
        onLoading: () -> Unit,
        onEmpty: () -> Unit,
        onException: (String?) -> Unit,
        onSuccess: (Map<String, Wish>) -> Unit
    ) {
        val uid = repository.getUid()

        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                onException("Fail to get idToken")
                return@launch
            }

            val response = repository.getWishes(
                idToken = idToken,
                orderBy = orderBy,
                equalTo = uid,
                onError = { _ ->
                    onEmpty()
                },
                onException = { e ->
                    onException(e)
                    Log.d("SettingsViewModel", "Fail to load data: $e")
                }
            )

            response
                .onStart { onLoading() }
                .collect { wishes ->
                    onSuccess(wishes)
                }
        }
    }

    fun loadApproachingWishes() {
        loadWishes(
            orderBy = Constants.DEVELOPER_ID,
            onLoading = {
                _approachingProjectUiState.value = ApproachingProjectUiState.Loading
            },
            onEmpty = {
                _approachingProjectUiState.value = ApproachingProjectUiState.Empty
            },
            onException = {
                _approachingProjectUiState.value = ApproachingProjectUiState.Exception
            },
            onSuccess = { wishes ->
                _approachingProjectUiState.value = ApproachingProjectUiState.Success(wishes)
            }
        )
    }

    fun loadMyWishes() {
        loadWishes(
            orderBy = Constants.POSTER_ID,
            onLoading = {
                _myProjectUiState.value = MyProjectUiState.Loading
            },
            onEmpty = {
                _myProjectUiState.value = MyProjectUiState.Empty
            },
            onException = {
                _myProjectUiState.value = MyProjectUiState.Exception
            },
            onSuccess = { wishes ->
                _myProjectUiState.value = MyProjectUiState.Success(wishes)
            }
        )
    }

    suspend fun deleteWish(wishId: String) {
        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                return@launch
            }
            repository.deleteWish(idToken = idToken, wishId = wishId)
            _myProjectUiState.value = MyProjectUiState.Loading
        }
    }

    private fun fetchUserInfo(): CurrentUserInfo {
        val userInfo = repository.getCurrentUser()
        val name = userInfo?.displayName ?: Constants.USER
        val email = userInfo?.email ?: Constants.EMAIL
        val imageUrl = userInfo?.photoUrl?.toString() ?: ""
        return CurrentUserInfo(name, email, imageUrl)
    }

    fun getMinimizedWish(
        wish: Wish,
    ): MinimizedWish {
        return MinimizedWish(
            postId = wish.postId,
            createdDate = wish.createdDate,
            startedDate = wish.startedDate,
            completedDate = wish.completedDate,
            posterId = wish.posterId,
            developerId = wish.developerId,
            posterName = wish.posterName,
            developerName = wish.developerName,
            title = wish.title,
            simpleDescription = wish.simpleDescription,
            comment = wish.comment
        )
    }

    private fun deleteAccount(googleIdToken: String) {
        viewModelScope.launch {
            val uid = repository.getUid()
            val idToken = repository.getFirebaseIdToken()

            repository.deleteAccount(googleIdToken = googleIdToken, idToken = idToken, uid = uid)
                .collect { isSuccess ->
                    _accountUiState.update { it.copy(isDeleteAccount = isSuccess) }
                }
        }
    }

    //From LoginViewModel
    private lateinit var signInClient: SignInClient
    private var signInRequest: BeginSignInRequest = getBeginSignInRequest()

    private val onSuccess: SignInSuccessListener = { idToken ->
        deleteAccount(idToken)
    }

    private fun getBeginSignInRequest() = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build()
        ).build()

    fun setSignInRequest(
        oneTapSignInLauncher: ActivityResultLauncher<IntentSenderRequest>,
        legacySignInLauncher: ActivityResultLauncher<IntentSenderRequest>,
        context: Context
    ) {
        signInClient = Identity.getSignInClient(context)

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                try {
                    oneTapSignInLauncher.launch(
                        IntentSenderRequest
                            .Builder(result.pendingIntent.intentSender)
                            .build()
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.d("SettingsViewModel", "sign-in error: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                Log.d("SettingsViewModel", "sign-in fail: ${e.localizedMessage}")
                requestLegacySignIn(legacySignInLauncher, context)
            }
            .addOnCanceledListener {
                Log.d("SettingsViewModel", "sign-in cancelled")
            }
    }

    fun signInWithGoogle(data: Intent?) {
        val credential = signInClient.getSignInCredentialFromIntent(data)
        val idToken = credential.googleIdToken
        if (idToken != null) {
            onSuccess(idToken)
        }
    }

    private fun requestLegacySignIn(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        context: Context
    ) {
        val lastToken = GoogleSignIn.getLastSignedInAccount(context)?.idToken
        if (lastToken == null) {
            val request = GetSignInIntentRequest.builder()
                .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
                .build()

            signInClient
                .getSignInIntent(request)
                .addOnSuccessListener { pendingIntent ->
                    launcher.launch(
                        IntentSenderRequest
                            .Builder(pendingIntent.intentSender)
                            .build()
                    )
                }
                .addOnFailureListener { e ->
                    Log.d("SettingsViewModel", "Legacy sign-in fail: ${e.localizedMessage}")
                }
                .addOnCanceledListener {
                    Log.d("SettingsViewModel", "Legacy sign-in cancelled")
                }
        } else {
            onSuccess(lastToken)
        }
    }

    fun notifyIsLoginError(message: String) {
        Log.d("SettingsViewModel", "error: $message")
    }
}

data class CurrentUserInfo(
    val name: String,
    val email: String,
    val imageUrl: String
)

data class AccountUiState(
    val isLogOut: Boolean = false,
    val isDeleteAccount: Boolean = false
)

sealed class ApproachingProjectUiState {
    data class Success(val wishes: Map<String, Wish>) : ApproachingProjectUiState()
    data object Loading : ApproachingProjectUiState()
    data object Empty : ApproachingProjectUiState()
    data object Exception : ApproachingProjectUiState()
}

sealed class MyProjectUiState {
    data class Success(val wishes: Map<String, Wish>) : MyProjectUiState()
    data object Loading : MyProjectUiState()
    data object Empty : MyProjectUiState()
    data object Exception : MyProjectUiState()
}