package merchant.mokka.ui.login.verify_by_sms

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView

interface VerifyBySmsView : IBaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTimeInfo(time: Long?)
}