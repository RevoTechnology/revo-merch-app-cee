package pl.revo.merchant.ui.purchase.contract

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView
import pl.revo.merchant.model.DocumentData

interface ContractView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(items: List<DocumentData>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTimeInfo(time: Long?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCodeValid(valid: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showInfoClientRepeatNoRcl()
}