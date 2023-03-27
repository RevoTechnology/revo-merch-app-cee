package pl.revo.merchant.ui.purchase.self_register

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import io.sentry.Sentry
import pl.revo.merchant.Event
import pl.revo.merchant.R
import pl.revo.merchant.api.adapter.MoshiUtils
import pl.revo.merchant.api.response.ClientRes
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.model.MemoryCashedData
import pl.revo.merchant.pref.Prefs
import pl.revo.merchant.track
import pl.revo.merchant.ui.root.Screens
import pl.revo.merchant.utils.clearPhone
import pl.revo.merchant.utils.isRoLocale
import pl.revo.merchant.utils.isRuLocale

@InjectViewState
class SelfRegisterPresenter(injector: KodeinInjector) : BasePresenter<SelfRegisterView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()

    var phone: String? = null
    var valid: Boolean = false
    lateinit var loan: LoanData

    fun isDemo() = service.demo

    fun createLoan() {
        if (this::loan.isInitialized) {
            sendSelfRegistration(loan)
        } else {
            viewState.showProgress()
            service.createLoanRequest()
                    .subscribeBy(
                            onSuccess = {
                                loan = LoanData(
                                    token = it.token.orEmpty(),
                                    clientPhone = phone?.clearPhone().orEmpty()
                                )
                                Sentry.removeExtra("loan_request_id")
                                Sentry.setExtra("loan_request_id", it.toString())
                                updateLoan()
                            },
                            onError = {
                                viewState.hideProgress()
                                viewState.onError(it)
                            }
                    )
        }
    }

    private fun updateLoan() {
        service.updateLoanRequest(
                loan.token,
                loan.clientPhone,
                if (isRuLocale()) getMinAmount() else null,
                agreeInsurance = null
        )
                .subscribeBy(
                        onSuccess = {
                            sendSelfRegistration(loan)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    private fun getMinAmount(): String {
        val store = memoryCashedData.agentData?.stores?.getOrNull(Prefs.currentStoreIdx)
        return (store?.store?.tariffMin ?: 0.0).toString()
    }

    private fun sendSelfRegistration(loan: LoanData) {
        try {
            if (isRoLocale() && loan.clientPhone[1] != '0') {
                viewState.onError(R.string.error_phone)
                return
            }
        } catch (e: Throwable) {
        }

        service.selfRegistration(loan.token, loan.clientPhone)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            when {
                                it.isSuccessful -> {
                                    Event.SELF_REG_SUCCESS.track()
                                    viewState.onSuccess()
                                }

                                it.code() == 201 -> {
                                    Event.SELF_REG_SUCCESS.track()
                                    viewState.onSuccess()
                                }

                                it.code() == 422 -> {
                                    val body = it.errorBody()?.string()

                                    if (body != null) {
                                        val moshi = MoshiUtils.moshi().build()
                                        val jsonAdapter = moshi.adapter<ClientRes>(ClientRes::class.java)
                                        val clientData = jsonAdapter.fromJson(body)
                                        if (clientData?.client != null) {
                                            Event.SELF_REG_REPEAT.track()
                                            loan.client = clientData.client
                                            viewState.onRetryClient()
                                        }
                                    } else {
                                        viewState.hideProgress()
                                    }
                                }

                                else -> {
                                    viewState.hideProgress()
                                }
                            }
                        },
                        onError = {
                            Event.SELF_REG_ERROR.track()
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }

    fun showConfirmScreen() {
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
}