package merchant.mokka.ui.chat

//import android.os.Build
//import android.os.Handler
//import com.arellomobile.mvp.InjectViewState
//import com.github.salomonbrys.kodein.KodeinInjector
//import com.github.salomonbrys.kodein.instance
//import livetex.capabilities.Capabilities
//import livetex.queue_service.Destination
//import livetex.queue_service.DialogState
//import livetex.queue_service.Message
//import livetex.queue_service.SendMessageResponse
//import merchant.mokka.App
//import merchant.mokka.api.error.*
//import merchant.mokka.common.BasePresenter
//import merchant.mokka.model.MemoryCashedData
//import merchant.mokka.pref.Prefs
//import sdk.Livetex
//import sdk.handler.AHandler
//import sdk.handler.IInitHandler
//import sdk.models.*
//import java.util.*
//
//private const val AUTH_URL = "https://authentication-service-sdk-production-1.livetex.ru"
//private const val API_KEY = "sdkkey175911"
//private const val APP_ID = "166051"
//private const val LIMIT = 50L

//@InjectViewState
//class ChatPresenter(injector: KodeinInjector) : BasePresenter<ChatView>(injector), IChatPresenter {
//    private lateinit var livetex: Livetex
//    private val handler = Handler()
//    private val resetTypingTask = Runnable { viewState.receiveTypingMessage(false) }
//    private var isStateOnline = false
//    private var offset = 0L
//
//    private val memoryCashedData by injector.instance<MemoryCashedData>()
//    override val username by lazy { memoryCashedData.agentData?.fullName }
//
//    override fun initLivetex() {
//        if (::livetex.isInitialized) return
//
//        livetex = Livetex.Builder(App.instance, API_KEY, APP_ID)
//                .addAuthUrl(AUTH_URL)
//                .addDeviceId(Prefs.pushToken)
//                .addCapabilities(arrayListOf(Capabilities.QUEUE))
//                .addToken(Prefs.livetexToken)
//                .build()
//
//        livetex.init(object : IInitHandler {
//            override fun onSuccess(token: String?) {
//                Prefs.livetexToken = token
//                destination()
//
//                state()
//                notificationCallbacks(livetex)
//
//                viewState.onReady()
//            }
//
//            override fun onError(p0: String?) {
//                viewState.onError(LivetexConnectionError(p0))
//            }
//        })
//    }
//
//    private fun destination(onComplete: (() -> Unit)? = null) = livetex.getDestinations(object : AHandler<ArrayList<Destination>> {
//        override fun onResultRecieved(destinations: ArrayList<Destination>?) {
//            val destination = destinations?.firstOrNull { it.isSetDepartment }
//                    ?: run {
//                        viewState.onError(LivetexConnectionError())
//                        return
//                    }
//
//            val os = try {
//                Build.VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name
//            } catch (e: Exception) {
//                Build.VERSION.SDK_INT.toString()
//            }
//
//            val map = hashMapOf(
//                    "Android version" to "Android $os ${Build.VERSION.SDK_INT}",
//                    "Device" to "${Build.MANUFACTURER} ${Build.BRAND} ${Build.MODEL}",
//                    "Language" to Locale.getDefault().displayLanguage
//            )
//
//            livetex.setName(username)
//            livetex.setDestination(destination, LTDialogAttributes(map))
//
//            onComplete?.invoke()
//        }
//
//        override fun onError(p0: String?) {
//            viewState.onError(LivetexDestinationError(p0))
//        }
//    })
//
//    private fun state() = livetex.getState(object : AHandler<DialogState> {
//        override fun onResultRecieved(state: DialogState?) {
//            onStateChanged(state.ltState())
//        }
//
//        override fun onError(p0: String?) {
//            viewState.onError(LivetexStateError(p0))
//            viewState.stateOnline(isOnline = false)
//        }
//    })
//
//    override fun history() = livetex.getLastMessages(offset, LIMIT, object : AHandler<LTSerializableHolder> {
//        override fun onResultRecieved(holder: LTSerializableHolder?) {
//            (holder?.serializable as? List<*>)
//                    ?.filterIsInstance(Message::class.java)
//                    ?.map { item ->
//                        with(item.attributes.text) { chatMessage(messageId = item.messageId) }
//                    }
//                    ?.let {
//                        val pOffset = offset
//                        offset += it.size.toLong()
//
//                        val jumpPosition = if (pOffset == 0L) 0 else offset - it.size - 1
//                        viewState.onMessages(list = it, hasMore = it.isNotEmpty(), jumpPosition = jumpPosition.toInt())
//
//                    }
//        }
//
//        override fun onError(p0: String?) {
//            viewState.onError(LivetexHistoryError(p0))
//        }
//    })
//
//    private fun notificationCallbacks(livetex: Livetex) {
//        livetex.setNotificationDialogHandler(object : LivetexNotification() {
//            override fun receiveTextMessage(item: LTTextMessage?) {
//                val message = item.chatMessage() ?: return
//                viewState.onChatMessage(message)
//            }
//
//            override fun receiveTypingMessage(typing: LTTypingMessage?) {
//                viewState.receiveTypingMessage(true)
//                handler.removeCallbacks(resetTypingTask)
//                handler.postDelayed(resetTypingTask, 1_500)
//            }
//
//            override fun updateDialogState(state: LTDialogState?) = onStateChanged(state)
//            override fun onError(p0: String?) = viewState.onError(LivetexDialogError(p0))
//        })
//    }
//
//    private fun onStateChanged(state: LTDialogState?) {
//        isStateOnline = state?.employee?.status == "1"
//        viewState.stateOnline(isOnline = isStateOnline)
//    }
//
//    override fun send(text: String) {
//        if (!isStateOnline)
//            destination {
//                isStateOnline = true
//                state()
//                send(text)
//            }
//        else
//            livetex.sendTextMessage(text, object : AHandler<SendMessageResponse> {
//                override fun onResultRecieved(item: SendMessageResponse?) {
//                    val message = item.chatMessage() ?: return
//                    viewState.onChatMessage(message)
//                }
//
//                override fun onError(p0: String?) {
//                    viewState.onError(LivetexSendTextError(p0))
//                }
//            })
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        runCatching { livetex.destroy() }
//    }
//
//    fun onPause() {
//        runCatching { livetex.destroy() }
//    }
//
//    fun onResume() {
//        runCatching {
//            if (::livetex.isInitialized) livetex.bindService()
//            else initLivetex()
//        }
//    }
//}