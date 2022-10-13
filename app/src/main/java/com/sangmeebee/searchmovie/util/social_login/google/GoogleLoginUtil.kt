package com.sangmeebee.searchmovie.util.social_login.google

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sangmeebee.searchmovie.model.UserModel
import com.sangmeebee.searchmovie.util.social_login.SocialLoginUtil
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GoogleLoginUtil @Inject constructor() : SocialLoginUtil {

    private val auth = Firebase.auth

    override suspend fun login(context: Context): Result<String> = runCatching {
        suspendCoroutine { continuation ->
            val oneTapClient = Identity.getSignInClient(context)
            GoogleProxyActivity.startActivityForLogin(context) { result, throwable ->
                if (result == null) {
                    continuation.resumeWithException(throwable!!)
                } else {
                    continuation.checkExceptionAndSignIn(result, oneTapClient)
                }
            }
        }
    }

    private fun Continuation<String>.checkExceptionAndSignIn(result: ActivityResult, oneTapClient: SignInClient) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken
            when {
                idToken != null -> {
                    // Got an ID token from Google. Use it to authenticate
                    // with Firebase.
                    Log.d("Sangmeebee", "Got ID token: $idToken")
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                    resume(idToken)
//                                .addOnCompleteListener(this) { task ->
//                                    if (task.isSuccessful) {
//                                        // Sign in success, update UI with the signed-in user's information
//                                        Log.d(TAG, "signInWithCredential:success")
//                                        val user = auth.currentUser
//                                        updateUI(user)
//                                    } else {
//                                        // If sign in fails, display a message to the user.
//                                        Log.w(TAG, "signInWithCredential:failure", task.exception)
//                                        updateUI(null)
//                                    }
//                                }
                }
                else -> {
                    resumeWithException(IllegalStateException("No ID token!"))
                }
            }
        } catch (e: ApiException) {
            resumeWithException(e)
        }
    }

    override suspend fun logout(context: Context): Result<Boolean> = runCatching {
        val oneTapClient = Identity.getSignInClient(context)
        suspendCoroutine { continuation ->
            oneTapClient.signOut()
                .addOnCompleteListener {
                    auth.signOut()
                    continuation.resume(true)
                }
                .addOnFailureListener { error ->
                    continuation.resumeWithException(error)
                }
        }
    }

    override suspend fun isLogin(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun getUserInfo(): Result<UserModel> {
        Log.d("Sangmeebee", auth.currentUser.toString())
        return Result.success(UserModel("12"))
    }
}