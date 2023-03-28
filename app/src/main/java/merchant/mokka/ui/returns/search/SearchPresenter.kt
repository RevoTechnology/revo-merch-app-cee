package merchant.mokka.ui.returns.search

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.SearchData
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.clearPhone

@InjectViewState
class SearchPresenter(injector: KodeinInjector) : BasePresenter<SearchView>(injector) {

    fun isDemo() = service.demo

    fun searchPurchases(login: String, passport: String, contract: String?) {
        var clearLogin: String? = login.clearPhone()
        if (clearLogin?.isEmpty() == true)
            clearLogin = null
        val clearPassport = passport.ifEmpty { null }

        viewState.showProgress()
        service.getOrderList(clearLogin, clearPassport, null, contract?.ifEmpty { null })
                .subscribeBy(
                        onSuccess = { items ->
                            viewState.hideProgress()
                            viewState.setData(items)
                        },
                        onError = {
                            viewState.hideProgress()
                        }
                )
    }

    fun showDashboardScreen() {
        router.newRootScreen(Screens.DASHBOARD)
    }

    fun makeReturn(data: SearchData) {
        router.navigateTo(Screens.DETAIL, data)
    }
}