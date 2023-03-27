package pl.revo.merchant.ui.returns.detail

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.ReturnData
import pl.revo.merchant.ui.root.Screens

@InjectViewState
class DetailPresenter(injector: KodeinInjector) : BasePresenter<DetailView>(injector) {

    fun sendConfirmCode(returnData: ReturnData) {
        viewState.showProgress()
        service.sendReturnConfirmationCode(returnData.orderId)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            router.navigateTo(Screens.RETURN_CONFIRM, returnData)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }
}