package pl.revo.merchant.ui.client.camera

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import pl.revo.merchant.BuildConfig
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseActivity

class CameraActivity : BaseActivity() {

    companion object {
        fun start(activity: Activity?, requestCode: Int) {
            val intent = Intent(activity, CameraActivity::class.java)
            activity?.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(BuildConfig.SCREEN_SHOOT_DISABLED) window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        val fragment = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CameraXFragment()
        else
            Camera2BasicFragment.newInstance()

        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.cameraContainer, fragment)
                .commit()
    }
}