package pl.revo.merchant.ui.root

import android.content.Context
import pl.revo.merchant.common.SessionRoute
import pl.revo.merchant.model.*
import pl.revo.merchant.ui.chat.ChatFragment
import pl.revo.merchant.ui.client.agreement.AgreementFragment
import pl.revo.merchant.ui.client.confirm_client.ConfirmClientFragment
import pl.revo.merchant.ui.client.document.DocumentsFragment
import pl.revo.merchant.ui.client.policy.PolicyFragment
import pl.revo.merchant.ui.client.profile.ClientProfileFragment
import pl.revo.merchant.ui.client.profile_bg.ClientProfileBgFragment
import pl.revo.merchant.ui.client.profile_ro.ClientProfileRoFragment
import pl.revo.merchant.ui.client.profile_ru.ClientProfileRuFragment
import pl.revo.merchant.ui.login.forgot.ForgotFragment
import pl.revo.merchant.ui.login.sign_in.SignInFragment
import pl.revo.merchant.ui.login.sign_up.SignUpFragment
import pl.revo.merchant.ui.login.verify_by_sms.VerifyBySmsFragment
import pl.revo.merchant.ui.main.browser.BrowserFragment
import pl.revo.merchant.ui.main.dashboard.DashboardFragment
import pl.revo.merchant.ui.main.declined.DeclinedFragment
import pl.revo.merchant.ui.main.help.HelpFragment
import pl.revo.merchant.ui.main.select_store.SelectStoreFragment
import pl.revo.merchant.ui.purchase.barcode.BarcodeFragment
import pl.revo.merchant.ui.purchase.calculator.CalculatorFragment
import pl.revo.merchant.ui.purchase.confirm.ConfirmFragment
import pl.revo.merchant.ui.purchase.contract.ContractFragment
import pl.revo.merchant.ui.purchase.contract_ru.ContractRuFragment
import pl.revo.merchant.ui.purchase.protection.ProtectionProgramFragment
import pl.revo.merchant.ui.purchase.purchase.PurchaseFragment
import pl.revo.merchant.ui.purchase.self_register.SelfRegisterFragment
import pl.revo.merchant.ui.returns.barcode.BarcodeReturnFragment
import pl.revo.merchant.ui.returns.confirm.ReturnConfirmFragment
import pl.revo.merchant.ui.returns.detail.DetailFragment
import pl.revo.merchant.ui.returns.search.SearchFragment
import pl.revo.merchant.ui.updater.new_version.NewVersionFragment
import pl.revo.merchant.utils.isBgLocale
import pl.revo.merchant.utils.isRoLocale
import pl.revo.merchant.utils.isRuLocale
import ru.terrakok.cicerone.android.SupportAppNavigator

class RootNavigator(
        activity: androidx.fragment.app.FragmentActivity,
        containerId: Int
) : SupportAppNavigator(activity, containerId) {

    override fun createActivityIntent(context: Context?, screenKey: String?, data: Any?) = null

    override fun createFragment(screenKey: String?, data: Any?): androidx.fragment.app.Fragment {
        return when(screenKey) {
            Screens.SIGN_IN         -> {
                SessionRoute.workRoute = false
                SignInFragment.getInstance()
            }
            Screens.FORGOT          -> {
                SessionRoute.workRoute = false
                ForgotFragment.getInstance(data as String)
            }
            Screens.VERIFY_BY_SMS   -> {
                SessionRoute.workRoute = false
                VerifyBySmsFragment.getInstance(data as String)
            }
            Screens.SIGN_UP         -> {
                SessionRoute.workRoute = false
                SignUpFragment.getInstance(data as VerifySmsData)
            }

            Screens.DASHBOARD       -> {
                SessionRoute.workRoute = true
                DashboardFragment.getInstance()
            }
            Screens.DECLINED        -> {
                SessionRoute.workRoute = true
                DeclinedFragment.getInstance(data as String)
            }
            Screens.SELECT_STORE    -> {
                SessionRoute.workRoute = true
                SelectStoreFragment.getInstance()
            }
            Screens.HELP            -> {
                SessionRoute.workRoute = true
                HelpFragment.getInstance(data as HelpDto)
            }

            Screens.CLIENT_PROFILE  -> {
                SessionRoute.workRoute = false
                when {
                    isRuLocale() ->  ClientProfileRuFragment.getInstance(data as LoanData)
                    isRoLocale() -> ClientProfileRoFragment.getInstance(data as LoanData)
                    isBgLocale() -> ClientProfileBgFragment.getInstance(data as LoanData)
                    else -> ClientProfileFragment.getInstance(data as LoanData)
                }

            }
            Screens.AGREEMENT       -> {
                SessionRoute.workRoute = true
                AgreementFragment.getInstance(data as LoanData)
            }
            Screens.POLICY          -> {
                SessionRoute.workRoute = true
                PolicyFragment.getInstance(data as PolicyDto)
            }
            Screens.BROWSER          -> {
                SessionRoute.workRoute = true
                BrowserFragment.getInstance(data as String)
            }
            Screens.DOCUMENTS       -> {
                SessionRoute.workRoute = true
                DocumentsFragment.getInstance(data as LoanData)
            }

            Screens.PURCHASE        -> {
                SessionRoute.workRoute = true
                PurchaseFragment.getInstance()
            }
            Screens.CONFIRM_CLIENT  -> {
                SessionRoute.workRoute = true
                ConfirmClientFragment.getInstance(data as LoanData)
            }
            Screens.CALCULATOR      -> {
                SessionRoute.workRoute = true
                CalculatorFragment.getInstance(data as LoanData)
            }
            Screens.CONFIRM         -> {
                SessionRoute.workRoute = true
                ConfirmFragment.getInstance(data as LoanData)
            }
            Screens.CONTRACT        -> {
                SessionRoute.workRoute = true
                ContractFragment.getInstance(data as LoanData)
            }
            Screens.CONTRACT_RU     -> {
                SessionRoute.workRoute = true
                ContractRuFragment.getInstance(data as LoanData)
            }
            Screens.BARCODE         -> {
                SessionRoute.workRoute = true
                BarcodeFragment.getInstance(data as FinalizeDto)
            }
            Screens.SELF_REGISTER   -> {
                SessionRoute.workRoute = true
                SelfRegisterFragment.getInstance(data as String)
            }
            Screens.SEARCH          -> {
                SessionRoute.workRoute = true
                SearchFragment.getInstance()
            }
            Screens.DETAIL          -> {
                SessionRoute.workRoute = true
                DetailFragment.getInstance(data as SearchData)
            }
            Screens.RETURN_CONFIRM  -> {
                SessionRoute.workRoute = true
                ReturnConfirmFragment.getInstance(data as ReturnData)
            }
            Screens.RETURN_BARCODE  -> {
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