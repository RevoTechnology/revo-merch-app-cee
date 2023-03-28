package merchant.mokka.ui.purchase.contract

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.R
import merchant.mokka.api.MockData
import merchant.mokka.api.error.ApiErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.DocumentData
import merchant.mokka.model.DocumentKind
import merchant.mokka.model.FinalizeDto
import merchant.mokka.model.FinalizeInputDto
import merchant.mokka.model.LoanData
import merchant.mokka.model.PolicyDto
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.Constants
import merchant.mokka.utils.isRoLocale
import merchant.mokka.utils.now
import merchant.mokka.utils.toText
import java.io.EOFException
import java.util.concurrent.TimeUnit

@InjectViewState
class ContractPresenter(injector: KodeinInjector) : BasePresenter<ContractView>(injector) {
    private val mockData: MockData by injector.instance()
    private var isInfoClientRepeatNoRclShown = false

    var documents: MutableList<DocumentData>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initTimer()
    }

    fun onItemClick(titleResId: Int, loan: LoanData, kind: String) {
        router.navigateTo(
            Screens.POLICY, PolicyDto(titleResId, loan.token, kind, null
//                if (service.demo) mockData.getTemplate(kind, loan)
//                else null
        )
        )
    }

    fun onNextClick(loan: LoanData, code: String) {
        viewState.showProgress()

        var agreeInfo = "1"

        documents?.forEach {
            when (it.kind) {
                DocumentKind.INDIVIDUAL -> agreeInfo = it.checked.toText()
                else -> {
                }
            }
        }

        service.finalizeLoan(
                loanToken = loan.token,
                agreeSmsInfo = agreeInfo,
                agreeProcessing = agreeInfo,
                code = code
        ).subscribeBy(
                onSuccess = {
                    viewState.hideProgress()
                    viewState.setCodeValid(true)
                    router.newRootScreen(
                        Screens.BARCODE,
                            FinalizeDto(
                                    offerId = it.offerId.orEmpty(),
                                    barcode = it.loanApplication?.barcodes ?: listOf(),
                                    input = FinalizeInputDto(),
                                    loan = loan
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
                                    )
                            )
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
                            initTimer()
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
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
                .doOnNext {
                    val seconds = (Constants.SMS_VERIFY_RETRY_SECONDS + 1) * 1000 - ((now().time - time.time))
                    viewState.showTimeInfo(seconds)
                }
                .doOnComplete { viewState.showTimeInfo(null) }
                .subscribe()
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }

    fun showInfoClientRepeatNoRcl(loan: LoanData) {
        if (isRoLocale() && !isInfoClientRepeatNoRclShown && loan.client?.isRepeated == true && loan.client?.rclAccepted != true) {
            isInfoClientRepeatNoRclShown = true
            viewState.showInfoClientRepeatNoRcl()
        }
    }
}