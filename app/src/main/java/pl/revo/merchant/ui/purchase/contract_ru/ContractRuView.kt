package pl.revo.merchant.ui.purchase.contract_ru

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView

interface ContractRuView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCodeLayoutVisibility(visibility: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTimeInfo(time: Long?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCodeValid(valid: Boolean)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun clearCode()
}