package pl.revo.merchant.ui.main.select_store

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView
import pl.revo.merchant.model.AgentData

interface SelectStoreView : IBaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setAgentInfo(agentData: AgentData?)

    @StateStrategyType(SkipStrategy::class)
    fun updateRootAgentInfo(agentData: AgentData?, position: Int)

    @StateStrategyType(SkipStrategy::class)
    fun updateDeviceInfo(logStep: String)
}