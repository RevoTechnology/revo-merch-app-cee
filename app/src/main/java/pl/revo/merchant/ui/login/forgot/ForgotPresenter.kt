package pl.revo.merchant.ui.login.forgot

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.Event
import pl.revo.merchant.R
import pl.revo.merchant.api.error.ApiErr
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.track
import pl.revo.merchant.ui.root.Screens

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