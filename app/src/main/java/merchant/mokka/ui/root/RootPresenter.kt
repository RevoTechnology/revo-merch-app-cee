package merchant.mokka.ui.root

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.BuildConfig
import merchant.mokka.R
import merchant.mokka.api.error.NetworkAvailableErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.*
import merchant.mokka.Repository.LamodaRepository
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isNewVersion
import merchant.mokka.utils.isPlLocale
import merchant.mokka.utils.isRoLocale

@InjectViewState
class RootPresenter(injector: KodeinInjector) : BasePresenter<RootView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()
    private val autoAgentData by injector.instance<AutoAgentData>()

    private val lamodaRepository = LamodaRepository()

    override fun onDestroy() {
        autoAgentData.update(forceUpdate = true)
    }

    // get an extra string from intent with key "json_data"
    fun handleLamoda(data: String?) {
        autoAgentData.update()
        if (BuildConfig.DEBUG) lamodaRepository.test(autoAgentData = autoAgentData)

        data ?: return
        try {
            lamodaRepository.parse(body = data, autoAgentData = autoAgentData)
        } catch (e: Exception) {
        }
    }

    fun checkApkVersion(deviceInfoData: DeviceInfoData?) {
        if (isPlLocale() || isRoLocale() || isBgLocale()) {
            showSignInScreen()
            service.loadApkVersion()
                    .subscribeBy(
                            onSuccess = {
                                memoryCashedData.remoteVersion = it
                                if (it.isNewVersion())
                                    viewState.confirmUpdate(it)
                            },
                            onError = {
                                if (it is NetworkAvailableErr)
                                    viewState.onError(it)
                                else
                                    memoryCashedData.remoteVersion = BuildConfig.VERSION_NAME
                            }
                    )
        } else {
            deviceLogs(deviceInfoData)
        }
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
                            viewState.onError(R.string.update_not_download)
                        }
                )
    }

    fun onBackCommandClick(): Boolean {
        router.exit()
        return true
    }

    fun onMakePurchaseClick() {
        viewState.showProgress()
        service.createLoanRequest()
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            router.newRootScreen(Screens.PURCHASE, LoanData(token = it.token.orEmpty()))
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }

    fun onSignOutClick() {
        service.signOut()
                .subscribeBy(
                        onSuccess = {
                            showSignInScreen()
                        },
                        onError = {
                            showSignInScreen()
                            viewState.setRootFrameVisibility(true)
                            //viewState.onError(it)
                        }
                )
    }

    fun onDashboardClick() {
        router.newRootScreen(Screens.DASHBOARD)
    }

    fun onMakeReturnClick() {
        router.newRootScreen(Screens.SEARCH)
    }

    fun setFrameVisibility(visible: Boolean) {
        viewState.setRootFrameVisibility(visible)
    }

    fun deviceLogs(data: DeviceInfoData?) {
        if (data != null) {
            viewState.showProgress()
            service.deviceLogs(data.uuid, data.info)
                    .subscribeBy(
                            onSuccess = { getDeviceSpecific(data.uuid) },
                            onError = { showSignInScreen() }
                    )
        } else {
            showSignInScreen()
        }
    }

    private fun showSignInScreen() {
        viewState.showToolbar()
        viewState.hideProgress()
        router.newRootScreen(Screens.SIGN_IN)
    }

    private fun getDeviceSpecific(deviceId: String) {
        service.getDevice(deviceId)
                .subscribeBy(
                        onSuccess = { getDeviceUpdate(deviceId, it.installationMessage) },
                        onError = { showSignInScreen() }
                )
    }

    private fun getDeviceUpdate(deviceId: String, installationMessage: String) {
        service.getDeviceUpdate(deviceId)
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            if (it.apkVersion.isNewVersion() && it.apkUrl.isNotEmpty()) {
                                router.newRootScreen(
                                    Screens.NEW_VERSION,
                                        UpdateData(it.apkUrl, installationMessage)
                                )
                            } else {
                                showSignInScreen()
                            }
                        },
                        onError = {
                            showSignInScreen()
                        }
                )
    }
}