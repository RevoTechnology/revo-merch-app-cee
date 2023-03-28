package merchant.mokka.ui.purchase.barcode

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.Event
import merchant.mokka.api.request.LamodaCredentialsRes
import merchant.mokka.api.request.LamodaLoanPayloadRes
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.AutoAgentData
import merchant.mokka.model.FinalizeInputDto
import merchant.mokka.model.LoanData
import merchant.mokka.Repository.LamodaRepository
import merchant.mokka.track
import merchant.mokka.ui.root.Screens

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