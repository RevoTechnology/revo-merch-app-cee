package pl.revo.merchant.ui.main.help

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_help.*
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.ExtrasKey
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.model.HelpDto

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