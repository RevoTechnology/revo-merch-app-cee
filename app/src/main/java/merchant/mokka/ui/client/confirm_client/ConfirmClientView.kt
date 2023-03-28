package merchant.mokka.ui.client.confirm_client

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView
import merchant.mokka.model.ClientData

interface ConfirmClientView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTimeInfo(time: Long?)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCodeValid(valid: Boolean)

    fun showClientInfo(client: ClientData?)
}