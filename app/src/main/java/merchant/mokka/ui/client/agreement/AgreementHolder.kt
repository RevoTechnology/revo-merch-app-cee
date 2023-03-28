package merchant.mokka.ui.client.agreement

import android.view.View
import kotlinx.android.synthetic.main.item_agreement_policy.view.*
import merchant.mokka.common.BaseRecyclerViewHolder
import merchant.mokka.model.DocumentData

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