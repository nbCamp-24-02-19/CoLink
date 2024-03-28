package com.seven.colink.data.remote.repository

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.seven.colink.domain.repository.KakaoRepository
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class KakaoRepositoryImpl @Inject constructor(
    private val client: UserApiClient,
    @ActivityContext private val context: Context,
): KakaoRepository {
    override suspend fun kakaoLogin(): Result<String> = suspendCoroutine { continuation: Continuation<Result<String>> ->
        if (client.isKakaoTalkLoginAvailable(context)) {
            client.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        continuation.resume(Result.failure(CancellationException("Login cancelled")))
                    } else {
                        loginWithKaKaoAccount(continuation)
                    }
                } else if (token != null) {
                    continuation.resume(Result.success(token.accessToken))
                }
            }
        } else {
            loginWithKaKaoAccount(continuation)
        }
    }

    private fun loginWithKaKaoAccount(continuation: Continuation<Result<String>>) {
        client.loginWithKakaoAccount(context) { token: OAuthToken?, error: Throwable? ->
            if (error != null) {
                continuation.resume(Result.failure(error))
            } else if (token != null) {
                continuation.resume(Result.success(token.accessToken))
            }
        }
    }
}