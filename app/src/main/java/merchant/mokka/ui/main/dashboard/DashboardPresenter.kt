package merchant.mokka.ui.main.dashboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.R
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.AutoAgentData
import merchant.mokka.model.MemoryCashedData
import merchant.mokka.model.ReportData
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.*
import merchant.mokka.utils.*
import java.util.*

@InjectViewState
class DashboardPresenter(injector: KodeinInjector) : BasePresenter<DashboardView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()
    private val reportList = mutableListOf<ReportData>()
    private val autoAgentData by injector.instance<AutoAgentData>()

    fun isDemo() = memoryCashedData.demo

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (reportList.isEmpty()) {
            viewState.showProgress()
            val current = now()
            service.getAgentReport(
                    current.beginOfMonth().toText(DateFormats.SERVER_FORMAT),
                    current.endOfMonth().toText(DateFormats.SERVER_FORMAT)
            )
                    .subscribeBy(
                            onSuccess = {
                                reportList.add(it)
                                //getPrevDate(current)
                                viewState.hideProgress()
                                viewState.onGetInfo(reportList)
                            },
                            onError = {
                                getPrevDate(current)
                            }
                    )
        }
    }

    private fun getPrevDate(date: Date) {
        val current = date.addMonth(-1)
        service.getAgentReport(
                current.beginOfMonth().toText(DateFormats.SERVER_FORMAT),
                current.endOfMonth().toText(DateFormats.SERVER_FORMAT)
        )
                .subscribeBy(
                        onSuccess = {
                            reportList.add(it)
                            reportList.add(ReportData(null, null, null, null))
                            viewState.hideProgress()
                            viewState.onGetInfo(reportList)
                        },
                        onError = {
                            viewState.hideProgress()
                        }
                )
    }

    fun getInfoByPeriod(selectedDate: SelectedDate?) {
        val from = selectedDate?.firstDate?.time
        val to = selectedDate?.endDate?.time

        if (from != null && to != null) {
            viewState.showProgress()
            service.getAgentReport(from.toText(DateFormats.SERVER_FORMAT),
                    to.toText(DateFormats.SERVER_FORMAT))
                    .subscribeBy(
                            onSuccess = {
                                if (reportList.size == 2)
                                    reportList.add(it)
                                else
                                    reportList[2] = it
                                viewState.hideProgress()
                                viewState.onGetPeriodInfo()
                            }
                    )
        }
    }

    fun showPurchaseScreen() = router.newRootScreen(Screens.PURCHASE)
    fun showReturnScreen() = router.newRootScreen(Screens.SEARCH)
    fun sendSelfRegistration() = router.newRootScreen(Screens.SELF_REGISTER, "")

    fun showChat(activity: Activity) = activity.apply {
        if (isRuLocale()) router.newScreenChain(Screens.CHAT, "")
        else
            Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.dashboard_chat_link))).run { startActivity(this) }
    }

}