package pl.revo.merchant.ui.client.profile_ru

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_check_profile.view.*
import pl.revo.merchant.R
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.utils.visible

class ClientPersonalDataDialog(private val context: Context,
                               private val loan: LoanData) {
    private fun getView() = LayoutInflater.from(context).inflate(R.layout.dialog_check_profile, null)
            .apply {
                peselContainer.visible(false)
                val lastPassportDigits = with(loan.client?.idDocuments?.russianPassport?.number.orEmpty()) {
                    if (length == 6) substring(length - 4, length)
                    else ""
                }

                profileClientId.text = lastPassportDigits
                profileClientName.text = listOf(
                        loan.client?.firstName.orEmpty().trim(),
                        loan.client?.middleName.orEmpty().trim(),
                        loan.client?.lastName.orEmpty().trim()
                )
                        .filter { t -> t.isNotEmpty() }
                        .let { t -> t.joinToString(separator = " ") }
            }

    fun show(onPositiveClick: (() -> Unit)? = null, onNegativeClick: (() -> Unit)? = null) {
        val view = getView()

        AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(R.string.button_confirm) { _, _ -> onPositiveClick?.invoke() }
                .setNegativeButton(R.string.button_return) { _, _ -> onNegativeClick?.invoke() }
                .show()
    }
}