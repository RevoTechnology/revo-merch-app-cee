package merchant.mokka.ui.client.document

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
import merchant.mokka.utils.granted
import merchant.mokka.utils.isVisible
import merchant.mokka.utils.toAlpha
import merchant.mokka.utils.visible
import java.io.File

class DocumentsFragment : BaseFragment(), DocumentsView {

    companion object {
        fun getInstance(loan: LoanData): DocumentsFragment {
            val fragment = DocumentsFragment()
            fragment.setArguments(ExtrasKey.LOAN, loan)
            return fragment
        }

        private const val REQUEST_CAMERA_PERMISSION = 2001

        private const val REQUEST_CHECK_PHOTO_NAME = 2003
        private const val REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT = 2004
        private const val REQUEST_CHECK_PHOTO_LIVING_ADDRES = 2005
    }

    override val layoutResId = R.layout.fragment_documents
    override val titleResId = R.string.documents_title
    override val homeIconType = HomeIconType.BACK_ARROW
    override val toolbarStyle = ToolbarStyle.ACCENT

    @InjectPresenter
    lateinit var presenter: DocumentsPresenter

    @ProvidePresenter
    fun providePresenter() = DocumentsPresenter(injector)

    private var request = 0

    private lateinit var loan: LoanData

    override fun initView(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        loan = arguments?.getSerializable(ExtrasKey.LOAN.name) as LoanData

        with(documentsNameView.text.isNullOrEmpty() || (isDocumentMissing(IdPhoto.PHOTO_NAME) && !loan.isNewClient)) {
            documentsNameCard.visible(!this)
            if (!this) showNameImage()
        }

        with(documentsClientWithPassportView.text.isNullOrEmpty() || (isDocumentMissing(IdPhoto.PHOTO_CLIENT_WITH_PASSPORT) && !loan.isNewClient)) {
            documentsClientWithPassportCard.visible(!this)
            if (!this) showClientWithPassportImage()
        }

        with(documentsLivingAddressView.text.isNullOrEmpty() || (isDocumentMissing(IdPhoto.PHOTO_LIVING_ADDRESS) && !loan.isNewClient)) {
            documentsLivingAddressCard.visible(!this)
            if (!this) showLivingAddressImage()
        }

        documentsNameCard.setOnClickListener {
            request = REQUEST_CHECK_PHOTO_NAME
            selectPhoto()
        }
        documentsNameNewButton.setOnClickListener {
            request = REQUEST_CHECK_PHOTO_NAME
            selectPhoto()
        }

        documentsClientWithPassportCard.setOnClickListener {
            request = REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT
            selectPhoto()
        }
        documentsClientWithPassportButton.setOnClickListener {
            request = REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT
            selectPhoto()
        }

        documentsLivingAddressCard.setOnClickListener {
            request = REQUEST_CHECK_PHOTO_LIVING_ADDRES
            selectPhoto()
        }
        documentsLivingAddressButton.setOnClickListener {
            request = REQUEST_CHECK_PHOTO_LIVING_ADDRES
            selectPhoto()
        }

        documentsNextBtn.setOnClickListener { onNextButtonClick() }
        validate()
    }

    private fun onNextButtonClick() {
        if (loan.client?.confirmData == true)
            ClientPersonalDataDialog(context = requireContext(), loan = loan).show(onPositiveClick = { presenter.onNextClick(loan) })
        else
            presenter.onNextClick(loan)
    }

    private fun selectPhoto() {
        if (activity.granted(Manifest.permission.CAMERA) && activity.granted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            CameraActivity.start(activity, request)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun isDocumentMissing(idPhoto: IdPhoto) =
            loan.client?.missingDocuments?.contains(idPhoto.photoName) == false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_PHOTO_NAME || requestCode == REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT ||
                requestCode == REQUEST_CHECK_PHOTO_LIVING_ADDRES
        ) {
            if (resultCode == Activity.RESULT_OK) {
                val image = data?.extras?.get(ExtrasKey.PHOTO_FILE.name) as File?
                when (request) {
                    REQUEST_CHECK_PHOTO_NAME -> {
                        if (image != null) loan.clientIds.nameImage = image
                        showNameImage()
                    }
                    REQUEST_CHECK_PHOTO_CLIENT_WITH_PASSPORT -> {
                        if (image != null) loan.clientIds.clientWithPassportImage = image
                        showClientWithPassportImage()
                    }
                    REQUEST_CHECK_PHOTO_LIVING_ADDRES -> {
                        if (image != null) loan.clientIds.livingAddressImage = image
                        showLivingAddressImage()
                    }
                }
                request = 0
            }
        }
    }

    private fun showNameImage() {
        if (loan.clientIds.nameImage == null) {
            documentsNameImage.visibility = View.VISIBLE
            documentsNamePhoto.visibility = View.GONE
            documentsNameNewButton.visibility = View.GONE
        } else {
            documentsNameImage.visibility = View.GONE
            documentsNamePhoto.visibility = View.VISIBLE
            documentsNameNewButton.visibility = View.VISIBLE

            Glide.with(requireContext())
                    .load(loan.clientIds.nameImage)
                    .apply(RequestOptions().fitCenter())
                    .into(documentsNamePhoto)
        }
        validate()
    }

    private fun showClientWithPassportImage() {
        if (loan.clientIds.clientWithPassportImage == null) {
            documentsClientWithPassportImage.visibility = View.VISIBLE
            documentsSecondPhoto.visibility = View.GONE
            documentsClientWithPassportButton.visibility = View.GONE
        } else {
            documentsClientWithPassportImage.visibility = View.GONE
            documentsSecondPhoto.visibility = View.VISIBLE
            documentsClientWithPassportButton.visibility = View.VISIBLE

            Glide.with(requireContext())
                    .load(loan.clientIds.clientWithPassportImage)
                    .apply(RequestOptions().fitCenter())
                    .into(documentsSecondPhoto)
        }
        validate()
    }

    private fun showLivingAddressImage() {
        if (loan.clientIds.livingAddressImage == null) {
            documentsLivingAddressImage.visibility = View.VISIBLE
            documentsThirdPhoto.visibility = View.GONE
            documentsLivingAddressButton.visibility = View.GONE
        } else {
            documentsLivingAddressImage.visibility = View.GONE
            documentsThirdPhoto.visibility = View.VISIBLE
            documentsLivingAddressButton.visibility = View.VISIBLE

            Glide.with(requireContext())
                    .load(loan.clientIds.livingAddressImage)
                    .apply(RequestOptions().fitCenter())
                    .into(documentsThirdPhoto)
        }
        validate()
    }

    private fun isValid(view: View, file: File?, idPhoto: IdPhoto): Boolean {
        return if (!view.isVisible()) true
        else if (loan.isNewClient) file != null
        else loan.client?.missingDocuments?.contains(idPhoto.photoName) == false || file != null
    }

    private fun validate() {
        val nameImageValid = isValid(view = documentsNameCard,
                file = loan.clientIds.nameImage,
                idPhoto = IdPhoto.PHOTO_NAME)

        val clientWithPassportImageValid = isValid(view = documentsClientWithPassportCard,
                file = loan.clientIds.clientWithPassportImage,
                idPhoto = IdPhoto.PHOTO_CLIENT_WITH_PASSPORT)

        val livingAddressValid = isValid(view = documentsLivingAddressCard,
                file = loan.clientIds.livingAddressImage,
                idPhoto = IdPhoto.PHOTO_LIVING_ADDRESS)


        val isValid = nameImageValid && clientWithPassportImageValid && livingAddressValid
        documentsNextBtn.isEnabled = isValid
        documentsNextBtn.alpha = isValid.toAlpha()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                val granted = grantResults.contains(PackageManager.PERMISSION_GRANTED)
                if (granted) {
                    selectPhoto()
                } else {
                    alert(getString(R.string.error_title), getString(R.string.scan_camera_permission))
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
}