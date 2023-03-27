package pl.revo.merchant.common

import android.os.Bundle
import android.util.Log
import android.view.*
import pl.revo.merchant.R
import pl.revo.merchant.api.error.*
import pl.revo.merchant.ui.root.RootActivity
import pl.revo.merchant.utils.isRoLocale
import java.io.Serializable

abstract class BaseFragment : AbsKodeinFragment(), IBaseView {


    protected abstract val layoutResId: Int
    protected abstract val titleResId: Int
    protected open val subtitleResId: Int? = null
    protected abstract val homeIconType: HomeIconType
    protected abstract val toolbarStyle: ToolbarStyle

    //region ================= Fragment =================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("fragment_lifecycle", "$javaClass")
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as RootActivity).initToolbarStyle(
                titleRes = titleResId,
                homeIcon = homeIconType,
                toolbarStyle = toolbarStyle,
                showDemo = showDemo(),
                subtitleRes = subtitleResId
        )

        initView(view, savedInstanceState)
    }

    open fun showDemo() = false

    protected abstract fun initView(view: View, savedInstanceState: Bundle?)

    open fun onBackPressed() = false

    protected fun openHelp(toolbarStyle: ToolbarStyle, helpRes: String, title: String) {
        (activity as BaseActivity).openHelp(toolbarStyle, helpRes, title)
    }

    protected fun openHelp(toolbarStyle: ToolbarStyle, helpRes: String) {
        (activity as BaseActivity).openHelp(toolbarStyle, helpRes, "")
    }

    protected fun setArguments(keyName: ExtrasKey, data: Serializable) {
        val args = Bundle()
        args.putSerializable(keyName.name, data)
        arguments = args
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.item_help)?.isVisible = !isRoLocale()
        super.onCreateOptionsMenu(menu, inflater)
    }

    protected fun confirmShowDashboard(positive: () -> Unit) {
        alert(
                title = "",
                message = requireContext().getString(R.string.dashboard_confirm_return),
                positive = { positive.invoke() },
                negative = { }
        )
    }

    //endregion

    //region ================= Mvp =================


    //endregion

    //region ================= IBaseView =================

    override fun onFailure() {
        //(activity as BaseActivity).onFailure()
    }

    override fun onMessage(message: String) {
        (activity as BaseActivity).alert("", message)
    }

    override fun onError(error: String) {
        (activity as BaseActivity).alert(getString(R.string.error_title), error)
    }

    override fun onError(throwable: Throwable) {
        when {
            throwable is UnknownErr -> onError(getString(R.string.err_unknown))
            throwable is NetworkAvailableErr -> onError(getString(R.string.err_network_is_not_available))
            throwable is ApiNotImplementErr -> onError(getString(R.string.err_api_not_implemented))
            throwable is UnAuthorizedErr -> onError(getString(R.string.error_not_authorized))
            throwable is ServerException -> onError(getString(R.string.err_server))
            throwable.message.isNullOrEmpty() -> onError(getString(R.string.err_unknown))
            else -> onError(throwable.message.orEmpty())
        }
    }

    override fun onError(errorRes: Int) {
        onError(getString(errorRes))
    }

    override fun showProgress() {
        (activity as? BaseActivity)?.getProgressDialog()?.show()
    }

    override fun hideProgress() {
        (activity as? BaseActivity)?.getProgressDialog()?.dismiss()
    }

    fun alert(
            title: String? = null,
            message: String,
            positive: (() -> Unit)? = null,
            positiveButtonResId: Int = R.string.button_ok,
            negative: (() -> Unit)? = null,
            negativeButtonResId: Int = R.string.button_cancel,
            cancelable: Boolean = true,
            canceledOnTouchOutside: Boolean = true
    ) {
        (activity as BaseActivity).alert(title, message, positive, positiveButtonResId, negative, negativeButtonResId, cancelable, canceledOnTouchOutside)
    }

    //endregion
}