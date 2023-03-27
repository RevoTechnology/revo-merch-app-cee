package pl.revo.merchant.ui.main.select_store

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_select_store.view.*
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseRecyclerViewHolder
import pl.revo.merchant.model.StoreData

class SelectStoreHolder(private val view: View) : BaseRecyclerViewHolder<StoreData>(view) {

    override fun bindView(data: StoreData, onItemClick: ((item: StoreData, position: Int) -> Unit)?) {
        with(view) {
            val spanable = SpannableString(data.name + "  " + data.traderName)
            spanable.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    0,
                    data.name.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spanable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent)),
                    data.name.length,
                    (data.name + "  " + data.traderName).length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            selectStoreName.text = spanable
            selectStoreAddress.text = data.address
            setOnClickListener { onItemClick?.invoke(data, adapterPosition) }
        }
    }
}