package pl.revo.merchant.ui.purchase.barcode

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.Event
import pl.revo.merchant.api.request.LamodaCredentialsRes
import pl.revo.merchant.api.request.LamodaLoanPayloadRes
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.AutoAgentData
import pl.revo.merchant.model.FinalizeInputDto
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.repository.LamodaRepository
import pl.revo.merchant.track
import pl.revo.merchant.ui.root.Screens

@InjectViewState
class BarcodePresenter(injector: KodeinInjector) : BasePresenter<BarcodeView>(injector) {
    private val autoAgentData by injector.instance<AutoAgentData>()
    private val lamodaRepository = LamodaRepository()

    fun isDemo() = service.demo
    fun onFinish(credentials: LamodaCredentialsRes? = null, payload: LamodaLoanPayloadRes? = null) {
        if (autoAgentData.isValid) {
            try {
                val result = lamodaRepository.result(lamoda = autoAgentData, credentials = credentials, payload = payload)
                viewState.lamodaResult(result!!)
            } catch (e: Exception) {
                Event.RETURN_MAIN.track()
                router.newRootScreen(Screens.DASHBOARD)
            }

        } else {
            Event.RETURN_MAIN.track()
            router.newRootScreen(Screens.DASHBOARD)
        }
    }

    fun finalize(loan: LoanData,
                 type: FinalizeInputDto.Type,
                 code: String?,
                 credentials: LamodaCredentialsRes?,
                 payload: LamodaLoanPayloadRes?) {
        if (type == FinalizeInputDto.Type.INPUT) bill(loan = loan, code = code)
        else onFinish(credentials = credentials, payload = payload)
    }

    fun bill(loan: LoanData, code: String?) {
        viewState.showProgress()
        service.bill(loanToken = loan.token, code = code)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            onFinish()
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }
}