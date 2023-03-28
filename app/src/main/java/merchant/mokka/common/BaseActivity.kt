package merchant.mokka.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.MvpDelegate
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import kotlinx.android.synthetic.main.dialog_progress.view.*
import merchant.mokka.ui.main.help.HelpActivity
import merchant.mokka.utils.isBgLocale
import merchant.mokka.utils.updateBaseContextLocale
import merchant.mokka.R
import merchant.mokka.BuildConfig

@SuppressLint("Registered")
open class BaseActivity : KodeinAppCompatActivity() {

    private var delegate: MvpDelegate<out BaseActivity>? = null
    private var progressDialog: AlertDialog? = null

    private fun getMvpDelegate(): MvpDelegate<out BaseActivity> {
        if (delegate == null)
            delegate = MvpDelegate(this)

        return delegate!!
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        getMvpDelegate().onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        getMvpDelegate().onAttach()
    }

    override fun onResume() {
        super.onResume()
        getMvpDelegate().onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        getMvpDelegate().onSaveInstanceState(outState)
        getMvpDelegate().onDetach()
    }

    override fun onStop() {
        super.onStop()
        getMvpDelegate().onDetach()
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        getMvpDelegate().onDestroyView()

        if (isFinishing) {
            getMvpDelegate().onDestroy()
        }
    }

    fun alert(
        title: String? = null,
        message: String? = null,
        positive: (() -> Unit)? = null,
        positiveButtonResId: Int = R.string.button_ok,
        negative: (() -> Unit)? = null,
        negativeButtonResId: Int = R.string.button_cancel,
        cancelable: Boolean = true,
        canceledOnTouchOutside: Boolean = true,
        view: View? = null
    ) {
        val builder = AlertDialog.Builder(this)
                .setPositiveButton(positiveButtonResId) { _, _ -> positive?.invoke() }
                .setCancelable(cancelable)

        message?.let { builder.setMessage("\n" + message + "\n") }
        view?.let { builder.setView(view) }
        title?.let { builder.setTitle(title) }
        negative?.let { builder.setNegativeButton(negativeButtonResId) { _, _ -> negative.invoke() } }

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)
        dialog.show()

        val messageView = dialog.findViewById(android.R.id.message) as TextView?
        messageView?.movementMethod = LinkMovementMethod.getInstance()
        messageView?.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY)
        else Html.fromHtml(message)
    }

    fun openHelp(toolbarStyle: ToolbarStyle, helpRes: String, title: String) {
        val assetsList = resources.assets.list("raw")
        val locale = when {
            isBgLocale() -> "bg"
            else -> ""
        }
        val fileName =
                when {
                    assetsList?.contains("${helpRes}_$locale") == true -> "${helpRes}_$locale"
                    assetsList?.contains(helpRes) == true -> helpRes
                    else -> ""
                }


        if (fileName.isNotEmpty()) {
            val intent = Intent(this, HelpActivity::class.java)
            intent.putExtra(ExtrasKey.HELP_TOOLBAR.name, toolbarStyle)
            intent.putExtra(ExtrasKey.HELP_RES_ID.name, fileName)
            intent.putExtra(ExtrasKey.HELP_TITLE.name, title)
            startActivity(intent)
        }
    }

    @SuppressLint("InflateParams")
    fun getProgressDialog() : AlertDialog? {
        if (progressDialog == null || progressDialog?.isShowing != true) {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_progress, null)

            dialogView.progress.start()

            dialogBuilder.setView(dialogView)
            dialogBuilder.setCancelable(false)
            progressDialog = dialogBuilder.create()
            progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return progressDialog
    }

    fun getWindowsHeight() : Int {
        val rect = Rect()
        val rootView = window.decorView
        rootView.getWindowVisibleDisplayFrame(rect)
        return rect.bottom
    }

    fun getDemoClickedFragment() : IDemoClickedView? {
        return supportFragmentManager.fragments.firstOrNull {
            it is IDemoClickedView
        } as? IDemoClickedView
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(updateBaseContextLocale(newBase))
    }
}