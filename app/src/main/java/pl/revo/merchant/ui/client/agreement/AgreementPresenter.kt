package pl.revo.merchant.ui.client.agreement

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import pl.revo.merchant.api.MockData
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.DocumentData
import pl.revo.merchant.model.GdprAcceptance
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.model.PolicyDto
import pl.revo.merchant.ui.root.Screens

@InjectViewState
class AgreementPresenter(injector: KodeinInjector) : BasePresenter<AgreementView>(injector) {

    private val mockData: MockData by injector.instance()

    var documents: MutableList<DocumentData>? = null
    var agrees: GdprAcceptance? = null

    fun onItemClick(titleResId: Int, loan: LoanData, kind: String) {
        val policy = PolicyDto(titleResId, loan.token, kind,
            if (service.demo && false) mockData.getTemplate(kind, loan) else null
        )
        router.navigateTo(Screens.POLICY, policy)
    }

    fun onNextClick(loan: LoanData) {
        router.navigateTo(Screens.CLIENT_PROFILE, loan)
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }
}