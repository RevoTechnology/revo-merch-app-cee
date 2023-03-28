package merchant.mokka.ui.returns.search

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.BaseRecyclerViewAdapter
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.SearchData
import merchant.mokka.utils.FormatTextWatcher
import merchant.mokka.utils.SimpleTextWatcher
import merchant.mokka.utils.addMask
import merchant.mokka.utils.clearPhone
import merchant.mokka.utils.cnp.isValidCnp
import merchant.mokka.utils.createPhoneMaskFormatWatcher
import merchant.mokka.utils.decoro.slots.PredefinedSlots
import merchant.mokka.utils.decoro.watchers.MaskFormatWatcher
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.isRoLocale
import merchant.mokka.utils.isValid
import merchant.mokka.utils.isValidAsPolishPESEL
import merchant.mokka.widget.EditTextValidator
import merchant.mokka.widget.attachValidator
import merchant.mokka.widget.detachValidator


class SearchFragment : BaseFragment(), SearchView {

    companion object {
        fun getInstance() = SearchFragment()
    }

    @InjectPresenter
    lateinit var presenter: SearchPresenter

    @ProvidePresenter
    fun providePresenter() = SearchPresenter(injector)

    override val layoutResId = R.layout.fragment_search
    override val titleResId = R.string.search_title
    override val homeIconType = HomeIconType.MENU
    override val toolbarStyle = ToolbarStyle.DARK

    private lateinit var phoneError: String
    private lateinit var passportError: String

    private var defaultBg: Drawable? = null
    private var errorBg: Drawable? = null

    private lateinit var adapter: BaseRecyclerViewAdapter<SearchData>

    private val loginValidator = SimpleTextWatcher { validate() }
    private lateinit var cnpValidator: EditTextValidator
    private val contractValidator = SimpleTextWatcher { validate() }

    private lateinit var phoneFormatWatcher: MaskFormatWatcher
    private lateinit var phonePrefix: String

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        phoneFormatWatcher =
            createPhoneMaskFormatWatcher(searchLogin, getString(R.string.phone_mask))
        searchLogin.setText(R.string.phone_empty)

        phoneError = getString(R.string.error_phone)
        passportError = getString(R.string.error_id_number)

        defaultBg = ContextCompat.getDrawable(requireContext(), R.drawable.edit_bg_selector)
        errorBg = ContextCompat.getDrawable(requireContext(), R.drawable.edit_bg_error)

        with(view) {
            when {
                isRoLocale() -> {
                    searchPassport.run {
                        inputType = InputType.TYPE_CLASS_NUMBER
                        filters = arrayOf<InputFilter>(LengthFilter(13))
                    }
                }
                isBgLocale() -> {
                    searchPassport.run {
                        inputType = InputType.TYPE_CLASS_NUMBER
                        filters = arrayOf<InputFilter>(LengthFilter(10))
                    }
                }
            }
            searchPassport.doAfterTextChanged {
                validate()
            }

            cnpValidator = EditTextValidator(
                validator = { text -> text.isValidCnp(presenter.isDemo()) },
                onChangedState = { validate() },
                errorText = getString(R.string.error_pesel),
                errorRequired = null
            )

            adapter = BaseRecyclerViewAdapter(
                layout = R.layout.item_search,
                items = mutableListOf(),
                holderFactory = { v -> SearchHolder(v) },
                onItemClick = { item, _ -> presenter.makeReturn(item) }
            )

            searchList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            searchList.adapter = adapter

            searchList.isNestedScrollingEnabled = false
            searchList.setHasFixedSize(false)

            searchButton.setOnClickListener {
                adapter.items.clear()

                val login = if (isBgLocale()) searchLogin.text.toString()
                    .replace(getString(R.string.phone_empty), "") else searchLogin.text.toString()

                presenter.searchPurchases(
                    login,
                    searchPassport.text.toString(),
                    searchContract.text.toString()
                )
            }

            validate()
        }
    }

    private fun validate() {
        var phoneValid = true
        val passportValid: Boolean

        searchPassportLayout.error = when {
            isRoLocale() -> {
                passportValid = cnpValidator.isValid() || searchPassport.text.isNullOrEmpty()
                if (passportValid) null else getString(R.string.error_pesel)
            }
            isBgLocale() -> {
                passportValid =
                    searchPassport.text?.length == 10 || searchPassport.text.isNullOrEmpty()
                if (passportValid) null else getString(R.string.error_pesel)
            }
            else -> {
                passportValid =
                    searchPassport.text.isNullOrEmpty() || searchPassport.text?.toString().orEmpty()
                        .isValidAsPolishPESEL(presenter.isDemo())
                if (passportValid) null else getString(R.string.error_pesel)
            }
        }

        searchPassport.background = ContextCompat.getDrawable(
            requireContext(),
            if (passportValid) R.drawable.edit_bg_selector else R.drawable.edit_error_selector
        )

        searchButton.isEnabled =
            when {
                presenter.isDemo() -> true
                searchLogin.text.toString().clearPhone().isNotEmpty() &&
                        searchLogin.text.toString()
                            .clearPhone() != getString(R.string.phone_prefix) -> phoneValid
                searchPassport.text?.isNotEmpty() == true -> passportValid
                searchContract.text?.isNotEmpty() == true -> true
                else -> false
            }
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
                openHelp(toolbarStyle, "help_search")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        searchLogin.addTextChangedListener(loginValidator)
        searchPassport.attachValidator(cnpValidator, searchPassportLayout)
        searchContract.addTextChangedListener(contractValidator)
    }

    override fun onPause() {
        super.onPause()
        searchLogin.removeTextChangedListener(loginValidator)
        searchPassport.detachValidator(cnpValidator)
        searchContract.removeTextChangedListener(contractValidator)
    }

    override fun setData(items: List<SearchData>) {
        adapter.addItems(items)
    }

    override fun onBackPressed(): Boolean {
        confirmShowDashboard { presenter.showDashboardScreen() }
        return true
    }
}