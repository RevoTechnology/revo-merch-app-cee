package pl.revo.merchant.ui.client.profile

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
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
import pl.revo.merchant.BuildConfig
import pl.revo.merchant.Event
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.ExtrasKey
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.model.*
import pl.revo.merchant.track
import pl.revo.merchant.ui.root.RootActivity
import pl.revo.merchant.utils.*
import pl.revo.merchant.utils.decoro.MaskImpl
import pl.revo.merchant.utils.decoro.parser.UnderscoreDigitSlotsParser
import pl.revo.merchant.utils.decoro.slots.PredefinedSlots
import pl.revo.merchant.utils.decoro.slots.Slot
import pl.revo.merchant.utils.decoro.watchers.MaskFormatWatcher
import pl.revo.merchant.widget.EditTextValidator
import pl.revo.merchant.widget.attachValidator
import pl.revo.merchant.widget.detachValidator
import java.util.*


class ClientProfileFragment : BaseFragment(), ClientProfileView {

    companion object {
        fun getInstance(loan: LoanData): ClientProfileFragment {
            val fragment = ClientProfileFragment()
            fragment.setArguments(ExtrasKey.LOAN, loan)
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_client_profile
    override val titleResId = R.string.profile_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    @InjectPresenter
    lateinit var presenter: ClientProfilePresenter

    @ProvidePresenter
    fun providePresenter() = ClientProfilePresenter(injector)

    private lateinit var firstNameValidator: EditTextValidator
    private lateinit var lastNameValidator: EditTextValidator
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
                profileWarningSwh).forEach { it?.visible(false) }

        if(BuildConfig.DEBUG ){
            profileFirstName.setText("MATEUSZ")
            profileLastName.setText("SZEMRAJ")
            profileId.setText("GWX599396")
            profilePesel.setText("87062746316")
            profileEmail.setText("forgiven.null+${UUID.randomUUID()}@gmail.com")
        }

        profilePesel.filters = arrayOf<InputFilter>(LengthFilter(11))
    }

    private fun saveData(confirm: Boolean) {
        val pesel = profilePesel.text.toString()
        val birthday = pesel.polishPeselToBirthDay()

        if (birthday == null && !presenter.isDemo()) {
            onError(R.string.error_pesel)
            return
        }

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
                profileClientName.text = loan.client?.fullName
                profileClientPesel.text = loan.client?.idDocuments?.polishPesel?.number
                profileClientId.text = loan.client?.idDocuments?.polishId?.number
            }

            AlertDialog.Builder(it)
                    .setView(dialogView)
                    .setPositiveButton(R.string.button_confirm) { _, _ -> onPositiveClick.invoke() }
                    .setNegativeButton(R.string.button_return) { _, _ -> onNegativeClick.invoke() }
                    .show()
        }
    }

    private fun getClientData(): ClientData {
        val pesel = profilePesel.text.toString()
        val birthday = pesel.polishPeselToBirthDay()

        return ClientData(
                phone = "",
                firstName = profileFirstName.text.toString(),
                lastName = profileLastName.text.toString(),
                birthDate = birthday,
                idDocuments = IdDocuments(
                        PolishId(number = profileId.text.toString()),
                        PolishPesel(number = pesel)
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

        idValidator = EditTextValidator(
                validator = { text -> text.isValidAsPolishID(presenter.isDemo()) },
                onChangedState = { validateModel() },
                errorText = getString(R.string.error_id_number),
                errorRequired = getString(R.string.error_field_required)
        )
        addMask(
                slots = arrayOf(
                        PredefinedSlots.any(), PredefinedSlots.any(), PredefinedSlots.any(),
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit(),
                        PredefinedSlots.digit(), PredefinedSlots.digit(), PredefinedSlots.digit()
                ),
                editor = profileId,
                showEmpty = false
        )

        peselValidator = EditTextValidator(
                validator = { text -> text.isValidAsPolishPESEL(presenter.isDemo()) },
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
//                streetValidator.isValid() &&
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
            profilePesel.setText(it.idDocuments?.polishPesel?.number)
            profileId.setText(it.idDocuments?.polishId?.number)
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
        val adapter = ArrayAdapter(requireContext(), R.layout.item_address_search, R.id.addressText, cities.toTypedArray())
        profileCity.threshold = 0
        profileCity.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    override fun setStreets(streets: List<String>) {
        val streetAdapter = ArrayAdapter(requireContext(), R.layout.item_address_search, R.id.addressText, streets.toTypedArray())
        profileStreet.threshold = 1
        profileStreet.setAdapter(streetAdapter)
        streetAdapter.notifyDataSetChanged()
    }

    //endregion
}