package merchant.mokka.ui.client.profile_bg

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.dialog_check_profile.view.*
import kotlinx.android.synthetic.main.part_client_profile.*
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.ClientData
import merchant.mokka.model.Egn
import merchant.mokka.model.IdCard
import merchant.mokka.model.IdDocuments
import merchant.mokka.model.LoanData
import merchant.mokka.track
import merchant.mokka.ui.root.RootActivity
import merchant.mokka.utils.Constants
import merchant.mokka.utils.decoro.MaskImpl
import merchant.mokka.utils.decoro.parser.UnderscoreDigitSlotsParser
import merchant.mokka.utils.decoro.slots.PredefinedSlots
import merchant.mokka.utils.decoro.slots.Slot
import merchant.mokka.utils.decoro.watchers.MaskFormatWatcher
import merchant.mokka.utils.isValidAsEmail
import merchant.mokka.utils.isValidAsPostalCode
import merchant.mokka.utils.toAlpha
import merchant.mokka.utils.toText
import merchant.mokka.utils.visible
import merchant.mokka.widget.EditTextValidator
import merchant.mokka.widget.attachValidator
import merchant.mokka.widget.detachValidator


class ClientProfileBgFragment : BaseFragment(), ClientProfileBgView {

    companion object {
        fun getInstance(loan: LoanData): ClientProfileBgFragment {
            val fragment = ClientProfileBgFragment()
            fragment.setArguments(ExtrasKey.LOAN, loan)
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_client_profile
    override val titleResId = R.string.profile_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    @InjectPresenter
    lateinit var presenter: ClientProfileBgPresenter

    @ProvidePresenter
    fun providePresenter() = ClientProfileBgPresenter(injector)

    private lateinit var firstNameValidator: EditTextValidator
    private lateinit var lastNameValidator: EditTextValidator
    private lateinit var patronymicValidator: EditTextValidator
    private lateinit var idValidator: EditTextValidator
    private lateinit var peselValidator: EditTextValidator
    private lateinit var streetValidator: EditTextValidator
    private lateinit var houseValidator: EditTextValidator
    private lateinit var postalCodeValidator: EditTextValidator
    private lateinit var cityValidator: EditTextValidator
    private lateinit var emailValidator: EditTextValidator

    private lateinit var loan: LoanData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.CLIENT_PROFILE.track()
    }

    //region ================= InitView =================

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData
        initValidators()
        profileNextBtn.setOnClickListener {
            if (isModelValid()) {
                saveData(true)
            }
        }
        setProfileData(loan.client)
        profileDemo.visible(presenter.isDemo())

        listOf(
            profilePostalCodeLayout,
            profileCityLayout,
            profileStreetLayout,
            profileHouseLayout,
            profileFlatLayout,
            profileWarningSwh
        ).forEach { it?.visible(false) }

        patronymicNameLayout.visible(true)
        profileId.apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf<InputFilter>(LengthFilter(9))
        }
        profilePesel.filters = arrayOf<InputFilter>(LengthFilter(10))
    }

    private fun saveData(confirm: Boolean) {
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
            with(dialogView) {
                profileClientName.text = loan.client?.fullNameWithPatronymic
                profileClientPesel.text = loan.client?.idDocuments?.egn?.number
                profileClientId.text = loan.client?.idDocuments?.idCard?.number
            }

            AlertDialog.Builder(it)
                .setView(dialogView)
                .setPositiveButton(R.string.button_confirm) { _, _ -> onPositiveClick.invoke() }
                .setNegativeButton(R.string.button_return) { _, _ -> onNegativeClick.invoke() }
                .show()
        }
    }

    private fun getClientData() = ClientData(
        phone = "",
        firstName = profileFirstName.text.toString(),
        lastName = profileLastName.text.toString(),
        middleName = patronymicName.text.toString(),
        birthDate = null,
        idDocuments = IdDocuments(
            idCard = IdCard(number = profileId.text.toString()),
            egn = Egn(number = profilePesel.text.toString())
        ),
        street = profileStreet.text.toString(),
        house = profileHouse.text.toString(),
        apartment = profileFlat.text.toString(),
        postalCode = profilePostalCode.text.toString(),
        settlement = profileCity.text.toString(),
        email = profileEmail.text.toString(),
        blackMark = profileWarningSwh.isChecked.toText(),
        creditLimit = loan.client?.creditLimit
    )

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

        patronymicValidator = EditTextValidator(
            validator = { text -> text.isNotEmpty() },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_field_required),
            errorRequired = null
        )

        idValidator = EditTextValidator(
            validator = { text -> text.length == 9 },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_id_number),
            errorRequired = getString(R.string.error_field_required)
        )
        addMask(
            slots = arrayOf(
                PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit(),
                PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit(),
                PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit()
            ),
            editor = profileId,
            showEmpty = false
        )

        peselValidator = EditTextValidator(
            validator = { text -> text.length == 10 },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_pesel),
            errorRequired = getString(R.string.error_field_required)
        )
        streetValidator = EditTextValidator(
            validator = { text -> text.isNotEmpty() },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_field_required),
            errorRequired = null
        )
        houseValidator = EditTextValidator(
            validator = { text -> text.isNotEmpty() },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_field_required),
            errorRequired = null
        )
        postalCodeValidator = EditTextValidator(
            validator = { text ->
                val postalCodeValid = text.isValidAsPostalCode(presenter.isDemo())
                if (postalCodeValid && !presenter.isDemo())
                    presenter.loadAddressByPostalCode(text)
                postalCodeValid
            },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_postal_code),
            errorRequired = getString(R.string.error_field_required)
        )
        if (!presenter.isDemo()) {
            addMask(
                slots = UnderscoreDigitSlotsParser().parseSlots(Constants.POSTAL_CODE_MASK),
                editor = profilePostalCode,
                showEmpty = false
            )
        }

        cityValidator = EditTextValidator(
            validator = { text ->
                val cityValid = text.isNotEmpty()
                if (cityValid)
                    presenter.loadStreetsForCity(text)
                cityValid
            },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_field_required),
            errorRequired = null
        )
        emailValidator = EditTextValidator(
            validator = { text -> text.isValidAsEmail(presenter.isDemo()) },
            onChangedState = { validateModel() },
            errorText = getString(R.string.error_email),
            errorRequired = getString(R.string.error_field_required)
        )
    }

    private fun addMask(slots: Array<Slot>, editor: EditText, showEmpty: Boolean) {
        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = true
        mask.isShowingEmptySlots = showEmpty
        val formatWatcher = MaskFormatWatcher(mask)
        formatWatcher.installOn(editor)
    }

    private fun isModelValid(): Boolean {
        return firstNameValidator.isValid() &&
                lastNameValidator.isValid() &&
                idValidator.isValid() &&
                peselValidator.isValid() &&
                patronymicValidator.isValid() &&
//                houseValidator.isValid() &&
//                postalCodeValidator.isValid() &&
//                cityValidator.isValid() &&
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
        patronymicName.attachValidator(patronymicValidator, patronymicNameLayout)
        profileId.attachValidator(idValidator, profileIdLayout)
        profilePesel.attachValidator(peselValidator, profilePeselLayout)
        profileStreet.attachValidator(streetValidator, profileStreetLayout)
        profileHouse.attachValidator(houseValidator, profileHouseLayout)
        profilePostalCode.attachValidator(postalCodeValidator, profilePostalCodeLayout)
        profileCity.attachValidator(cityValidator, profileCityLayout)
        profileEmail.attachValidator(emailValidator, profileEmailLayout)
    }

    override fun onPause() {
        profileFirstName.detachValidator(firstNameValidator)
        profileLastName.detachValidator(lastNameValidator)
        patronymicName.detachValidator(patronymicValidator)
        profileId.detachValidator(idValidator)
        profilePesel.detachValidator(peselValidator)
        profileStreet.detachValidator(streetValidator)
        profileHouse.detachValidator(houseValidator)
        profilePostalCode.detachValidator(postalCodeValidator)
        profileCity.detachValidator(cityValidator)
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
            patronymicName.setText(it.middleName)
            profilePesel.setText(it.idDocuments?.egn?.number)
            profileId.setText(it.idDocuments?.idCard?.number)
            profileStreet.setText(it.street)
            profileHouse.setText(it.house)
            profileFlat.setText(it.apartment)
            profilePostalCode.setText(it.postalCode)
            profileCity.setText(it.settlement)
            profileEmail.setText(it.email)

            profileWarningSwh.isChecked = it.blackMark == "1"
        }

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

    override fun setCities(cities: List<String>) {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_address_search,
            R.id.addressText,
            cities.toTypedArray()
        )
        profileCity.threshold = 0
        profileCity.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    override fun setStreets(streets: List<String>) {
        val streetAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_address_search,
            R.id.addressText,
            streets.toTypedArray()
        )
        profileStreet.threshold = 1
        profileStreet.setAdapter(streetAdapter)
        streetAdapter.notifyDataSetChanged()
    }

    //endregion
}