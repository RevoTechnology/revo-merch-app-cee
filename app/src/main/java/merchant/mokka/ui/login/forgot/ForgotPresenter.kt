package merchant.mokka.ui.login.forgot

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.api.error.ApiErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.track
import merchant.mokka.ui.root.Screens

@InjectViewState
class ForgotPresenter(injector: KodeinInjector) : BasePresenter<ForgotView>(injector) {

    fun requestCode(login: String) {
        Event.PIN_REQUEST.track()
        viewState.showProgress()
        service.requestSmsCode(login)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            router.navigateTo(Screens.VERIFY_BY_SMS, login)
                        },
                        onError = { throwable ->
                            viewState.hideProgress()
                            if (throwable is ApiErr && throwable.loginOrSmsError())
                                viewState.onError(R.string.error_login)
                            else
                                viewState.onError(throwable)
                        }
                )
    }
}