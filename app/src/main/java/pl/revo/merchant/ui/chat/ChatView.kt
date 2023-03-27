package pl.revo.merchant.ui.chat

import pl.revo.merchant.api.response.ChatMessageDto
import pl.revo.merchant.common.IBaseView

interface ChatView: IBaseView {
    fun onChatMessage(dto: ChatMessageDto)
    fun onMessages(list: List<ChatMessageDto>, hasMore: Boolean, jumpPosition: Int)
    fun receiveTypingMessage(typying: Boolean)
    fun stateOnline(isOnline: Boolean)
    fun onReady()
}