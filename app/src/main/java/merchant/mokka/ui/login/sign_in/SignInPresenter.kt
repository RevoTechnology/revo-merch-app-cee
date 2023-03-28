package merchant.mokka.ui.login.sign_in

import com.arellomobile.mvp.InjectViewState
import com.exponea.sdk.Exponea
import com.exponea.sdk.models.CustomerIds
import com.exponea.sdk.models.PropertiesList
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import io.sentry.Sentry
import merchant.mokka.BuildConfig
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.api.error.ApiErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.AutoAgentData
import merchant.mokka.model.DocumentData
import merchant.mokka.model.MemoryCashedData
import merchant.mokka.Repository.LamodaRepository
import merchant.mokka.track
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.clearPhone
import merchant.mokka.utils.isEmail
import merchant.mokka.utils.isNewVersion

@InjectViewState
class SignInPresenter(injector: KodeinInjector) : BasePresenter<SignInView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()
    private val autoAgentData by injector.instance<AutoAgentData>()
    private val lamodaRepository = LamodaRepository()

    fun create() {
        if (lamodaRepository.tokenValid) {
            viewState.showProgress()
            getAgentInfo()
        }
    }

    fun signIn(login: String, pinCode: String) {
        if (memoryCashedData.remoteVersion?.isNewVersion() == true) {
            viewState.confirmUpdate(memoryCashedData.remoteVersion.orEmpty())
        } else {
            Event.LOGIN.track()
            viewState.showProgress()
            service
                    .signIn(
                            login = if (login.isEmail()) login else login.clearPhone(),
                            pin = pinCode
                    ).subscribeBy(
                            onSuccess = { getAgentInfo() },
                            onError = { throwable ->
                                viewState.hideProgress()
                                if (throwable is ApiErr && throwable.loginOrPasswordError())
                                    viewState.onError(R.string.error_login_or_password)
                                else
                                    viewState.onError(throwable)
                            }
                    )
        }
    }

    private fun getAgentInfo() {
        service.getAgentInfo()
                .subscribeBy(
                        onSuccess = { agentData ->
                            Exponea.identifyCustomer(
                                    customerIds = CustomerIds().withId("registered", "-${agentData.id}"),
                                    properties = PropertiesList(hashMapOf("is_prod" to BuildConfig.IS_PROD))
                            )

                            viewState.hideProgress()

                            // set user's default store if it was passed from Lamoda or other merchant apps
                            // remove the other ones from agentData
                            if (autoAgentData.isValid && agentData.stores.any { it.store.id == autoAgentData.storeId })
                                agentData.stores.removeAll { it.store.id != autoAgentData.storeId }

                            val defaultStore = if (agentData.stores.size == 1) agentData.stores.getOrNull(0)?.store
                            else null

                            defaultStore?.let { store ->
                                viewState.setAgentData(agentData)
                                Sentry.setExtra("store_id", store.id.toString())
                                Sentry.setExtra("trader_id", store.name)
                            }

                            val screen = when {
                                agentData.stores.size > 1 -> Screens.SELECT_STORE
                                autoAgentData.isValid -> Screens.PURCHASE
                                else -> Screens.DASHBOARD
                            }

                            router.newRootScreen(screen)
                        },
                        onError = { throwable ->
                            viewState.hideProgress()
                            viewState.onError(throwable)
                        }
                )
    }

    fun showForgotScreen(login: String) {
        router.navigateTo(Screens.FORGOT, login)
    }

    fun showPolicy(data: DocumentData) {
        router.navigateTo(Screens.POLICY, data)
    }

    fun setDemo() {
        service.demo = true
        signIn("", "")
    }
}