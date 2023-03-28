package merchant.mokka.ui.returns.detail

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.ReturnData
import merchant.mokka.ui.root.Screens

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