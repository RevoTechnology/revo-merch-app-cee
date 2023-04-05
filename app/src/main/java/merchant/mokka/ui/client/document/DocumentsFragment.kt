package merchant.mokka.ui.client.document

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_documents.*
import merchant.mokka.R
import merchant.mokka.common.BaseFragment
import merchant.mokka.common.ExtrasKey
import merchant.mokka.common.HomeIconType
import merchant.mokka.common.ToolbarStyle
import merchant.mokka.model.IdPhoto
import merchant.mokka.model.LoanData
import merchant.mokka.ui.client.camera.CameraActivity
import merchant.mokka.ui.client.profile_ru.ClientPersonalDataDialog
import merchant.mokka.utils.*
import java.io.File
import java.security.Permission

private const val REQUEST_CHECK_PHOTO_NAME = 2003
private const val REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT = 2004

class DocumentsFragment : BaseFragment(), DocumentsView {

    @InjectPresenter
    lateinit var presenter: DocumentsPresenter

    @ProvidePresenter
    fun providePresenter() = DocumentsPresenter(injector)

    override val layoutResId = R.layout.fragment_documents
    override val titleResId = R.string.documents_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    private lateinit var loan: LoanData

    private var requestCode = 0
        set(value) {
            field = value
            if (value != 0) {
                selectPhoto(value)
            }
        }

    private val isRequestCodeValid get() =
                requestCode == REQUEST_CHECK_PHOTO_NAME ||
                requestCode == REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT

    private val isCameraGrander: Boolean get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.granted(Manifest.permission.CAMERA)
        }else {
            activity.granted(Manifest.permission.CAMERA) &&
            activity.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private val IdPhoto.isDocumentMissing get() =
        loan.client?.missingDocuments?.contains(photoName) == true

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData
        initializeButtonVisibility()
        initializeDocumentButtonsClickListeners()
        initializeNextButtonClickListener()
        validate()
    }

    private fun initializeButtonVisibility() {
        val isNameMissing = IdPhoto.PHOTO_NAME.isDocumentMissing
        val isPassportMissing = IdPhoto.PHOTO_CLIENT_WITH_PASSPORT.isDocumentMissing

        documentsNameButton.isVisible = isNameMissing
        documentsClientWithPassportButton.isVisible = isPassportMissing && isPlLocale()
    }

    private fun initializeDocumentButtonsClickListeners() {
        val listener = View.OnClickListener {
            requestCode = when (it.id) {
                R.id.documentsNameButton -> REQUEST_CHECK_PHOTO_NAME
                R.id.documentsClientWithPassportButton -> REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT
                else -> 0
            }
        }
        documentsNameButton.setOnClickListener(listener)
        documentsClientWithPassportButton.setOnClickListener(listener)
    }

    private fun initializeNextButtonClickListener() {
        documentsNextBtn.setOnClickListener {
            if (loan.client?.confirmData == true) {
                ClientPersonalDataDialog(
                    context = requireContext(),
                    loan = loan
                ).show(
                    onPositiveClick = { presenter.onNextClick(loan) }
                )
            }
            else presenter.onNextClick(loan)
        }
    }

    private fun isValid(view: View, file: File?, idPhoto: IdPhoto): Boolean {
        return if (!view.isVisible()) true
        else if (loan.isNewClient) file != null
        else loan.client?.missingDocuments?.contains(idPhoto.photoName) == false || file != null
    }

    private fun validate() {
        val nameImageValid = isValid(
            view = documentsNameButton,
            file = loan.clientIds.nameImage,
            idPhoto = IdPhoto.PHOTO_NAME
        )
        val clientWithPassportImageValid = isValid(
            view = documentsClientWithPassportButton,
            file = loan.clientIds.clientWithPassportImage,
            idPhoto = IdPhoto.PHOTO_CLIENT_WITH_PASSPORT
        )
        val isValid = nameImageValid && clientWithPassportImageValid
        documentsNextBtn.isEnabled = isValid
        documentsNextBtn.alpha = isValid.toAlpha()
    }

    private fun selectPhoto(requestCode: Int) {
        if (isCameraGrander) {
            CameraActivity.start(activity, requestCode)
        } else {
           cameraPermissionChecker.launch(
               arrayOf(
                   Manifest.permission.CAMERA,
                   Manifest.permission.WRITE_EXTERNAL_STORAGE
               )
           )
        }
    }

    private val cameraPermissionChecker = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var cameraGranted = false
        var storageGranted = false
        permissions.entries.forEach {
            when (it.key) {
                Manifest.permission.CAMERA -> cameraGranted = it.value
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> storageGranted = it.value
            }
        }
        val isNeedStoragePermission = Build.VERSION.SDK_INT <= Build.VERSION_CODES.R
        val isGranted = if (isNeedStoragePermission) cameraGranted && storageGranted else cameraGranted
        if (isGranted) {
            selectPhoto(requestCode)
        }else {
            alert(getString(R.string.error_title), getString(R.string.scan_camera_permission))
        }
    }

    private fun checkActivityResult(code: Int, image: File?) {
        when (code) {
            REQUEST_CHECK_PHOTO_NAME -> {
                if (image != null) loan.clientIds.nameImage = image
                documentsNameButton.setImage(image)
            }
            REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT -> {
                if (image != null) loan.clientIds.clientWithPassportImage = image
                documentsClientWithPassportButton.setImage(image)
            }
        }
        validate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (isRequestCodeValid && resultCode == Activity.RESULT_OK) {
            val image = data?.extras?.get(ExtrasKey.PHOTO_FILE.name) as File?
            checkActivityResult(requestCode, image)
            this@DocumentsFragment.requestCode = 0
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_close -> {
                confirmShowDashboard { presenter.showDashboardScreen() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun getInstance(loan: LoanData): DocumentsFragment {
            val fragment = DocumentsFragment()
            fragment.setArguments(ExtrasKey.LOAN, loan)
            return fragment
        }
    }
}