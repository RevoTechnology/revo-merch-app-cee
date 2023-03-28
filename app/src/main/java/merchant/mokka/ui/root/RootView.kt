package merchant.mokka.ui.root

import android.net.Uri
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

interface RootView : MvpView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onError(throwable: Throwable)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun onError(errorRes: Int)

    @StateStrategyType(SkipStrategy::class)
    fun showProgress()

    @StateStrategyType(SkipStrategy::class)
    fun hideProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setRootFrameVisibility(visibility: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun confirmUpdate(version: String)

    @StateStrategyType(SkipStrategy::class)
    fun showCompleteUpdate(filePath: Uri)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showToolbar()
}