package merchant.mokka.ui.login.sign_in

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView
import merchant.mokka.model.AgentData

interface SignInView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setAgentData(agentData: AgentData)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun confirmUpdate(version: String)
}