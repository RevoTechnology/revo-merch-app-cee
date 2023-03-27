package pl.revo.merchant.ui.client.profile

import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.model.AddressData
import pl.revo.merchant.model.LoanData
import pl.revo.merchant.model.MemoryCashedData
import pl.revo.merchant.ui.root.Screens

@InjectViewState
class ClientProfilePresenter(injector: KodeinInjector) : BasePresenter<ClientProfileView>(injector) {

    private val memoryCashedData by injector.instance<MemoryCashedData>()

    private var addressList = listOf<AddressData>()

    fun isDemo() = memoryCashedData.demo

    fun loadAddressByPostalCode(postalCode: String) {
        service.getAddressByPostalCode(postalCode)
                .subscribeBy(
                        onSuccess = {
                            addressList = it
                            viewState.setCities(getCities())
                        },
                        onError = {
                            addressList = arrayListOf()
                        }
                )
    }

    fun loadStreetsForCity(cityName: String) {
        viewState.setStreets(getStreets(cityName))
    }

    private fun getCities() = addressList
            .map { cityData -> cityData.city.orEmpty() }
            .distinct()
            .sorted()

    private fun getStreets(cityName: String) = addressList
            .filter { cityData ->
                cityData.city?.toLowerCase()?.contains(cityName.toLowerCase()) == true &&
                        cityData.street != null
            }
            .map { cityData -> cityData.street.orEmpty() }
            .distinct()
            .sorted()

    fun onNextClick(loan: LoanData) {
        viewState.showProgress()
        if (loan.isNewClient) {
            confirmClient(loan)
        } else {
            getTariffInformation(loan)
        }
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

    private fun getTariffInformation (loan: LoanData) {
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
}