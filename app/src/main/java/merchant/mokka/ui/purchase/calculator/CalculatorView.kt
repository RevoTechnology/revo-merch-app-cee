package merchant.mokka.ui.purchase.calculator

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.api.response.TariffClientSmsInfoData
import merchant.mokka.common.IBaseView
import merchant.mokka.model.TariffData

interface CalculatorView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(tariffData: List<TariffData>?, clientSmsInfoData: TariffClientSmsInfoData?)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun refreshSum()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun lockSumAndRefresh()
}