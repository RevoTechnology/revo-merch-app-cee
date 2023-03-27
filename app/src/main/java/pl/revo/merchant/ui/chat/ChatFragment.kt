package pl.revo.merchant.ui.chat

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_chat.*
import pl.revo.merchant.BuildConfig
import pl.revo.merchant.R
import pl.revo.merchant.api.error.*
import pl.revo.merchant.api.response.ChatMessageDto
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.utils.enable
import pl.revo.merchant.utils.visible
import pl.revo.merchant.widget.EditTextValidator
import pl.revo.merchant.widget.attachValidator
import pl.revo.merchant.widget.detachValidator

class ChatFragment : BaseFragment(), ChatView {
    companion object {
        fun getInstance() = ChatFragment()
    }

    override val layoutResId = R.layout.fragment_chat
    override val titleResId = R.string.chat_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.LIGHT

    @InjectPresenter
    lateinit var presenter: ChatPresenter2

    @ProvidePresenter
    fun providePresenter() = ChatPresenter2(injector)

    private lateinit var messageValidator: EditTextValidator
    private val chatAdapter by lazy {
        ChatAdapter(
                username = presenter.username,
                onLoadNext = {
                    presenter.history()
                })
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setSubtitle(R.string.chat_status_online)
        initValidators()
        createForm()
        createAdapters()

        presenter.initLivetex()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        input.attachValidator(messageValidator, inputLayout)
    }

    override fun onPause() {
        input.detachValidator(messageValidator)
        presenter.onPause()
        super.onPause()
    }

    // View initialization
    private fun initValidators() {
        messageValidator = EditTextValidator(
                validator = { text -> text.isNotEmpty() },
                onChangedState = {
                    buttonSubmit.enable(enabled = it, alpha = 1.0f)
                    buttonSubmit.setImageResource(
                            if (it) R.drawable.ic_chat_send_active else R.drawable.ic_chat_send_disable)
                }
        )
    }

    private fun createForm() {
        buttonSubmit.setOnClickListener {
            presenter.send(text = input.text.toString())
            input.setText("")
        }
    }

    private fun createAdapters() = recyclerview.apply {
        adapter = chatAdapter
        itemAnimator = null
    }

    private fun showError(message: String?) {
        input.enable(false)
        recyclerview.visible(false)
        connectionErrorView.visible(true)
        message?.let { errorTextview.text = message }
        errorTextview.visible(!message.isNullOrEmpty() && !BuildConfig.IS_PROD)
    }

    // ChatView
    override fun onError(throwable: Throwable) {
        input.enable(false)
        recyclerview.visible(false)

        when (throwable) {
            is LivetexConnectionError, is NetworkAvailableErr, is LivetexDestinationError, is LivetexHistoryError ->
                showError(message = throwable.message)

            is LivetexStateError, is LivetexDialogError, is LivetexSendTextError ->
                Toast.makeText(requireActivity(), throwable.message, Toast.LENGTH_LONG).show()

            else -> super.onError(throwable)
        }
    }

    override fun onChatMessage(dto: ChatMessageDto) {
        chatAdapter.update(dto)
        recyclerview?.scrollToPosition(0)
    }

    override fun onMessages(list: List<ChatMessageDto>, hasMore: Boolean, jumpPosition: Int) {
        chatAdapter.insert(list = list, hasMore = hasMore)
        recyclerview?.scrollToPosition(jumpPosition)
    }

    override fun receiveTypingMessage(typying: Boolean) {
        val text = if (typying) R.string.chat_status_typying else R.string.chat_status_online
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setSubtitle(text)
    }

    override fun stateOnline(isOnline: Boolean) {
        val text = if (isOnline) R.string.chat_status_online else R.string.chat_status_offline
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.setSubtitle(text)

        if (!isOnline) chatAdapter.status(isOnline = false)
    }

    override fun onReady() {
        Handler().postDelayed({ presenter.history() }, 2_000)
    }
}