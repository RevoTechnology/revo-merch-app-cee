package pl.revo.merchant.ui.main.dashboard

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView
import pl.revo.merchant.model.ReportData

interface DashboardView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onGetInfo(items: List<ReportData>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onGetPeriodInfo()
}