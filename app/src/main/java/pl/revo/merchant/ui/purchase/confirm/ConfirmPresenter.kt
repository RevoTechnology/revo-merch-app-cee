package pl.revo.merchant.ui.purchase.confirm

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.ui.root.Screens

@InjectViewState
class ConfirmPresenter(
        injector: KodeinInjector
) : BasePresenter<ConfirmView>(injector) {

    fun onNextClick(loan: LoanData) {
        viewState.showProgress()
        service.sendConfirmClientCode(loan.token)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            router.navigateTo(Screens.CONTRACT, loan)
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