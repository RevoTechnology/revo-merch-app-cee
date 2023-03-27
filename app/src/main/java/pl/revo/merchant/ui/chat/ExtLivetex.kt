package pl.revo.merchant.ui.chat

//import livetex.queue_service.DialogState
//import livetex.queue_service.SendMessageResponse
//import livetex.queue_service.TextMessage
//import pl.revo.merchant.api.response.ChatMessageDto
//import sdk.models.LTDialogState
//import sdk.models.LTEmployee
//import sdk.models.LTTextMessage
//import java.util.*
//
//
//fun livetex.queue_service.Destination.string(): String {
//    val result = StringBuilder()
//    result.append("employee=")
//    result.append(trycatch { employee })
//    result.append(", ")
//
//    result.append("department=")
//    result.append(trycatch { department })
//    result.append(", ")
//
//    result.append("isSetDepartment=")
//    result.append(trycatch { isSetDepartment })
//    result.append(", ")
//
//    result.append("isSetEmployee=")
//    result.append(trycatch { isSetEmployee })
//    result.append(", ")
//
//    result.append("isSetTouchPoint=")
//    result.append(trycatch { isSetTouchPoint })
//    result.append(", ")
//
//    result.append("touchPoint=")
//    result.append(trycatch { touchPoint })
//    result.append("\n")
//
//
//    return result.toString()
//}
//
//fun LTTextMessage.string() = "timestamp=$timestamp, sender=$sender, text=$text"
//
//private fun trycatch(callback: () -> Any?) = try {
//    callback()
//} catch (e: java.lang.Exception) {
//    null
//}
//
//
//fun TextMessage.chatMessage(messageId: String) =
//    ChatMessageDto(
//            uid = messageId,
//            message = text.orEmpty(),
//            createdAt = created?.toLong()?.let { Date(it) } ?: Date(),
//            isUser = sender == null
//    )
//
//fun SendMessageResponse?.chatMessage() = with(this) {
//    this ?: return@with null
//    with(attributes.text) { chatMessage(messageId = messageId) }
//}
//
//fun LTTextMessage?.chatMessage() = with(this){
//    this ?: return@with null
//    ChatMessageDto(
//            uid = id,
//            createdAt = Date(timestamp.toLong()),
//            message = text,
//            isUser = false
//    )
//}
//
//fun DialogState?.ltState() = with(this) {
//    this ?: return@with null
//    val ltEmployee = LTEmployee()
//    ltEmployee.lastname = employee?.lastname
//    ltEmployee.firstname = employee?.firstname
//    ltEmployee.status = employee?.status
//
//    val result = LTDialogState()
//    result.employee = ltEmployee
//    result
//}
