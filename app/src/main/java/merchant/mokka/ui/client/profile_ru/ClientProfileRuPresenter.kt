package merchant.mokka.ui.client.profile_ru

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.api.MockData
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.LoanData
import merchant.mokka.model.MemoryCashedData
import merchant.mokka.model.PolicyDto
import merchant.mokka.ui.root.Screens

@InjectViewState
class ClientProfileRuPresenter(injector: KodeinInjector) : BasePresenter<ClientProfileRuView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()
    private val mockData: MockData by injector.instance()

    fun isDemo() = memoryCashedData.demo

    fun onNextClick(loan: LoanData) {
        viewState.showProgress()
        if (loan.isNewClient) confirmClient(loan)
        else getTariffInformation(loan)
    }

    private fun confirmClient(loan: LoanData) {
        service.sendConfirmClientCode(loan.token)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            router.navigateTo(Screens.CONFIRM_CLIENT, loan)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    private fun getTariffInformation (loan: LoanData) {
        service.getTariffInfo(loan)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            loan.tariffs = it.tariffs
                            router.newRootScreen(Screens.CALCULATOR, loan)
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

    fun onAgreeClick(titleResId: Int, loan: LoanData, kind: String) {
        router.navigateTo(
            Screens.POLICY, PolicyDto(titleResId, loan.token, kind,
                if (service.demo) mockData.getTemplate(kind, loan) else null
        )
        )
    }
}