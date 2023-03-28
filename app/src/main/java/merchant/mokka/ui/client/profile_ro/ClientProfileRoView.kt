package merchant.mokka.ui.client.profile_ro

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView

interface ClientProfileRoView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProfile()
}