package pl.revo.merchant.ui.updater.new_version

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.R
import pl.revo.merchant.common.BasePresenter

@InjectViewState
class NewVersionPresenter(injector: KodeinInjector) : BasePresenter<NewVersionView>(injector) {

    fun loadApkFile(apkUri: String) {
        viewState.showProgress()
        service.loadNatashaApkFile(apkUri)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            viewState.installUpdate(it)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(R.string.update_not_download)
                        }
                )
    }
}