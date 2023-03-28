package merchant.mokka.ui.client.document

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.R
import merchant.mokka.api.error.LargeDataError
import merchant.mokka.api.error.NetworkAvailableErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.LoanData
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.isRuLocale

@InjectViewState
class DocumentsPresenter(injector: KodeinInjector) : BasePresenter<DocumentsView>(injector) {

    fun onNextClick(loan: LoanData) {
        val passed = loan.clientIds.nameImage != null || loan.clientIds.clientWithPassportImage != null || loan.clientIds.livingAddressImage != null
        if (passed) {
            viewState.showProgress()
            service.updateClientDocuments(loan.token, loan.clientIds)
                    .subscribeBy(
                            onSuccess = {
                                viewState.hideProgress()
                                val nextScreen = when{
                                    isRuLocale() -> Screens.CONTRACT_RU
                                    else -> Screens.CONTRACT
                                }

                                router.navigateTo(nextScreen, loan)
                            },
                            onError = {
                                viewState.hideProgress()
                                when {
                                    it is LargeDataError -> viewState.onError(R.string.error_large_data)
                                    it.message?.toLowerCase()?.contains("i/o error during system call") == true ||
                                            it.message?.toLowerCase()?.contains("software caused connection abort") == true ||
                                            it.message?.toLowerCase()?.contains("unable to resolve host") == true ||
                                            it.message?.toLowerCase()?.contains("no address associated with hostname") == true ->
                                        viewState.onError(NetworkAvailableErr())
                                    else -> viewState.onError(it)
                                }
                            }
                    )
        }
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }
}