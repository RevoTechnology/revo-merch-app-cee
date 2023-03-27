package pl.revo.merchant.ui.client.agreement

import android.view.View
import kotlinx.android.synthetic.main.item_agreement_policy.view.*
import pl.revo.merchant.common.BaseRecyclerViewHolder
import pl.revo.merchant.model.DocumentData

class AgreementHolder(
        val view: View,
        private val onCheckChanged: () -> Unit
) : BaseRecyclerViewHolder<DocumentData>(view) {

    override fun bindView(
            data: DocumentData,
            onItemClick: ((item: DocumentData, position: Int) -> Unit)?)
    {
        with(view) {
            agreementName.text = data.name
            contractRuAgreeCheck.isChecked = data.checked
            setOnClickListener { onItemClick?.invoke(data, adapterPosition) }
            contractRuAgreeCheck.setOnClickListener {
                data.checked = contractRuAgreeCheck.isChecked
                onCheckChanged.invoke()
            }
        }
    }
}