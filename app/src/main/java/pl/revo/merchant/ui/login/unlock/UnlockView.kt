package pl.revo.merchant.ui.login.unlock

import android.net.Uri
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface UnlockView : MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setUserName(userName: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onErrorUnlock()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onSuccessUnlock()

    @StateStrategyType(SkipStrategy::class)
    fun showProgress()

    @StateStrategyType(SkipStrategy::class)
    fun hideProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onError(throwable: Throwable)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onError(errorRes: Int)

    @StateStrategyType(SkipStrategy::class)
    fun confirmUpdate(version: String)

    @StateStrategyType(SkipStrategy::class)
    fun showCompleteUpdate(filePath: Uri)
}