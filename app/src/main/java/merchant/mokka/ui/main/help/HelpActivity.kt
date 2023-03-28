package merchant.mokka.ui.main.help

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_help.*
import merchant.mokka.BuildConfig
import merchant.mokka.R
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.utils.getHomeIcon

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if(BuildConfig.SCREEN_SHOOT_DISABLED) window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val toolbarStyle = intent.getSerializableExtra(ExtrasKey.HELP_TOOLBAR.name) as ToolbarStyle
        val toolbarTitle = intent.getStringExtra(ExtrasKey.HELP_TITLE.name)
        initToolbar(toolbarStyle, toolbarTitle.orEmpty())

        val helpRes = intent.getStringExtra(ExtrasKey.HELP_RES_ID.name)
        helpInfo.loadUrl("file:///android_asset/raw/$helpRes")
    }

    private fun initToolbar(toolbarStyle: ToolbarStyle, title: String) {
        helpToolbar.setTitleTextColor(ContextCompat.getColor(this, toolbarStyle.titleColorRes))
        helpToolbar.setBackgroundColor(ContextCompat.getColor(this, toolbarStyle.bkgColorRes))
        setSupportActionBar(helpToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(getHomeIcon(R.drawable.ic_close, toolbarStyle))
            if (title.isNotEmpty())
                it.title = title
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}