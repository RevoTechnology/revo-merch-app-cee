package pl.revo.merchant.ui.purchase.contract

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.item_contract.view.*
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseRecyclerViewHolder
import pl.revo.merchant.model.ClientData
import pl.revo.merchant.model.DocumentData
import pl.revo.merchant.model.DocumentKind
import pl.revo.merchant.utils.isBgLocale
import pl.revo.merchant.utils.isPlLocale
import pl.revo.merchant.utils.isRoLocale
import pl.revo.merchant.utils.visible

class ContractHolder(
        val view: View,
        private val client: ClientData?,
        private val onCheckChanged: () -> Unit
) : BaseRecyclerViewHolder<DocumentData>(view) {

    override fun bindView(
            data: DocumentData,
            onItemClick: ((item: DocumentData, position: Int) -> Unit)?) {
        with(view) {

            if (data.checkable) {
                contractCheck.visibility = View.VISIBLE
                contractCheck.isChecked = data.checked
                contractCheck.setOnClickListener {
                    data.checked = contractCheck.isChecked
                    onCheckChanged.invoke()
                }
                contractName.visibility = View.GONE
                contractInfoImage.visibility = View.GONE

                showText(view = contractCheck, data = data)
            } else {
                contractCheck.visibility = View.GONE
                contractName.visibility = View.VISIBLE
                contractInfoImage.visibility = View.VISIBLE

                showText(view = contractName, data = data)
            }

            linkImageView.visible(data.kind != DocumentKind.NONE)
            if (data.kind == DocumentKind.NONE) linkImageView.setOnClickListener(null)
            else linkImageView.setOnClickListener { onItemClick?.invoke(data, adapterPosition) }
        }
    }

    private fun showText(view: TextView, data: DocumentData) {
        when {
            isPlLocale() -> showPoland(view, data)
            isRoLocale() -> showRomania(view, data)
            isBgLocale() -> showRomania(view, data)
        }
    }

    private fun showRomania(view: TextView, data: DocumentData) {
        view.text = data.name
    }

    private fun showPoland(view: TextView, data: DocumentData) {
        val checkable = view is CheckBox
        view.text = when {
            // checkable field
            client?.isNewClient == true && checkable -> R.string.confirm_read_loan_info_checkable
            client?.rclAccepted == true && checkable -> R.string.confirm_read_loan_info_rcl_accepted_checkable
            client?.isRepeated == true && !client.rclAccepted && data.kind == DocumentKind.INDIVIDUAL && checkable ->
                R.string.confirm_read_loan_info_repeated_not_rcl_accepted_kind_individual_agreement
            client?.isRepeated == true && !client.rclAccepted && data.kind == DocumentKind.RCL && checkable ->
                R.string.confirm_read_loan_info_repeated_not_rcl_accepted_kind_rcl

            // not checkable field
            client?.isNewClient == true && !checkable -> R.string.confirm_read_loan_info
            client?.rclAccepted == true && !checkable -> R.string.confirm_read_loan_info_rcl_accepted
            client?.isRepeated == true && !client.rclAccepted && !checkable -> R.string.confirm_read_loan_info_repeated_not_rcl_accepted

            else -> null
        }?.let { view.context.getString(it) } ?: data.name
    }

}