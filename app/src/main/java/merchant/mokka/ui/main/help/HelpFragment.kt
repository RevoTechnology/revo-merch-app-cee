package merchant.mokka.ui.main.help

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_help.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.HelpDto

class HelpFragment : BaseFragment() {

    companion object {
        fun getInstance(help: HelpDto) : HelpFragment {
            val fragment = HelpFragment()
            fragment.setArguments(ExtrasKey.HELP_TOOLBAR, help)
            return fragment
        }
    }

    override val layoutResId = R.layout.fragment_help
    override val titleResId = R.string.help_title
    override val homeIconType = HomeIconType.CLOSE
    override lateinit var toolbarStyle: ToolbarStyle

    private lateinit var help: HelpDto

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        help = arguments?.getSerializable(ExtrasKey.HELP_TOOLBAR.name) as HelpDto
        toolbarStyle = help.toolbarStyle

        super.onViewCreated(view, savedInstanceState)
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        with (view) {
            helpInfo.loadUrl("file:///android_asset/raw/${help.helpRes}")
        }
    }
}