package pl.revo.merchant.ui.returns.confirm

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView

interface ReturnConfirmView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTimeInfo(time: Long?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCodeValid(valid: Boolean)

    fun showTechnicalError()
}