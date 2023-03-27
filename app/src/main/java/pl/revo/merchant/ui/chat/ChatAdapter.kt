package pl.revo.merchant.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chat_header.view.textView
import kotlinx.android.synthetic.main.item_chat_user.view.*
import pl.revo.merchant.R
import pl.revo.merchant.api.response.ChatItem
import pl.revo.merchant.api.response.ChatMessageDto
import pl.revo.merchant.api.response.ChatMessageStatus
import pl.revo.merchant.utils.visible
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(val username: String?,
                  var hasMore: Boolean = false,
                  var onLoadNext: () -> Unit,
                  private var items: MutableList<ChatItem> = mutableListOf()) : RecyclerView.Adapter<ChatHolder>() {

    enum class ViewType(@LayoutRes val res: Int, val viewType: Int) {
        Header(res = R.layout.item_chat_header, viewType = 0),
        Support(res = R.layout.item_chat_support, viewType = 1),
        User(res = R.layout.item_chat_user, viewType = 2),
        Status(res = R.layout.item_chat_status, viewType = 3),
        Unknown(res = R.layout.item_chat_user, viewType = 4),
        Loading(res = R.layout.item_chat_loadung, viewType = 5)
    }

    private val countOffset
        get() = 1 + (if (hasMore) 1 else 0)

    override fun getItemCount() = items.size + countOffset
    override fun getItemViewType(position: Int): Int {
        when (position) {
            itemCount - 1 -> ViewType.Header.viewType
            itemCount - 2 -> if (hasMore) ViewType.Loading.viewType else null
            else -> null
        }?.let { return it }


        val item = item(position)
        return when {
            (item as? ChatMessageDto)?.isUser == true -> ViewType.User.viewType
            (item as? ChatMessageDto)?.isUser == false -> ViewType.Support.viewType
            item is ChatMessageStatus -> ViewType.Status.viewType
            else -> ViewType.Unknown.viewType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val view = type(viewType).res
                .let { LayoutInflater.from(parent.context).inflate(it, parent, false) }

        return ChatHolder(view)
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        when (type(getItemViewType(position))) {
            ViewType.Header -> holder.header(username = username)
            ViewType.Support -> holder.support(item(position) as ChatMessageDto)
            ViewType.User -> holder.user(item(position) as ChatMessageDto)
            ViewType.Status -> holder.status(item(position) as ChatMessageStatus)
            ViewType.Loading -> onLoadNext()
        }
    }

    private fun itemPosition(position: Int) = itemCount - (position + countOffset + 1)
    private fun item(position: Int) = items[itemPosition(position)]
    private fun type(viewType: Int) = ViewType.values().first { it.viewType == viewType }

    fun update(item: ChatMessageDto) {
        items.indexOfFirst { it == item }
                .let {
                    if (it >= 0) {
                        items[itemPosition(it)] = item
                        notifyItemChanged(it)
                    } else {
                        items.add(item)
                        notifyItemInserted(itemPosition(items.size - 1))
                    }
                }

    }

    fun insert(list: List<ChatItem>, hasMore: Boolean) {
        this.hasMore = hasMore
        val diff = DiffUtil.calculateDiff(ChatDiff(oldList = this.items, newList = list))
        diff.dispatchUpdatesTo(this)

        val result = mutableSetOf<ChatItem>()
        result.addAll(this.items)
        result.addAll(list)

        this.items = result.sortedBy { it.createdAt }.toMutableList()
    }

    fun status(isOnline: Boolean) {
//        items.add(ChatMessageStatus(online = isOnline))
//        notifyItemInserted(itemPosition(items.size - 1))
    }
}

private class ChatDiff(
        private val oldList: List<Any>,
        private val newList: List<Any>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

}

class ChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun header(username: String?) = itemView.apply {
        textView.text = String.format(context.getString(R.string.chat_greetings), username)
    }

    fun support(item: ChatMessageDto) = itemView.apply {
        textView.text = item.message
        timeView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(item.createdAt)
    }

    fun user(item: ChatMessageDto) = itemView.apply {
        textView.text = item.message
        timeView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(item.createdAt)
        readIndicator.visible(item.isRead)
    }

    fun status(status: ChatMessageStatus) = itemView.apply {
        if (!status.online) textView.setText(R.string.chat_operator_gone)
        else visible(false)
    }
}