package merchant.mokka.ui.client.profile_ru

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_client_profile_ru.*
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.*
import merchant.mokka.track
import merchant.mokka.utils.*
import merchant.mokka.model.*
import merchant.mokka.utils.decoro.slots.PredefinedSlots
import merchant.mokka.widget.EditTextValidator
import merchant.mokka.widget.attachValidator
import merchant.mokka.widget.detachValidator
import merchant.mokka.utils.*

class ClientProfileRuFragment : BaseFragment(), ClientProfileRuView {

    companion object {
        fun getInstance(loan: LoanData): ClientProfileRuFragment {
            val fragment = ClientProfileRuFragment()
            fragment.setArguments(ExtrasKey.LOAN, loan)
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_client_profile_ru
    override val titleResId = R.string.profile_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    @InjectPresenter
    lateinit var presenter: ClientProfileRuPresenter

    @ProvidePresenter
    fun providePresenter() = ClientProfileRuPresenter(injector)

    private lateinit var firstNameValidator: EditTextValidator
    private lateinit var lastNameValidator: EditTextValidator
    private lateinit var middleNameValidator: EditTextValidator

    private lateinit var idValidator: EditTextValidator
    private lateinit var birthValidator: EditTextValidator

    private lateinit var loan: LoanData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.CLIENT_PROFILE.track()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData

        initValidators()

        profileAgreeAsp.setOnClickListener { validateModel() }
        profileNextBtn.setOnClickListener {
            if (isModelValid()) {
                saveData()
            }
        }
        profileAgreeAspButton.setOnClickListener {
            presenter.onAgreeClick(R.string.profile_agree_asp_title, loan, DocumentKind.ASP.urlPart)
        }
        profileAgreePersonalDataButton.setOnClickListener {
            presenter.onAgreeClick(R.string.profile_agree_personal_date_title, loan, DocumentKind.AGREEMENT.urlPart)
        }

        if (isRuLocale())
            profileWarningSwh.visible(false)
    }

    private fun saveData() {
        val pass = profileId.text.toString()
        val clientData = ClientData(
                firstName = profileFirstName.text.toString(),
                lastName = profileLastName.text.toString(),
                middleName = profileMiddleName.text.toString(),
                birthDate = profileBirth.text.toString().toDate(DateFormats.SIMPLE_FORMAT),
                idDocuments = IdDocuments(russianPassport =
                RussianPassport(
                        series = pass.substring(0, 4),
                        number = pass.substring(5, 11))
                )
        )
        loan.client = clientData
        presenter.onNextClick(loan)
    }

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
        middleNameValidator = EditTextValidator(
                validator = { text -> text.isNotEmpty() },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_field_required),
                errorRequired = null
        )
        idValidator = EditTextValidator(
                validator = { text -> text.length == 11 },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_id_number),
                errorRequired = null
        )
        addMask(
                slots = arrayOf(
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit(),
                        PredefinedSlots.digit(), PredefinedSlots.hardcodedSlot(' '),
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit(),
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit()
                ),
                editor = profileId,
                showEmpty = false
        )
        birthValidator = EditTextValidator(
                validator = { text -> text.isValidAsBirth() },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_birth_day),
                errorRequired = null
        )
        addMask(
                slots = arrayOf(
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.hardcodedSlot('.'),
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.hardcodedSlot('.'),
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit()
                ),
                editor = profileBirth,
                showEmpty = false
        )
    }

    private fun validateModel() {
        val isValid = isModelValid()
        profileNextBtn.isEnabled = isValid
        profileNextBtn.alpha = isValid.toAlpha()
    }

    private fun isModelValid(): Boolean {
        return firstNameValidator.isValid() && lastNameValidator.isValid() &&
                middleNameValidator.isValid() && idValidator.isValid() &&
                birthValidator.isValid() && profileAgreeAsp.isChecked
    }

    override fun onResume() {
        super.onResume()
        profileFirstName.attachValidator(firstNameValidator, profileFirstNameLayout)
        profileLastName.attachValidator(lastNameValidator, profileLastNameLayout)
        profileMiddleName.attachValidator(middleNameValidator, profileMiddleNameLayout)
        profileId.attachValidator(idValidator, profileIdLayout)
        profileBirth.attachValidator(birthValidator, profileBirthLayout)
    }

    override fun onPause() {
        super.onPause()
        profileFirstName.detachValidator(firstNameValidator)
        profileLastName.detachValidator(lastNameValidator)
        profileMiddleName.detachValidator(middleNameValidator)
        profileId.detachValidator(idValidator)
        profileBirth.detachValidator(birthValidator)
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
}