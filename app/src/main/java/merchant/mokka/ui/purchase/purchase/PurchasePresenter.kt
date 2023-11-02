package merchant.mokka.ui.purchase.purchase

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import io.sentry.Sentry
import merchant.mokka.BuildConfig
import merchant.mokka.R
import merchant.mokka.api.RevoInterceptor
import merchant.mokka.api.error.ApiErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.AutoAgentData
import merchant.mokka.model.LoanData
import merchant.mokka.model.MemoryCashedData
import merchant.mokka.pref.Prefs
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.Constants
import merchant.mokka.utils.clearPhone

@InjectViewState
class PurchasePresenter(injector: KodeinInjector) : BasePresenter<PurchaseView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()
    private val autoAgentData by injector.instance<AutoAgentData>()

    var phone: String = ""
    var valid: Boolean = false
    lateinit var loan: LoanData

    fun isDemo() = service.demo

    fun create() {
        if (autoAgentData.isValid) phone = autoAgentData.phone ?: ""
    }

    fun createLoan(phone: String) {
        viewState.showProgress()
        service.createLoanRequest(phone)
            .subscribeBy(
                onSuccess = {
                    viewState.hideProgress()
                    Sentry.removeExtra("loan_request_id")
                    Sentry.setExtra("loan_request_id", it.token.toString())
                    val sum = if (autoAgentData.isValid) autoAgentData.amount?.toDouble() else null
                    loan = LoanData(
                        token = it.token.orEmpty(),
                        clientPhone = phone,
                        sum = sum ?: 0.0,
                        insuranceAvailable = it.insuranceAvailable ?: false
                    )
                    getClientInfo(loan)
                },
                onError = {
                    viewState.hideProgress()
                    if (it is ApiErr) {
                        when {
                            it.largeAmount() -> viewState.onError(R.string.error_purchase_exceeds)
                            it.phoneError() -> viewState.onError(R.string.error_phone)
                            else -> viewState.onError(it)
                        }
                    } else
                        viewState.onError(it)
                }
            )
    }

    private fun getMinAmount(): String {
        val store = memoryCashedData.agentData?.stores?.getOrNull(Prefs.currentStoreIdx)
        return (store?.store?.tariffMin ?: 0.0).toString()
    }

    private fun getClientInfo(loan: LoanData) {
        service.getClientInfo(loan.token)
            .subscribeBy(
                onSuccess = {
                    loan.client = it
                    sendClientConfirmCode(loan)
                },
                onError = {
                    viewState.hideProgress()
                    loan.isNewClient = true
                    router.navigateTo(Screens.AGREEMENT, loan)
                }
            )
    }

    private fun sendClientConfirmCode(loan: LoanData) {
        service.sendConfirmClientCode(loan.token)
            .subscribeBy(
                onSuccess = {
                    viewState.hideProgress()
                    router.navigateTo(Screens.CONFIRM_CLIENT, loan)
                },
                onError = {
                    viewState.hideProgress()
                    if (it is ApiErr && it.clientError()) {
                        router.navigateTo(Screens.AGREEMENT, loan)
                    } else
                        viewState.onError(it)
                }
            )
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }

    fun showSelfRegistration(phone: String) {
        router.newRootScreen(Screens.SELF_REGISTER, phone)
    }

    fun clickOnLink(link: String) {
        val url = StringBuilder().append(RevoInterceptor.baseUrl(Prefs.stand ?: Constants.STANDS.getByEnv(BuildConfig.ENV)))
        if (::loan.isInitialized) url.append(String.format(link, loan.token))
        else url.append("api/loans/v1/documents/privacy_policy")

        router.navigateTo(Screens.BROWSER, url.toString())
    }
}