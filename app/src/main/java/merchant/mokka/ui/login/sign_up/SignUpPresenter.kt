package merchant.mokka.ui.login.sign_up

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import io.sentry.Sentry
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.VerifySmsData
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.isValidPinCode

@InjectViewState
class SignUpPresenter(injector: KodeinInjector) : BasePresenter<SignUpView>(injector) {

    fun signUpNewPin(verifySmsData: VerifySmsData, pin: String) {
        if (verifySmsData.login.isNotEmpty() && verifySmsData.confirmationCode.isNotEmpty() && pin.isValidPinCode()) {
            viewState.showProgress()
            service.signUpNewPin(verifySmsData.login, verifySmsData.confirmationCode, pin)
                    .subscribeBy(
                            onSuccess = { getAgentInfo() },
                            onError = { throwable ->
                                viewState.hideProgress()
                                viewState.onError(throwable)
                            }
                    )
        }
    }

    private fun getAgentInfo() {
        service.getAgentInfo()
                .subscribeBy(
                        onSuccess = { it ->
                            viewState.hideProgress()
                            if (it.stores.size > 1)
                                router.newRootScreen(Screens.SELECT_STORE)
                            else {
                                viewState.setAgentData(it)
                                it.stores.getOrNull(0)?.store?.id?.let {
                                    Sentry.setExtra("store_id", it.toString())
                                }
                                router.newRootScreen(Screens.DASHBOARD)
                            }
                        },
                        onError = { throwable ->
                            viewState.hideProgress()
                            viewState.onError(throwable)
                        }
                )
    }
}