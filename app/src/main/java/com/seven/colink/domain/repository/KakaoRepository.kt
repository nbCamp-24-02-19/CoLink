package com.seven.colink.domain.repository

interface KakaoRepository {

    suspend fun kakaoLogin(): Result<String>
}
