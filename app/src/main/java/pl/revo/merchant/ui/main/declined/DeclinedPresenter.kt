package pl.revo.merchant.ui.main.declined

import com.github.salomonbrys.kodein.KodeinInjector
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.ui.root.Screens

class DeclinedPresenter(injector: KodeinInjector) : BasePresenter<DeclinedView>(injector) {

    fun goToDashboard() {
        router.newRootScreen(Screens.DASHBOARD)
    }
}