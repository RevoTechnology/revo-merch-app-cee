package merchant.mokka.ui.returns.barcode

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.ReturnData
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.orZero

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