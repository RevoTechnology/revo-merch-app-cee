package merchant.mokka.ui.purchase.self_register

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView

interface SelfRegisterView : IBaseView {
    fun onSuccess()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun onRetryClient()
}