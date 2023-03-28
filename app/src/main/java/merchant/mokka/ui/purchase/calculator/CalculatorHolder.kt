package merchant.mokka.ui.purchase.calculator

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_calculator.view.*
import kotlinx.android.synthetic.main.item_loan_pay.view.*
import merchant.mokka.R
import merchant.mokka.common.BaseRecyclerViewHolder
import merchant.mokka.model.TariffData
import merchant.mokka.utils.*
import merchant.mokka.utils.*

class CalculatorHolder(
        val view: View,
        val inflater: LayoutInflater
) : BaseRecyclerViewHolder<TariffData>(view) {

    @SuppressLint("InflateParams")
    override fun bindView(
        data: TariffData,
        onItemClick: ((item: TariffData, position: Int) -> Unit)?
    ) {
        val context = view.context
        val colorWhite = ContextCompat.getColor(context, R.color.white)
        val colorAccent = ContextCompat.getColor(context, R.color.colorAccent)
        val colorBlack = ContextCompat.getColor(context, R.color.black)
        val colorSteel = ContextCompat.getColor(context, R.color.steel)

        with(view) {
            when {
                data.currentSum < data.minAmount || (data.currentSum > data.maxAmount && data.maxAmount > 0.0) -> {
                    data.selected = false
                    data.expanded = false

                    loanHeaderLayout.setBackgroundColor(colorWhite)
                    loanHeaderImg.setImageResource(R.drawable.ic_radio_off)
                    loanHeader.setTextColor(colorSteel)
                    loanMonthPay.setTextColor(colorSteel)
                    loanMonthPay.alpha = 0.6f

                    loanHeaderImg.visibility = View.INVISIBLE
                }
                data.selected -> {
                    loanHeaderLayout.setBackgroundColor(colorAccent)
                    loanHeaderImg.setImageResource(R.drawable.ic_radio_on_white)
                    loanHeader.setTextColor(colorWhite)
                    loanMonthPay.setTextColor(colorWhite)
                    loanMonthPay.alpha = 0.6f

                    loanHeaderImg.visibility = View.VISIBLE
                }
                !data.selected -> {
                    loanHeaderLayout.setBackgroundColor(colorWhite)
                    loanHeaderImg.setImageResource(R.drawable.ic_radio_off)
                    loanHeader.setTextColor(colorBlack)
                    loanMonthPay.setTextColor(colorSteel)
                    loanMonthPay.alpha = 1.0f

                    loanHeaderImg.visibility = View.VISIBLE
                }
            }

            loanSumLayout.visible(data.sumWithDiscount != data.totalOfPayments)
            totalOverpaymentContainer.visible(true)
            totalOverpaymentDivider.visible(totalOverpaymentContainer.isVisible())

            when (data.expanded) {
                true -> loanDetailLayout.visibility = View.VISIBLE
                false -> loanDetailLayout.visibility = View.GONE
            }

            data.bnpl?.also { bnpl ->
                loanHeader.text = String.format(context.getString(R.string.bnpl_term_title), bnpl.term)
                loanMonthPay.text = String.format(
                        context.getString(R.string.bnpl_sum_with_discount),
                        data.sumWithDiscount.toText()
                )
                loanTotalPayment.text = data.sumWithDiscount.toTextWithCent()
                loanOverPayment.text = bnpl.commission.orZero().toTextWithCent()
                loanSum.text = data.sumWithDiscount.toText()

                loanSumLayout.visible(true)
                bnplLayout.visible(true)

                loanPaymentsBnplLayout.removeAllViews()
                data.schedule.firstOrNull()?.also {
                    val payView = inflater.inflate(R.layout.item_loan_pay, null)
                    with(payView) {
                        payDate.text = now().addDay(bnpl.term).toText(DateFormats.SIMPLE_FORMAT)
                        paySum.text = data.sumWithDiscount.toTextWithCent()
                        payDivider.visibility = View.GONE
                    }

                    loanPaymentsBnplLayout.addView(payView)
                }

                optionPayTextView.text = String.format(context.getString(R.string.option_pay_installments), data.schedule.size)

            } ?: run {
                loanHeader.text = String.format(context.getString(R.string.calc_loan_period), data.term)
                loanMonthPay.text = String.format(
                        context.getString(R.string.calc_loan_pay),
                        data.monthlyPayment.toText()
                )
                loanTotalPayment.text = data.totalOfPayments.toTextWithCent()
                loanOverPayment.text = data.totalOverpayment.toTextWithCent()
            }


            loanSum.text = data.sumWithDiscount.toTextWithCent()

            loanPaymentsLayout.removeAllViews()

            (data.schedule.indices).forEach {
                val payView = inflater.inflate(R.layout.item_loan_pay, null)
                with(payView) {
                    payDate.text = data.schedule[it].date.toText(DateFormats.SIMPLE_FORMAT)
                    paySum.text = data.schedule[it].amount.toTextWithCent()
                    if (it == data.schedule.size - 1)
                        payDivider.visibility = View.GONE
                    else
                        payDivider.visibility = View.VISIBLE
                }
                loanPaymentsLayout.addView(payView)
            }

            data.bnpl?.also { bnpl ->
                val textView = TextView(context)
                textView.text = String.format(context.getString(R.string.option_pay_installments), bnpl.term)
                loanPaymentsLayout.addView(View(context), 1)
            }

            setOnClickListener {
                data.selected = !data.selected
                data.expanded = !data.expanded
                onItemClick?.invoke(data, adapterPosition)
            }
        }
    }
}