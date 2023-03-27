package pl.revo.merchant.ui.login.sign_in

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView
import pl.revo.merchant.model.AgentData

interface SignInView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setAgentData(agentData: AgentData)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun confirmUpdate(version: String)
}