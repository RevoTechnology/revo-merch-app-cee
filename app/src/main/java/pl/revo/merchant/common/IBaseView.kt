package pl.revo.merchant.common

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface IBaseView : MvpView {
    fun onFailure()
    fun onMessage(message: String)
    fun onError(error: String)
    fun onError(throwable: Throwable)
    fun onError(errorRes: Int)
    fun showProgress()
    fun hideProgress()
}