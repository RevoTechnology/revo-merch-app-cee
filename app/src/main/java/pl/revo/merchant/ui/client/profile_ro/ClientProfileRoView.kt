package pl.revo.merchant.ui.client.profile_ro

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView

interface ClientProfileRoView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProfile()
}