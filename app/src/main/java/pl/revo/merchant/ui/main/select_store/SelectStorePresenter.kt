package pl.revo.merchant.ui.main.select_store

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import io.sentry.Sentry
import pl.revo.merchant.Event
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.AgentData
import pl.revo.merchant.model.DeviceInfoData
import pl.revo.merchant.model.MemoryCashedData
import pl.revo.merchant.track
import pl.revo.merchant.ui.root.Screens

@InjectViewState
class SelectStorePresenter(injector: KodeinInjector) : BasePresenter<SelectStoreView>(injector) {

    private var agentInfo: AgentData? = null
    private val memoryCashedData by injector.instance<MemoryCashedData>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        agentInfo = memoryCashedData.agentData
        viewState.setAgentInfo(agentInfo)
    }

    fun onItemClick(position: Int) {
        viewState.updateRootAgentInfo(agentInfo, position)
        viewState.updateDeviceInfo("after_choosing_store")
        agentInfo?.stores?.getOrNull(position)?.store?.let {
            Event.STORE_SELECTION.track(
                properties = hashMapOf(
                    "store_id" to it.id,
                    "trader_id" to it.name
                )
            )
            Sentry.setExtra("store_id", it.id.toString())
            Sentry.setExtra("trader_id", it.name)
        }
//        router.newRootScreen(Screens.DASHBOARD)
    }

    fun deviceLogs(data: DeviceInfoData?) {
        if (data != null) {
            viewState.showProgress()
            service.deviceLogs(data.uuid, data.info)
                    .subscribeBy(
                            onSuccess = {
                                viewState.hideProgress()
                                router.newRootScreen(Screens.DASHBOARD)
                            },
                            onError = {
                                viewState.hideProgress()
                                router.newRootScreen(Screens.DASHBOARD)
                            }
                    )
        } else {
            viewState.hideProgress()
            router.newRootScreen(Screens.DASHBOARD)
        }
    }

    fun onBackClick() {
        viewState.showProgress()
        service.signOut()
                .subscribeBy(
                        onSuccess = {
                            viewState.hideProgress()
                            router.newRootScreen(Screens.SIGN_IN)
                        },
                        onError = {
                            viewState.hideProgress()
                            viewState.onError(it)
                        }
                )
    }
}