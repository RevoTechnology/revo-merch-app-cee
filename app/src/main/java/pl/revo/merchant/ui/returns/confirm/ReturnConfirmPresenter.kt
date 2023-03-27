package pl.revo.merchant.ui.returns.confirm

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.R
import pl.revo.merchant.api.error.ApiErr
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.ReturnData
import pl.revo.merchant.ui.root.Screens
import pl.revo.merchant.utils.Constants
import pl.revo.merchant.utils.now
import pl.revo.merchant.utils.toServerFormat
import java.util.concurrent.TimeUnit

@InjectViewState
class ReturnConfirmPresenter(injector: KodeinInjector) : BasePresenter<ReturnConfirmView>(injector) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initTimer()
    }

    private fun initTimer() {
        val time = now()
        Flowable.range(0, Constants.SMS_VERIFY_RETRY_SECONDS * 2)
                .concatMap {
                    Flowable.just(it).delay(Constants.SMS_VERIFY_RETRY_DELAY, TimeUnit.MILLISECONDS)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val seconds = (Constants.SMS_VERIFY_RETRY_SECONDS  + 1) * 1000 - ((now().time - time.time))
                    viewState.showTimeInfo(seconds)
                }
                .doOnComplete{ viewState.showTimeInfo(null) }
                .subscribe()
    }

    fun onNextClick(data: ReturnData, code: String) {
        viewState.showProgress()
        service.createReturn(data.orderId, code, data.returnSum.toServerFormat())
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            viewState.setCodeValid(true)
                            data.returnId = it.returnId.id
                            data.barcode = it.returnId.barcode

                            if (data.returnId == 0) {
                                viewState.showTechnicalError()
                            } else {
                                router.newRootScreen(Screens.RETURN_BARCODE, data)
                            }
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.setCodeValid(false)
                            if (it is ApiErr && it.codeError())
                                viewState.onError(R.string.error_sms_code)
                            else
                                viewState.onError(it)
                        }
                )
    }

    fun sendConfirmCodeAgain(orderId: Int) {
        viewState.showProgress()
        service.sendReturnConfirmationCode(orderId)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            initTimer()
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    fun exitToDashboard() {
        router.newRootScreen(Screens.DASHBOARD)
    }
}