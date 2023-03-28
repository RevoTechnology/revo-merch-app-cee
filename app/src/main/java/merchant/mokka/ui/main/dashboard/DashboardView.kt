package merchant.mokka.ui.main.dashboard

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView
import merchant.mokka.model.ReportData

interface DashboardView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onGetInfo(items: List<ReportData>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onGetPeriodInfo()
}