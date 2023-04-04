package merchant.mokka.ui.purchase.calculator

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import io.reactivex.rxkotlin.subscribeBy
import merchant.mokka.R
import merchant.mokka.api.error.ApiErr
import merchant.mokka.common.BasePresenter
import merchant.mokka.model.AutoAgentData
import merchant.mokka.model.LoanData
import merchant.mokka.ui.root.Screens
import merchant.mokka.utils.*
import merchant.mokka.utils.*

@InjectViewState
class CalculatorPresenter(injector: KodeinInjector) : BasePresenter<CalculatorView>(injector) {
	private val autoAgentData by injector.instance<AutoAgentData>()
	var initialSum = 0.0

	fun start(loan: LoanData) {
		if (autoAgentData.isValid) viewState.lockSumAndRefresh()
		else viewState.setData(loan.tariffs, null)
	}

	fun onNextClick(loan: LoanData, termId: Int, smsInfoAgree: Boolean) {
		if (termId > 0) {
			loan.termId = termId
			loan.smsInfoAgree = smsInfoAgree
			createApprovedLoan(loan)
		}
	}

	fun showDashboardScreen() {
		router.newRootScreen(Screens.DASHBOARD)
	}

	private fun createApprovedLoan(loan: LoanData) {
		viewState.showProgress()
		service.createApprovedLoan(loan.token, loan.termId.orZero(), loan.smsInfoAgree)
			.subscribeBy(
				onSuccess = {
					sendClientConfirmCode(loan)
				},
				onError = {
					viewState.hideProgress()
					viewState.onError(it)
				}
			)
	}

	private fun sendClientConfirmCode(loan: LoanData) {
		service.sendConfirmClientCode(loan.token)
			.subscribeBy(
				onSuccess = {
					viewState.hideProgress()
					showConfirmScreen(loan)
				},
				onError = {
					viewState.hideProgress()
					viewState.onError(it)
				}
			)
	}

	private fun showConfirmScreen(loan: LoanData) {
		val nextScreen = when {
			isPlLocale() -> Screens.CONTRACT
			isRoLocale() && loan.client?.rclAccepted != true && loan.tariff?.isRclProductKing == true && !isAvailabilityDocuments(loan) -> Screens.DOCUMENTS
			isBgLocale() && !isAvailabilityDocuments(loan) -> Screens.DOCUMENTS
			else -> Screens.CONTRACT
		}

		router.navigateTo(nextScreen, loan)
	}

	private fun isAvailabilityDocuments(loan: LoanData): Boolean = loan.client?.missingDocuments.isNullOrEmpty()

	fun refreshSum(loan: LoanData, sum: Double, agreeInsurance: Boolean?) {
		viewState.showProgress()
		service.updateLoanRequest(
			loanToken = loan.token,
			phone = loan.clientPhone,
			amount = sum.toServerFormat(),
			agreeInsurance = agreeInsurance
		).subscribeBy(
			onSuccess = {
				loan.sum = sum
				initialSum = sum
				getTariffInformation(loan)
			},
			onError = {
				viewState.hideProgress()
				if (it is ApiErr) {
					val error = when {
						it.largeAmount() -> R.string.error_purchase_exceeds
						it.phoneError() -> R.string.error_phone
						else -> null
					}
					error?.let { viewState.onError(error) }
				} else {
					viewState.onError(it)
				}
			}
		)
	}

	private fun getTariffInformation(loan: LoanData) {
		service.getTariffInfo(loan)
			.subscribeBy(
				onSuccess = {
					viewState.hideProgress()
					loan.tariffs = it.tariffs
					viewState.setData(
						it.tariffs, it.client?.smsInfo
					)
				},
				onError = {
					viewState.hideProgress()
					viewState.onError(it)
				}
			)
	}
}