package pl.revo.merchant.ui.client.camera

import android.annotation.SuppressLint
import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.github.salomonbrys.kodein.KodeinInjector
import pl.revo.merchant.common.BasePresenter
import pl.revo.merchant.utils.camerax.ImagexSaver
import java.io.File

@InjectViewState
class CameraPresenter(injector: KodeinInjector) : BasePresenter<CameraView>(injector) {
    var file: File? = null

    @SuppressLint("CheckResult")
    fun onTakePicture(context: Context, file: File) {
        ImagexSaver.onTakePicture(
                context = context,
                iFile = file,
                onFileReady = { this.file = it },
                onBitmapReady = { viewState.onPicture(it) },
                onProgressChanged = {
                    if (it) viewState.showProgress()
                    else viewState.hideProgress()
                },
                onError = { viewState.onError(it) }
        )
    }
}