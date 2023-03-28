package merchant.mokka.ui.purchase.contract_ru

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView

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