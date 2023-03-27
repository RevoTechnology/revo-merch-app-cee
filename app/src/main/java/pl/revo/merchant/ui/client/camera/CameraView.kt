package pl.revo.merchant.ui.client.camera

import android.graphics.Bitmap
import pl.revo.merchant.common.IBaseView

interface CameraView : IBaseView {
    enum class State {
        CAMERA, PICTURE_TAKEN
    }

    fun onPicture(bitmap: Bitmap)
    fun onStateChanged(state: State)

    override fun onFailure() {}
    override fun onMessage(message: String) {}
    override fun onError(error: String) {}
    override fun onError(throwable: Throwable) {}
    override fun onError(errorRes: Int) {}
    override fun showProgress() {}
    override fun hideProgress() {}
}