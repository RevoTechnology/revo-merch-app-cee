package pl.revo.merchant.ui.client.profile_bg

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView

interface ClientProfileBgView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProfile()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setCities(cities: List<String>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setStreets(streets: List<String>)
}