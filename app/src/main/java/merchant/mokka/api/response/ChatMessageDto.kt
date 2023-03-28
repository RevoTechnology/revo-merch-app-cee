package merchant.mokka.api.response

import java.util.*

abstract class ChatItem(
        val uid: String,
        val createdAt: Date = Date()) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatItem
        return uid == other.uid
    }

    override fun hashCode() = uid.hashCode()
}

class ChatMessageDto(
        val message: String,
        val isUser: Boolean = true,
        val isRead: Boolean = false,
        createdAt: Date = Date(),
        uid: String = UUID.randomUUID().toString()
) : ChatItem(createdAt = createdAt, uid = uid)

class ChatMessageStatus(val online: Boolean = false) : ChatItem(createdAt = Date(), uid = UUID.randomUUID().toString())