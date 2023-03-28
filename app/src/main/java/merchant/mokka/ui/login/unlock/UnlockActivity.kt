package merchant.mokka.ui.login.unlock

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_unlock.*
import merchant.mokka.BuildConfig
import merchant.mokka.R
import merchant.mokka.common.BaseActivity
import merchant.mokka.common.ExtrasKey
import merchant.mokka.model.UnlockData
import merchant.mokka.utils.SessionUtils
import merchant.mokka.utils.granted
import merchant.mokka.widget.KeyboardWidget
import merchant.mokka.widget.PinState
import merchant.mokka.widget.PinWidget

class UnlockActivity : BaseActivity(), UnlockView {

    companion object {
        private const val REQUEST_WRITE_STORAGE_PERMISSION = 2001
    }

    @InjectPresenter
    lateinit var presenter: UnlockPresenter

    @ProvidePresenter
    fun providePresenter() = UnlockPresenter(injector)

    private val sessionUtils: SessionUtils by injector.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        if(BuildConfig.SCREEN_SHOOT_DISABLED) window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock)

        initToolbar()
        initKeyboard()

        unlockLogin.setOnClickListener {
            val data = Intent()
            data.putExtra(ExtrasKey.UNLOCK.name, UnlockData.LOGIN)
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(unlockToolbar)
    }

    private fun initKeyboard() {
        for (i in 0..3) {
            unlockCode.addView(PinWidget(this))
        }
        unlockKeyboard.setPins(unlockCode)
        unlockKeyboard.setNextListener(nextListener)
    }

    private val nextListener = object : KeyboardWidget.OnNextListener{
        override fun next() {
            presenter.onUnlock(unlockKeyboard.getValue())
        }
    }

    override fun setUserName(userName: String) {
        unlockUserName.text = userName
        unlockNotYou.text = getString(R.string.lock_not_you)
    }

    override fun onErrorUnlock() {
        (0..3).map { unlockCode.getChildAt(it) as PinWidget }
            .forEach { it.setState(PinState.ERROR) }
    }

    override fun onSuccessUnlock() {
        val data = Intent()
        data.putExtra(ExtrasKey.UNLOCK.name, UnlockData.SUCCESS)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun showProgress() {
        getProgressDialog()?.show()
    }

    override fun hideProgress() {
        getProgressDialog()?.dismiss()
    }

    override fun onError(throwable: Throwable) {
        alert(getString(R.string.error_title), throwable.message.orEmpty())
    }

    override fun onError(errorRes: Int) {
        alert(getString(R.string.error_title), getString(errorRes))
    }

    override fun onBackPressed() {  }

    override fun onUserInteraction() {
        super.onUserInteraction()
        sessionUtils.onUserInteraction()
    }

    override fun confirmUpdate(version: String) {
        if (this.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            loadApkFile()
        else
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_STORAGE_PERMISSION
            )
    }

    private fun loadApkFile() {
        AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(getString(R.string.update_found))
                .setPositiveButton(R.string.update_found_ok, { _, _ -> presenter.loadApkFile() })
                .setNegativeButton(R.string.update_found_cancel, { _, _ -> })
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            val granted = grantResults.contains(PackageManager.PERMISSION_GRANTED)
            if (granted) {
                loadApkFile()
            }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun showCompleteUpdate(filePath: Uri) {
        AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(getString(R.string.update_download_complete))
                .setPositiveButton(R.string.update_install) { _, _ -> installUpdate(filePath) }
                .setNegativeButton(R.string.update_found_cancel) { _, _ -> }
                .show()
    }

    private fun installUpdate(filePath: Uri) {
        if (filePath.toString().isNotEmpty()) {
            val installIntent = Intent(Intent.ACTION_VIEW)
            installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val resInfoList = packageManager.queryIntentActivities(installIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            resInfoList
                    .map { it.activityInfo.packageName }
                    .forEach {
                        grantUriPermission(
                                it,
                                filePath,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

            installIntent.setDataAndType(filePath, "application/vnd.android.package-archive")
            startActivity(installIntent)

            onSuccessUnlock()
        }
    }
}