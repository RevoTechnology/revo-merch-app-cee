package pl.revo.merchant.ui.client.profile_ro

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.api.MockData
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.model.MemoryCashedData
import pl.revo.merchant.model.PolicyDto
import pl.revo.merchant.ui.root.Screens

@InjectViewState
class ClientProfileRoPresenter(injector: KodeinInjector) : BasePresenter<ClientProfileRoView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()
    private val mockData: MockData by injector.instance()
    private val predefinedCounties = "AX,TR,AR,XC,ZC,MM,XM,XB,XT,BV,ZV,XR,DP,DR,DT,DX,RD,RR,RT,RX,RK,IF,XZ,KL,KX,CJKT,KZDX,DZHD,VN,GL," +
            "ZL,GG,MX,MZ,MH,HR,XH,ZH,NT,AS,AZ,PH,PX,KS,VX,SM,KV,SB,OT,SZ,SV,XV,TM,TZ,DD,GZ,ZS,MS,TC,VS,SX".split(",")

    fun isDemo() = memoryCashedData.demo

    fun onNextClick(loan: LoanData) {
        viewState.showProgress()
        if (loan.isNewClient) confirmClient(loan)
        else getTariffInformation(loan)
    }

    private fun confirmClient(loan: LoanData) {
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

    private fun getTariffInformation(loan: LoanData) {
        service.getTariffInfo(loan)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            loan.tariffs = it.tariffs
                            router.newRootScreen(Screens.CALCULATOR, loan)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }

    fun onAgreeClick(titleResId: Int, loan: LoanData, kind: String) {
        router.navigateTo(Screens.POLICY, PolicyDto(titleResId, loan.token, kind,
                if (service.demo) mockData.getTemplate(kind, loan) else null
        ))
    }

    fun isCountyValid(text: String) = text.length == 8 && predefinedCounties.contains(text.take(2), ignoreCase = true)
}