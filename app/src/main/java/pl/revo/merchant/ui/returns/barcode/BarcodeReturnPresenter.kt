package pl.revo.merchant.ui.returns.barcode

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.ReturnData
import pl.revo.merchant.ui.root.Screens
import pl.revo.merchant.utils.orZero

@InjectViewState
class BarcodeReturnPresenter(injector: KodeinInjector) : BasePresenter<BarcodeReturnView>(injector) {

    fun confirmReturn(data: ReturnData) {
        viewState.showProgress()
        service.confirmReturn(data.returnId.orZero())
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            router.newRootScreen(Screens.DASHBOARD)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    fun cancelReturn(data: ReturnData) {
        viewState.showProgress()
        service.cancelReturn(data.returnId.orZero())
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            router.newRootScreen(Screens.DASHBOARD)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }
}