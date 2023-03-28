package merchant.mokka.ui.chat

import merchant.mokka.api.response.ChatMessageDto
import merchant.mokka.common.IBaseView

interface ChatView: IBaseView {
    fun onChatMessage(dto: ChatMessageDto)
    fun onMessages(list: List<ChatMessageDto>, hasMore: Boolean, jumpPosition: Int)
    fun receiveTypingMessage(typying: Boolean)
    fun stateOnline(isOnline: Boolean)
    fun onReady()
}