package pl.revo.merchant.ui.purchase.contract_ru

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.R
import pl.revo.merchant.api.MockData
import pl.revo.merchant.api.error.ApiErr
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.FinalizeDto
import pl.revo.merchant.model.FinalizeInputDto
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.model.PolicyDto
import pl.revo.merchant.ui.root.Screens
import pl.revo.merchant.utils.Constants
import pl.revo.merchant.utils.now
import pl.revo.merchant.utils.toText
import java.io.EOFException
import java.util.concurrent.TimeUnit

@InjectViewState
class ContractRuPresenter(injector: KodeinInjector) : BasePresenter<ContractRuView>(injector) {

    private val mockData: MockData by injector.instance()

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

    fun setCodeLayoutVisibility(visibility: Boolean) {
        viewState.setCodeLayoutVisibility(visibility)
    }

    fun onNextClick(loan: LoanData, code: String) {
        viewState.showProgress()
        service.finalizeLoan(
                loanToken = loan.token,
                agreeSmsInfo = loan.smsInfoAgree.toText(),
                agreeProcessing = "1",
                code = code
        ).subscribeBy(
                onSuccess = { finalize ->
                    viewState.hideProgress()
                    viewState.setCodeValid(true)

                    val input = finalize.loanApplication?.finalizeInput ?: FinalizeInputDto()
                    if (finalize.isLamoda) input.type = FinalizeInputDto.Type.NO_INPUT

                    router.newRootScreen(
                            Screens.BARCODE,
                            FinalizeDto(
                                    offerId = finalize.offerId.orEmpty(),
                                    loan = loan,
                                    barcode = finalize.loanApplication?.barcodes ?: listOf(),
                                    input = input,

                                    // specific fields for Lamoda
                                    credentialsLamoda = finalize.credentialsLamoda,
                                    payloadLamoda = finalize.payloadLamoda
                            )
                    )
                },
                onError = {
                    viewState.hideProgress()
                    if (it is ApiErr && it.codeError())
                        viewState.onError(R.string.error_sms_code)
                    else
                        if (it is EOFException) {
                            viewState.setCodeValid(true)
                            router.newRootScreen(
                                    Screens.BARCODE,
                                    FinalizeDto(
                                            offerId = "",
                                            barcode = listOf(),
                                            input = FinalizeInputDto(),
                                            loan = loan
                                    ))
                        } else
                            viewState.onError(it)
                    viewState.setCodeValid(false)
                }
        )
    }

    fun sendCodeAgain(token: String) {
        viewState.showProgress()
        service.sendConfirmClientCode(token)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            viewState.clearCode()
                            initTimer()
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    fun onOfferClick(loan: LoanData, kind: String) {
        router.navigateTo(Screens.POLICY, PolicyDto(R.string.contract_ru_offer_title, loan.token, kind,
                if (service.demo) mockData.getTemplate(kind, loan) else null
        ))
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }

    fun onInsuranceInfoClick(loan: LoanData) {
        router.navigateTo(Screens.PROTECTION_PROGRAM, loan)
    }
}