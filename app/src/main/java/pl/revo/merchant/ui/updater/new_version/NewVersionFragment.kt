package pl.revo.merchant.ui.updater.new_version

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_new_version.*
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.ExtrasKey
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.model.UpdateData
import pl.revo.merchant.utils.granted

class NewVersionFragment : BaseFragment(), NewVersionView  {

    companion object {
        fun getInstance(data: UpdateData) : NewVersionFragment {
            val fragment = NewVersionFragment()
            fragment.setArguments(ExtrasKey.UPDATE, data)
            return fragment
        }

        private const val REQUEST_WRITE_STORAGE_PERMISSION = 2001
    }

    @InjectPresenter
    lateinit var presenter: NewVersionPresenter

    @ProvidePresenter
    fun providePresenter() = NewVersionPresenter(injector)

    override val layoutResId = R.layout.fragment_new_version
    override val titleResId = 0
    override val homeIconType = HomeIconType.NONE
    override val toolbarStyle = ToolbarStyle.LIGHT

    private lateinit var updateData: UpdateData

    override fun initView(view: View, savedInstanceState: Bundle?) {
        updateData = arguments?.getSerializable(ExtrasKey.UPDATE.name) as UpdateData

        updateHintView.text = updateData.installationMessage

        updateButton.setOnClickListener { loadApk() }
    }

    private fun loadApk() {
        if (updateData.apkUrl.isNotEmpty()) {
            if (activity.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                presenter.loadApkFile(updateData.apkUrl)
            } else {
                requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_WRITE_STORAGE_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            val granted = grantResults.contains(PackageManager.PERMISSION_GRANTED)
            if (granted) {  presenter.loadApkFile(updateData.apkUrl) }
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun installUpdate(filePath: Uri) {
        if (filePath.toString().isNotEmpty()) {
            val installIntent = Intent(Intent.ACTION_VIEW)
            installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val resInfoList = context?.packageManager?.queryIntentActivities(installIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            resInfoList
                    ?.map { it.activityInfo.packageName }
                    ?.forEach {
                        context?.grantUriPermission(
                                it,
                                filePath,
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    }

            installIntent.setDataAndType(filePath, "application/vnd.android.package-archive")
            startActivity(installIntent)
        }
    }
}