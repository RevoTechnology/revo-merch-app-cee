package merchant.mokka.ui.client.confirm_client

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.R
import merchant.mokka.api.error.ApiErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.LoanData
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.Constants
import merchant.mokka.utils.isRuLocale
import merchant.mokka.utils.now
import java.util.concurrent.TimeUnit

@InjectViewState
class ConfirmClientPresenter(injector: KodeinInjector) : BasePresenter<ConfirmClientView>(injector) {

    fun isDemo() = service.demo

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
                    val seconds = (Constants.SMS_VERIFY_RETRY_SECONDS + 1) * 1000 - ((now().time - time.time))
                    viewState.showTimeInfo(seconds)
                }
                .doOnComplete { viewState.showTimeInfo(null) }
                .subscribe()
    }

    fun onNextClick(loan: LoanData, code: String) {
        if (loan.isNewClient) createNewClient(loan, code)
        else confirmClient(loan, code)
    }

    private fun createNewClient(loan: LoanData, code: String) {
        loan.client?.let { client ->
            viewState.showProgress()
            service.createClient(loan.token, loan.clientPhone, client, loan.agrees, code)
                    .subscribeBy(
                            onSuccess = {
                                loan.client = it
                                checkLoan(loan, true)
                            },
                            onError = {
                                viewState.hideProgress()

                                when {
                                    it is ApiErr && (it.invalidClientData() || it.invalidClientDocument()) -> viewState.onError(it)
                                    it is ApiErr -> viewState.onError(it.message.orEmpty())
                                    else -> viewState.onError(it)
                                }
                            }
                    )
        }
    }

    private fun confirmClient(loan: LoanData, code: String) {
        viewState.showProgress()
        service.confirmClient(loan.token, code)
                .subscribeBy(
                        onSuccess = {
                            loan.client?.apply {
                                creditDecision = it.decision
                                creditLimit = it.creditLimit
                                decisionMessage = it.decisionMessage
                                decisionCode = it.decisionCode
                            }

                            checkLoan(loan, it.kycPassed)
                        },
                        onError = {
                            viewState.hideProgress()

                            when {
                                it is ApiErr && it.codeError() -> viewState.onError(R.string.error_sms_code)
                                else -> viewState.onError(it)
                            }

                            viewState.setCodeValid(false)
                        }
                )
    }

    private fun checkLoan(loan: LoanData, kycPassed: Boolean?) {
        loan.client?.let {
            when {
                !it.approved -> {
                    viewState.hideProgress()
                    router.newRootScreen(Screens.DECLINED, it.decisionMessage)
                }
                it.decisionCode == 220 -> {
                    viewState.hideProgress()
                    router.newRootScreen(Screens.DECLINED, it.decisionMessage)
                }
                kycPassed == null && isRuLocale() -> getTariffInformation(loan)
                kycPassed != true -> {
                    viewState.hideProgress()
                    router.navigateTo(Screens.CLIENT_PROFILE, loan)
                }
                else -> getTariffInformation(loan)
            }
        }
    }

    private fun getTariffInformation(loan: LoanData) {
        viewState.showClientInfo(loan.client)

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

    fun showDocuments(loan: LoanData) {
        router.newScreenChain(Screens.DOCUMENTS, loan)
    }

    fun showClientProfile(loan: LoanData) {
        router.newScreenChain(Screens.CLIENT_PROFILE, loan)
    }

    fun sendCodeAgain(token: String) {
        viewState.showProgress()
        service.sendConfirmClientCode(token)
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

}