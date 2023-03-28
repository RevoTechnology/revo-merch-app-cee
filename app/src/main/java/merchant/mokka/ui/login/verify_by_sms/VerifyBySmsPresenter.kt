package merchant.mokka.ui.login.verify_by_sms

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.R
import merchant.mokka.api.error.ApiErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.VerifySmsData
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.Constants
import merchant.mokka.utils.isValidSmsCode
import merchant.mokka.utils.now
import java.util.concurrent.TimeUnit

@InjectViewState
class VerifyBySmsPresenter(injector: KodeinInjector) : BasePresenter<VerifyBySmsView>(injector) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initTimer()
    }

    fun sendCodeAgain(login: String) {
        viewState.showProgress()
        service.requestSmsCode(login)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            initTimer()
                        },
                        onError = { throwable ->
                            viewState.hideProgress()
                            viewState.onError(throwable)
                        }
                )
    }

    private fun initTimer() {
        val time = now()
        Flowable.range(0, Constants.SMS_VERIFY_RETRY_SECONDS * 2)
                .concatMap {
                    Flowable.just(it).delay(Constants.SMS_VERIFY_RETRY_DELAY, TimeUnit.MILLISECONDS)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({
                    val seconds = (Constants.SMS_VERIFY_RETRY_SECONDS  + 1) * 1000 - ((now().time - time.time))
                    viewState.showTimeInfo(seconds)
                })
                .doOnComplete{ viewState.showTimeInfo(null) }
                .subscribe()
    }

    fun onNext(login: String, code: String) {
        if (login.isNotEmpty() && code.isValidSmsCode()) {
            viewState.showProgress()
            service.checkSmsCode(login, code)
                    .subscribeBy(
                            onSuccess = { _ ->
                                viewState.hideProgress()
                                router.navigateTo(Screens.SIGN_UP, VerifySmsData(login, code))
                            },
                            onError = { throwable ->
                                viewState.hideProgress()
                                if (throwable is ApiErr && throwable.loginOrSmsError())
                                    viewState.onError(R.string.error_sms_code)
                                else
                                    router.navigateTo(Screens.SIGN_UP, VerifySmsData(login, code))
                            }
                    )
        }
    }
}