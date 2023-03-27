package pl.revo.merchant.ui.login.unlock

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.BuildConfig
import pl.revo.merchant.R
import pl.revo.merchant.api.error.NetworkAvailableErr
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.MemoryCashedData
import pl.revo.merchant.utils.isNewVersion

@InjectViewState
class UnlockPresenter(injector: KodeinInjector) : BasePresenter<UnlockView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setUserName( memoryCashedData.agentData?.firstName.orEmpty())
    }

    fun onUnlock(pin: String) {
        viewState.showProgress()
        service.unlock(pin)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            if (it) {
                                loadApkVersion()
                            } else
                                viewState.onErrorUnlock()
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onErrorUnlock()
                            viewState.onError(R.string.error_login_or_password)
                        }
                )
    }

    private fun loadApkVersion() {
        service.loadApkVersion()
                .subscribeBy(
                        onSuccess = {
                            memoryCashedData.remoteVersion = it
                            if (it.isNewVersion())
                                viewState.confirmUpdate(it)
                            else
                                viewState.onSuccessUnlock()
                        },
                        onError = {
                            if (it is NetworkAvailableErr)
                                viewState.onError(it)
                            else
                                memoryCashedData.remoteVersion = BuildConfig.VERSION_NAME
                        }
                )
    }

    fun loadApkFile() {
        viewState.showProgress()
        service.loadApkFile()
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            viewState.showCompleteUpdate(it)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onSuccessUnlock()
                        }
                )
    }
}