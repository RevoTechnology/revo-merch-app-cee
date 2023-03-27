package pl.revo.merchant.ui.login.verify_by_sms

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView

interface VerifyBySmsView : IBaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTimeInfo(time: Long?)
}