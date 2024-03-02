package com.seven.colink.domain.usecase

import com.seven.colink.domain.repository.ChatRepository
import javax.inject.Inject
class CheckEmptyChatUsecase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(chatRoomId: String) {
        /*chatRepository.*/
    }
}