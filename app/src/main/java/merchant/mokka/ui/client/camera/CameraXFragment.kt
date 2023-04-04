package merchant.mokka.ui.client.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_camerax.*
import merchant.mokka.R
import merchant.mokka.common.AbsKodeinFragment
import merchant.mokka.common.BaseActivity
import merchant.mokka.common.ExtrasKey
import merchant.mokka.utils.camerax.CameraxHelper
import merchant.mokka.utils.granted
import merchant.mokka.utils.photoDir
import merchant.mokka.utils.visible

private const val REQUEST_CAMERA_PERMISSION = 2001

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraXFragment : AbsKodeinFragment(), CameraView {
    @InjectPresenter
    lateinit var presenter: CameraPresenter

    @ProvidePresenter
    fun providePresenter() = CameraPresenter(injector)

    private val cameraxHelper by lazy {
        CameraxHelper(
                caller = this,
                previewView = previewView,
                filesDirectory = requireActivity().photoDir,
                onPictureTaken = { file, _ -> presenter.onTakePicture(requireActivity(), file) },
                onError = { (requireActivity() as BaseActivity).alert(getString(R.string.error_title), it.message.orEmpty()) }
        )
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_camerax, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        cameraxHelper.start()
        pictureButton.setOnClickListener { takePictureClicked() }
        savePhotoButton.setOnClickListener { saveButtonClicked() }
        refreshPhotoButton.setOnClickListener { onRefreshClicked() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                val granted = grantResults.contains(PackageManager.PERMISSION_GRANTED)
                if (granted) takePictureClicked()
                else {
                    (requireActivity() as BaseActivity).alert(getString(R.string.error_title), getString(R.string.scan_camera_permission))
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun takePictureClicked() {
        val isNeedStoragePermission = Build.VERSION.SDK_INT <= Build.VERSION_CODES.R
        val isGranted = if (isNeedStoragePermission) {
            activity.granted(Manifest.permission.CAMERA) &&
            activity.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }else {
            activity.granted(Manifest.permission.CAMERA)
        }
        if (isGranted) {
            pictureButton.visible(visible = false, isInvisible = true)
            cameraxHelper.takePicture()
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun onRefreshClicked() = onStateChanged(CameraView.State.CAMERA)
    private fun saveButtonClicked() = requireActivity().apply {
        cameraxHelper.destroy()

        val data = Intent().putExtra(ExtrasKey.PHOTO_FILE.name, presenter.file)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    // CameraView
    override fun onPicture(bitmap: Bitmap) {
        onStateChanged(CameraView.State.PICTURE_TAKEN)
        imageview.setImageBitmap(bitmap)
    }

    override fun onStateChanged(state: CameraView.State) {
        val viewsCameraState = listOf(pictureButton, previewView)
        val viewsPictureTakenState = listOf(imageview, savePhotoButton, refreshPhotoButton)

        when (state) {
            CameraView.State.CAMERA -> {
                viewsCameraState.forEach { it.visible(true) }
                viewsPictureTakenState.forEach { it.visible(visible = false, isInvisible = true) }
            }

            CameraView.State.PICTURE_TAKEN -> {
                viewsPictureTakenState.forEach { it.visible(true) }
                viewsCameraState.forEach { it.visible(visible = false, isInvisible = true) }
            }
        }
    }
}