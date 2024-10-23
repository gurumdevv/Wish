package com.gurumlab.wish.ui.settings

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.gurumlab.wish.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginService {

    private val _idToken: MutableStateFlow<String?> = MutableStateFlow(null)
    val idToken = _idToken.asStateFlow()

    private lateinit var signInClient: SignInClient
    private var signInRequest: BeginSignInRequest = getBeginSignInRequest()

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
                    _idToken.value = ""
                    Log.d("LoginService", "sign-in error: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                Log.d("LoginService", "sign-in fail: ${e.localizedMessage}")
                requestLegacySignIn(legacySignInLauncher, context)
            }
            .addOnCanceledListener {
                Log.d("LoginService", "sign-in cancelled")
            }
    }

    fun signInWithGoogle(data: Intent?) {
        val credential = signInClient.getSignInCredentialFromIntent(data)
        val idToken = credential.googleIdToken
        _idToken.value = idToken ?: ""
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
                    _idToken.value = ""
                    Log.d("LoginService", "Legacy sign-in fail: ${e.localizedMessage}")
                }
                .addOnCanceledListener {
                    Log.d("LoginService", "Legacy sign-in cancelled")
                }
        } else {
            _idToken.value = lastToken
        }
    }
}