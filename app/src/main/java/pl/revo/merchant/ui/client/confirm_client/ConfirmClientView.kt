package pl.revo.merchant.ui.client.confirm_client

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView
import pl.revo.merchant.model.ClientData

interface ConfirmClientView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTimeInfo(time: Long?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCodeValid(valid: Boolean)

    fun showClientInfo(client: ClientData?)
}