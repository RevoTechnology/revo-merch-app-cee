package pl.revo.merchant.ui.purchase.self_register

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView

interface SelfRegisterView : IBaseView {
    fun onSuccess()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun onRetryClient()
}