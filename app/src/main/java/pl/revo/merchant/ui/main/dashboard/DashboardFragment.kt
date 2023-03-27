package pl.revo.merchant.ui.main.dashboard

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import pl.revo.merchant.Event
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.model.ReportData
import pl.revo.merchant.track
import pl.revo.merchant.utils.dialogs.PeriodPickerFragment

class DashboardFragment : BaseFragment(), DashboardView {

    companion object {
        fun getInstance() = DashboardFragment()
    }

    @InjectPresenter
    lateinit var presenter: DashboardPresenter

    @ProvidePresenter
    fun providePresenter() = DashboardPresenter(injector)

    override val layoutResId = R.layout.fragment_dashboard
    override val titleResId = R.string.dashboard_title
    override val homeIconType = HomeIconType.MENU
    override val toolbarStyle = ToolbarStyle.LIGHT

    private lateinit var adapter: DashboardPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.DASHBOARD.track()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        view.apply {
            adapter = DashboardPagerAdapter(
                    titles = arrayOf(""),
                    items = arrayListOf(),
                    onPeriodClick = { onPeriodClick() }
            )
            dashboardPager.adapter = adapter
            dashboardTabs.setupWithViewPager(dashboardPager)

            dashboardMakeCard.setOnClickListener { presenter.showPurchaseScreen() }
            dashboardReturnCard.setOnClickListener { presenter.showReturnScreen() }
            dashboardChat.setOnClickListener { presenter.showChat(requireActivity()) }
            dashboardCall.setOnClickListener {
                dashboardCallText.text = getString(R.string.dashboard_support_call)
            }

            if (presenter.isDemo())
                dashboardDemo.visibility = View.VISIBLE


            dashboardSelfRegister.setOnClickListener { presenter.sendSelfRegistration() }
        }
    }

    override fun onGetInfo(items: List<ReportData>) {
        adapter.setItems(items)
        adapter.notifyDataSetChanged()
    }

    private fun onPeriodClick() {
        val pickerDialog = PeriodPickerFragment()
        pickerDialog.setCallback { selectedDate -> presenter.getInfoByPeriod(selectedDate) }
        pickerDialog.setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, 0)
        pickerDialog.show(requireActivity().supportFragmentManager, "SUBLIME_PICKER")
    }

    override fun onGetPeriodInfo() {
        activity?.runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onBackPressed(): Boolean {
        alert(
                title = "",
                message = requireContext().getString(R.string.confirm_exit_from_app),
                positive = { activity?.finish() },
                negative = { }
        )
        return true
    }
}