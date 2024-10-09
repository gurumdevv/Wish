package com.gurumlab.wish.ui.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.BuildConfig
import com.gurumlab.wish.data.model.UserInfo
import com.gurumlab.wish.data.repository.LoginRepository
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private typealias SignInSuccessListener = (uid: String) -> Unit

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState
    private val _agreementState = mutableStateOf(AgreementState())
    val agreementState: State<AgreementState> = _agreementState

    fun onAgreementChange(
        ageChecked: Boolean? = null,
        termsChecked: Boolean? = null,
        privacyChecked: Boolean? = null
    ) {
        _agreementState.value = _agreementState.value.copy(
            isAgeChecked = ageChecked ?: _agreementState.value.isAgeChecked,
            isTermsChecked = termsChecked ?: _agreementState.value.isTermsChecked,
            isPrivacyChecked = privacyChecked ?: _agreementState.value.isPrivacyChecked
        )
    }

    private lateinit var signInClient: SignInClient
    private var signInRequest: BeginSignInRequest = getBeginSignInRequest()

    private val onSuccess: SignInSuccessListener = { idToken ->
        getFirebaseCredential(idToken)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            _uiState.update { it.copy(isLoginSuccess = true) }
        } else {
            updateFailState()
        }
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
        _uiState.update { it.copy(isOnGoingSignIn = true) }

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
                    Log.d("LoginViewModel", "sign-in error: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                Log.d("LoginViewModel", "sign-in fail: ${e.localizedMessage}")
                requestLegacySignIn(legacySignInLauncher, context)
            }
            .addOnCanceledListener {
                Log.d("LoginViewModel", "sign-in cancelled")
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
                    Log.d("LoginViewModel", "Legacy sign-in fail: ${e.localizedMessage}")
                    _uiState.update { it.copy(isOnGoingSignIn = false, isLoginError = true) }
                }
                .addOnCanceledListener {
                    Log.d("LoginViewModel", "Legacy sign-in cancelled")
                    _uiState.update { it.copy(isOnGoingSignIn = false, isLoginError = true) }
                }
        } else {
            onSuccess(lastToken)
        }
    }

    private fun getFirebaseCredential(idToken: String?) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
            .addOnCompleteListener {
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    updateFailState()
                } else {
                    user.getIdToken(true)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseIdToken = task.result.token
                                if (firebaseIdToken.isNullOrBlank() || user.uid.isBlank()) {
                                    updateFailState()
                                } else {
                                    viewModelScope.launch {
                                        repository.setUid(user.uid)
                                        registerUserInfo(user.uid) { isSuccess ->
                                            if (isSuccess) updateUI(user)
                                            else updateFailState()
                                        }
                                    }
                                }
                            } else {
                                updateFailState()
                            }
                        }
                }
            }.addOnFailureListener {
                Log.d("LoginViewModel", "Firebase authentication failed: ${it.message}")
                updateFailState()
            }
    }

    private fun registerUserInfo(uid: String, callback: (Boolean) -> Unit) {
        val ref = Firebase.database.getReference(Constants.AUTH)
        ref.child(uid).get().addOnSuccessListener {
            if (it.value == null) {
                val currentUser = Firebase.auth.currentUser
                val userInfo = UserInfo(
                    uid,
                    currentUser?.email ?: "",
                    currentUser?.displayName ?: "",
                    (currentUser?.photoUrl ?: "").toString()
                )
                ref.child(uid).setValue(userInfo)
            }
            callback(true)
        }.addOnFailureListener {
            Log.d("LoginViewModel", "Firebase register userInfo failed: ${it.message}")
            callback(false)
        }
    }

    fun resetIsLoginError() {
        _uiState.update { it.copy(isLoginError = false, isOnGoingSignIn = false) }
    }

    fun notifyIsLoginError() {
        _uiState.update { it.copy(isLoginError = true) }
    }

    private fun updateFailState() {
        _uiState.update { it.copy(isOnGoingSignIn = false, isLoginError = true) }
    }
}

data class AgreementState(
    val isAgeChecked: Boolean = false,
    val isTermsChecked: Boolean = false,
    val isPrivacyChecked: Boolean = false
) {
    val isAllChecked: Boolean
        get() = isAgeChecked && isTermsChecked && isPrivacyChecked
}

data class LoginUiState(
    val isOnGoingSignIn: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val isLoginError: Boolean = false
)