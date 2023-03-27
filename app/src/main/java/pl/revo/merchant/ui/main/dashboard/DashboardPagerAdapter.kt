package pl.revo.merchant.ui.main.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.page_dashboard.view.*
import pl.revo.merchant.R
import pl.revo.merchant.model.ReportData
import pl.revo.merchant.utils.toText

class DashboardPagerAdapter(
        private val titles: Array<String>,
        private var items: List<ReportData>,
        val onPeriodClick: (() -> Unit)? = null
) : androidx.viewpager.widget.PagerAdapter() {

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view == any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
                    .inflate(R.layout.page_dashboard, container, false)
        with(view) {
            if (position == 2) {
                dashboardPeriod.setOnClickListener { onPeriodClick?.invoke() }
                dashboardPeriodArrow.visibility = View.VISIBLE
            } else {
                dashboardPeriodArrow.visibility = View.GONE
            }

            val periodName = items[position].periodName ?: context.getString(R.string.dashboard_select_period)
            dashboardPeriodText.text = String.format(
                    context.getString(R.string.dashboard_period), periodName
            )
            dashboardLoans.text = items[position].loansCount?.toString() ?: "0"
            dashboardAmount.text = items[position].amount?.toText() ?: "0"
        }

        container.addView(view)
        return view
    }

    override fun getCount() = items.size

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun setItems(newItems: List<ReportData>) {
        items = newItems
    }
}