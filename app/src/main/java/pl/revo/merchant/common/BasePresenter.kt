package pl.revo.merchant.common

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import pl.revo.merchant.api.ApiService
import ru.terrakok.cicerone.Router

abstract class BasePresenter<T: MvpView>(injector: KodeinInjector) : MvpPresenter<T>() {

    protected val service: ApiService by injector.instance()
    protected val router: Router by injector.instance()
}