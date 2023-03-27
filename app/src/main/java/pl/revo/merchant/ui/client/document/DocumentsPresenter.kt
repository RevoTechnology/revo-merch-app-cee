package pl.revo.merchant.ui.client.document

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.R
import pl.revo.merchant.api.error.LargeDataError
import pl.revo.merchant.api.error.NetworkAvailableErr
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.ui.root.Screens
import pl.revo.merchant.utils.isRuLocale

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