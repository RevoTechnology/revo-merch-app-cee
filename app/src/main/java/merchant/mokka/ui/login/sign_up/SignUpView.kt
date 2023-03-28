package merchant.mokka.ui.login.sign_up

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView
import merchant.mokka.model.AgentData

interface SignUpView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setAgentData(agentData: AgentData)
}