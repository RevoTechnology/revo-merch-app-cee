package merchant.mokka.ui.client.agreement

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import merchant.mokka.api.MockData
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.DocumentData
import merchant.mokka.model.GdprAcceptance
import merchant.mokka.model.LoanData
import merchant.mokka.model.PolicyDto
import merchant.mokka.ui.root.Screens

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