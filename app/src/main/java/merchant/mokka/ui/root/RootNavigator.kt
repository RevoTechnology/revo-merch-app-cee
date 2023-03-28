package merchant.mokka.ui.root

import android.content.Context
import merchant.mokka.common.SessionRoute
import merchant.mokka.ui.chat.ChatFragment
import merchant.mokka.ui.client.agreement.AgreementFragment
import merchant.mokka.ui.client.confirm_client.ConfirmClientFragment
import merchant.mokka.ui.client.document.DocumentsFragment
import merchant.mokka.ui.client.policy.PolicyFragment
import merchant.mokka.ui.client.profile.ClientProfileFragment
import merchant.mokka.ui.client.profile_bg.ClientProfileBgFragment
import merchant.mokka.ui.client.profile_ro.ClientProfileRoFragment
import merchant.mokka.ui.login.forgot.ForgotFragment
import merchant.mokka.ui.login.sign_in.SignInFragment
import merchant.mokka.ui.login.sign_up.SignUpFragment
import merchant.mokka.ui.login.verify_by_sms.VerifyBySmsFragment
import merchant.mokka.ui.main.browser.BrowserFragment
import merchant.mokka.ui.main.dashboard.DashboardFragment
import merchant.mokka.ui.main.declined.DeclinedFragment
import merchant.mokka.ui.main.help.HelpFragment
import merchant.mokka.ui.main.select_store.SelectStoreFragment
import merchant.mokka.ui.purchase.barcode.BarcodeFragment
import merchant.mokka.ui.purchase.calculator.CalculatorFragment
import merchant.mokka.ui.purchase.confirm.ConfirmFragment
import merchant.mokka.ui.purchase.contract.ContractFragment
import merchant.mokka.ui.purchase.protection.ProtectionProgramFragment
import merchant.mokka.ui.purchase.purchase.PurchaseFragment
import merchant.mokka.ui.purchase.self_register.SelfRegisterFragment
import merchant.mokka.ui.returns.barcode.BarcodeReturnFragment
import merchant.mokka.ui.returns.confirm.ReturnConfirmFragment
import merchant.mokka.ui.returns.detail.DetailFragment
import merchant.mokka.ui.returns.search.SearchFragment
import merchant.mokka.ui.updater.new_version.NewVersionFragment
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isRoLocale
import merchant.mokka.model.*
import ru.terrakok.cicerone.android.SupportAppNavigator

class RootNavigator(
        activity: androidx.fragment.app.FragmentActivity,
        containerId: Int
) : SupportAppNavigator(activity, containerId) {

    override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?) = null

    override fun createFragment(screenKey: String?, data: Any?): androidx.fragment.app.Fragment {
        return when(screenKey) {
            Screens.SIGN_IN -> {
                SessionRoute.workRoute = false
                SignInFragment.getInstance()
            }
            Screens.FORGOT -> {
                SessionRoute.workRoute = false
                ForgotFragment.getInstance(data as String)
            }
            Screens.VERIFY_BY_SMS -> {
                SessionRoute.workRoute = false
                VerifyBySmsFragment.getInstance(data as String)
            }
            Screens.SIGN_UP -> {
                SessionRoute.workRoute = false
                SignUpFragment.getInstance(data as VerifySmsData)
            }

            Screens.DASHBOARD -> {
                SessionRoute.workRoute = true
                DashboardFragment.getInstance()
            }
            Screens.DECLINED -> {
                SessionRoute.workRoute = true
                DeclinedFragment.getInstance(data as String)
            }
            Screens.SELECT_STORE -> {
                SessionRoute.workRoute = true
                SelectStoreFragment.getInstance()
            }
            Screens.HELP -> {
                SessionRoute.workRoute = true
                HelpFragment.getInstance(data as HelpDto)
            }

            Screens.CLIENT_PROFILE -> {
                SessionRoute.workRoute = false
                when {
                    isRoLocale() -> ClientProfileRoFragment.getInstance(data as LoanData)
                    isBgLocale() -> ClientProfileBgFragment.getInstance(data as LoanData)
                    else -> ClientProfileFragment.getInstance(data as LoanData)
                }

            }
            Screens.AGREEMENT -> {
                SessionRoute.workRoute = true
                AgreementFragment.getInstance(data as LoanData)
            }
            Screens.POLICY -> {
                SessionRoute.workRoute = true
                PolicyFragment.getInstance(data as PolicyDto)
            }
            Screens.BROWSER -> {
                SessionRoute.workRoute = true
                BrowserFragment.getInstance(data as String)
            }
            Screens.DOCUMENTS -> {
                SessionRoute.workRoute = true
                DocumentsFragment.getInstance(data as LoanData)
            }

            Screens.PURCHASE -> {
                SessionRoute.workRoute = true
                PurchaseFragment.getInstance()
            }
            Screens.CONFIRM_CLIENT -> {
                SessionRoute.workRoute = true
                ConfirmClientFragment.getInstance(data as LoanData)
            }
            Screens.CALCULATOR -> {
                SessionRoute.workRoute = true
                CalculatorFragment.getInstance(data as LoanData)
            }
            Screens.CONFIRM -> {
                SessionRoute.workRoute = true
                ConfirmFragment.getInstance(data as LoanData)
            }
            Screens.CONTRACT -> {
                SessionRoute.workRoute = true
                ContractFragment.getInstance(data as LoanData)
            }
            Screens.BARCODE -> {
                SessionRoute.workRoute = true
                BarcodeFragment.getInstance(data as FinalizeDto)
            }
            Screens.SELF_REGISTER -> {
                SessionRoute.workRoute = true
                SelfRegisterFragment.getInstance(data as String)
            }
            Screens.SEARCH -> {
                SessionRoute.workRoute = true
                SearchFragment.getInstance()
            }
            Screens.DETAIL -> {
                SessionRoute.workRoute = true
                DetailFragment.getInstance(data as SearchData)
            }
            Screens.RETURN_CONFIRM -> {
                SessionRoute.workRoute = true
                ReturnConfirmFragment.getInstance(data as ReturnData)
            }
            Screens.RETURN_BARCODE -> {
                SessionRoute.workRoute = true
                BarcodeReturnFragment.getInstance(data as ReturnData)
            }
            Screens.NEW_VERSION -> {
                SessionRoute.workRoute = false
                NewVersionFragment.getInstance(data as UpdateData)
            }
            Screens.PROTECTION_PROGRAM -> {
                SessionRoute.workRoute = true
                ProtectionProgramFragment.getInstance(data as LoanData)
            }
            Screens.CHAT -> {
                SessionRoute.workRoute = false
                ChatFragment.getInstance()
            }

            else -> throw IllegalArgumentException()
        }
    }


}