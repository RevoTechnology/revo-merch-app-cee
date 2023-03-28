package merchant.mokka.ui.main.declined

import com.github.salomonbrys.kodein.KodeinInjector
import merchant.mokka.common.BasePresenter
import merchant.mokka.ui.root.Screens

class DeclinedPresenter(injector: KodeinInjector) : BasePresenter<DeclinedView>(injector) {

    fun goToDashboard() {
        router.newRootScreen(Screens.DASHBOARD)
    }
}