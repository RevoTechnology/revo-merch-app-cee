package pl.revo.merchant.ui.chat

import com.github.salomonbrys.kodein.KodeinInjector
import pl.revo.merchant.common.BasePresenter

class ChatPresenter2(injector: KodeinInjector) : BasePresenter<ChatView>(injector), IChatPresenter {
    override val username: String? = null

    override fun initLivetex() {}
    override fun history() {}
    override fun send(text: String) {}
    override fun onResume() {}
    override fun onPause() {}
}