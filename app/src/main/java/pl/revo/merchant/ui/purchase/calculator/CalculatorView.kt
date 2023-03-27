package pl.revo.merchant.ui.purchase.calculator

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.api.response.TariffClientSmsInfoData
import pl.revo.merchant.common.IBaseView
import pl.revo.merchant.model.TariffData

interface CalculatorView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(tariffData: List<TariffData>?, clientSmsInfoData: TariffClientSmsInfoData?)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun refreshSum()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun lockSumAndRefresh()
}