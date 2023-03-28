package merchant.mokka.ui.main.declined

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_declined.view.*
import merchant.mokka.Event
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.track

class DeclinedFragment : BaseFragment(), DeclinedView {

    companion object {
        fun getInstance(message: String) : DeclinedFragment {
            val fragment = DeclinedFragment()
            fragment.setArguments(ExtrasKey.DECLINED, message)
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: DeclinedPresenter

    @ProvidePresenter
    fun providePresenter() = DeclinedPresenter(injector)

    override val layoutResId = R.layout.fragment_declined
    override val titleResId = R.string.declined_title
    override val homeIconType = HomeIconType.MENU
    override val toolbarStyle = ToolbarStyle.ACCENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Event.LIMIT_REFUSE_SCREEN.track()
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        val message = arguments?.getSerializable(ExtrasKey.DECLINED.name) as String

        with(view) {
            declinedToDashboard.setOnClickListener { presenter.goToDashboard() }

            @Suppress("DEPRECATION")
            if (message.isNotEmpty())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    declinedMessage.text = Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
                else
                    declinedMessage.text = Html.fromHtml(message)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.item_help -> {
                openHelp(toolbarStyle, "help_declined")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(): Boolean {
        presenter.goToDashboard()
        return true
    }
}