package merchant.mokka.ui.client.profile_ro

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_check_profile.view.*
import kotlinx.android.synthetic.main.fragment_client_profile_ro.*
import merchant.mokka.BuildConfig
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.*
import merchant.mokka.ui.root.RootActivity
import merchant.mokka.utils.*
import merchant.mokka.utils.cnp.cnp
import merchant.mokka.utils.cnp.isValidCnp
import merchant.mokka.utils.decoro.slots.PredefinedSlots
import merchant.mokka.widget.EditTextValidator
import merchant.mokka.widget.attachValidator
import merchant.mokka.widget.detachValidator
import java.util.*

class ClientProfileRoFragment : BaseFragment(), ClientProfileRoView {

    companion object {
        fun getInstance(loan: LoanData): ClientProfileRoFragment {
            val fragment = ClientProfileRoFragment()
            fragment.setArguments(ExtrasKey.LOAN, loan)
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_client_profile_ro
    override val titleResId = R.string.profile_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    @InjectPresenter
    lateinit var presenter: ClientProfileRoPresenter

    @ProvidePresenter
    fun providePresenter() = ClientProfileRoPresenter(injector)

    private lateinit var firstNameValidator: EditTextValidator
    private lateinit var lastNameValidator: EditTextValidator
    private lateinit var cnpValidator: EditTextValidator
    private lateinit var emailValidator: EditTextValidator

    private lateinit var loan: LoanData

    private var dialog: AlertDialog? = null

    //region ================= InitView =================

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData
        initValidators()
        profileNextBtn.setOnClickListener {
            if (isModelValid()) saveData(true)
        }
        setProfileData(loan.client)
        profileDemo.visible(presenter.isDemo())

        addMask(
                slots = arrayOf(
                        PredefinedSlots.letter(), PredefinedSlots.letter(),
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit(),
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit()
                ),
                editor = profileBulletin,
                showEmpty = false
        )

        dialog?.dismiss()
        if (loan.client?.isRepeated == true && loan.client?.isKycPassed == false) {
            saveData(true)
        }

        validateModel()
    }

    private fun saveData(confirm: Boolean) {
        val cnp = profileCnp?.text.toString().cnp()
        val isOver18 = cnp?.checkAge(18) == true

        if ((cnp == null || !isOver18) && !presenter.isDemo()) {
            onError(R.string.error_pesel)
            return
        }

        // Remove it due to disable this check for awhile
//        if (!presenter.isCountyValid(profileBulletin.text.toString())) {
//            onError(R.string.error_bulletin)
//            return
//        }

        loan.client = getClientData()
        if (confirm) {
            showConfirmDialog(
                    onPositiveClick = { presenter.onNextClick(loan) },
                    onNegativeClick = {}
            )
        } else {
            presenter.onNextClick(loan)
        }
    }

    private fun showConfirmDialog(onPositiveClick: () -> Unit, onNegativeClick: () -> Unit) {
        context?.let {
            val dialogView = layoutInflater.inflate(R.layout.dialog_check_profile, null)

                    .apply {
                        profileClientContainer.visible(false)
                        profileClientName.text = loan.client?.fullName
                        profileClientPesel.text = loan.client?.idDocuments?.romanianCnp?.number
                        profileClientId.text = loan.client?.idDocuments?.polishId?.number
                    }

            dialog = AlertDialog.Builder(it)
                .setView(dialogView)
                .setPositiveButton(R.string.button_confirm) { _, _ -> onPositiveClick.invoke() }
                .setNegativeButton(R.string.button_return) { _, _ -> onNegativeClick.invoke() }
                .create()

            dialog?.show()
        }
    }

    private fun getClientData(): ClientData {
        val cnpString = profileCnp.text.toString()
        val cnp = cnpString.cnp()
        val birthday = cnp?.birthDate()

        return ClientData(
                phone = "",
                firstName = profileFirstName.text.toString(),
                lastName = profileLastName.text.toString(),
                birthDate = birthday,
                email = profileEmail.text.toString(),
                blackMark = profileWarningSwh.isChecked.toText(),
                creditLimit = loan.client?.creditLimit,
                idDocuments = IdDocuments(
                        romanianCnp = RomanianCnp(cnpString),
                        idCard = IdCard(profileBulletin.text.toString())
                )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_close -> {
                confirmShowDashboard { presenter.showDashboardScreen() }
                true
            }
            R.id.item_help -> {
                openHelp(toolbarStyle, "help_profile")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion

    //region ================= Validation =================

    private fun initValidators() {
        firstNameValidator = EditTextValidator(
                validator = { text -> text.isNotEmpty() },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_field_required),
                errorRequired = null
        )
        lastNameValidator = EditTextValidator(
                validator = { text -> text.isNotEmpty() },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_field_required),
                errorRequired = null
        )

        cnpValidator = EditTextValidator(
                validator = { text -> text.isValidCnp(presenter.isDemo()) },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_pesel),
                errorRequired = getString(R.string.error_field_required)
        )

        emailValidator = EditTextValidator(
                validator = { text -> text.isValidAsEmail(presenter.isDemo()) },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_email),
                errorRequired = getString(R.string.error_field_required)
        )
    }

    private fun isModelValid(): Boolean {
        return firstNameValidator.isValid() &&
                lastNameValidator.isValid() &&
                cnpValidator.isValid() &&
                emailValidator.isValid()
    }

    private fun validateModel() {
        val isValid = isModelValid()
        profileNextBtn.isEnabled = isValid
        profileNextBtn.alpha = isValid.toAlpha()
    }

    override fun onResume() {
        super.onResume()
        profileFirstName.attachValidator(firstNameValidator, profileFirstNameLayout)
        profileLastName.attachValidator(lastNameValidator, profileLastNameLayout)
        profileCnp.attachValidator(cnpValidator, profilePeselLayout)
        profileEmail.attachValidator(emailValidator, profileEmailLayout)
    }

    override fun onPause() {
        profileFirstName.detachValidator(firstNameValidator)
        profileLastName.detachValidator(lastNameValidator)
        profileCnp.detachValidator(cnpValidator)
        profileEmail.detachValidator(emailValidator)

        super.onPause()
    }

    //endregion

    //region ================= ClientProfileView =================

    override fun showProfile() {
        (activity as RootActivity).initToolbarStyle(
                titleRes = titleResId,
                homeIcon = homeIconType,
                toolbarStyle = toolbarStyle,
                showDemo = showDemo()
        )
    }

    private fun setProfileData(clientData: ClientData?) {
        clientData?.let {
            profileFirstName.setText(it.firstName)
            profileLastName.setText(it.lastName)
            profileCnp.setText(it.idDocuments?.romanianCnp?.number)
            profileEmail.setText(it.email)
            profileWarningSwh.isChecked = it.blackMark == "1"
            if (clientData != null) {
                showConfirmDialog(
                    onPositiveClick = {
                        if (isModelValid()) {
                            saveData(false)
                        }
                    },
                    onNegativeClick = { }
                )
            }
        }
    }

    //endregion
}