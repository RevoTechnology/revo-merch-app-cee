package merchant.mokka.ui.root

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.drawer_header_root.view.*
import merchant.mokka.BuildConfig
import merchant.mokka.R
import merchant.mokka.common.*
import merchant.mokka.model.AgentData
import merchant.mokka.model.UnlockData
import merchant.mokka.pref.Prefs
import merchant.mokka.ui.login.unlock.UnlockActivity
import merchant.mokka.ui.main.dashboard.DashboardFragment
import merchant.mokka.ui.purchase.purchase.PurchaseFragment
import merchant.mokka.ui.returns.search.SearchFragment
import merchant.mokka.utils.*
import ru.terrakok.cicerone.NavigatorHolder


class RootActivity : BaseActivity(), RootView {

    companion object {
        private const val REQUEST_UNLOCK = 3010
        private const val REQUEST_WRITE_STORAGE_PERMISSION = 2001

        private const val LOG_STEP = "on_initial_start"
    }

    @InjectPresenter
    lateinit var presenter: RootPresenter

    @ProvidePresenter
    fun providePresenter(): RootPresenter {
        return RootPresenter(injector)
    }

    private val navigatorHolder: NavigatorHolder by injector.instance()
    private val sessionUtils: SessionUtils by injector.instance()

    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        if(BuildConfig.SCREEN_SHOOT_DISABLED) window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_root)

        viewflipper.postDelayed({ viewflipper.displayedChild = 1 }, 3_000)

        sessionUtils.setActivity(object : SessionUtils.ManagedActivity {
            override fun logout() {
                if (SessionRoute.workRoute) {
                    startActivityForResult(
                            Intent(this@RootActivity, UnlockActivity::class.java),
                            REQUEST_UNLOCK
                    )
                    presenter.setFrameVisibility(false)
                }
            }
        })

        if (savedInstanceState != null) {
            sessionUtils.onCreate(savedInstanceState, System.currentTimeMillis())
        }

        initToolbar()
        initNavigationMenu()

        supportFragmentManager.addOnBackStackChangedListener {
            when (getCurrentFragment()) {
                is DashboardFragment -> rootNavigationView.menu.findItem(
                        R.id.item_dashboard)?.isChecked = true
                is PurchaseFragment -> rootNavigationView.menu.findItem(
                        R.id.item_make_purchase)?.isChecked = true
                is SearchFragment -> rootNavigationView.menu.findItem(
                        R.id.item_return_purchase)?.isChecked = true
            }
        }
        checkLocaleUpdates(this) {
            presenter.checkApkVersion(getDeviceInfo(LOG_STEP))
            presenter.handleLamoda(intent.getStringExtra("json_data"))
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(RootNavigator(this, R.id.rootContainer))
        sessionUtils.onResume()
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.rootContainer)
    }

    //region ================= SessionUtils =================

    override fun onRestart() {
        super.onRestart()
        sessionUtils.onRestart()
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        sessionUtils.onPause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        sessionUtils.onSaveInstanceState(outState)
    }

    override fun onUserInteraction() {
        sessionUtils.onUserInteraction()
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        hideProgress()

        if (requestCode == REQUEST_UNLOCK && resultCode == Activity.RESULT_OK && data != null) {
//            val unlock = data.extras?.get(ExtrasKey.UNLOCK.name) as UnlockData
            val unlock = data.extras?.get(ExtrasKey.UNLOCK.name) as? UnlockData
            if (unlock == UnlockData.LOGIN) {
                initToolbarStyle(R.string.sign_in_title, ToolbarStyle.LIGHT, HomeIconType.NONE,
                        true)
                SessionRoute.workRoute = false
                presenter.onSignOutClick()
            } else {
                presenter.setFrameVisibility(true)
            }
        } else {
            getCurrentFragment()?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.setLocale())
    }

    //endregion

    //region ================= InitActivity =================

    private fun initToolbar() {
        setSupportActionBar(rootToolbar)
        toggle = object : ActionBarDrawerToggle(this, rootDrawerLayout, rootToolbar, R.string.drawer_open, R.string.drawer_close) {}
        rootDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun initToolbarStyle(
        @StringRes titleRes: Int,
        toolbarStyle: ToolbarStyle,
        homeIcon: HomeIconType,
        showDemo: Boolean,
        @StringRes subtitleRes: Int? = null
    ) {
        rootToolbar.apply {
            post {
                setToolbarTextViewsMarquee()
                title = if (titleRes == 0) "" else getString(titleRes)
                subtitle = subtitleRes?.let { getString(it) }
                setTitleTextColor(ContextCompat.getColor(this@RootActivity, toolbarStyle.titleColorRes))
                setBackgroundColor(ContextCompat.getColor(this@RootActivity, toolbarStyle.bkgColorRes))
            }
        }

        if (showDemo) {
            rootDemoButton.visibility = View.VISIBLE
            rootDemoButton.setOnClickListener { getDemoClickedFragment()?.onDemoClick() }
        } else {
            rootDemoButton.visibility = View.GONE
        }

        supportActionBar?.let { aBar ->
            when (homeIcon) {
                HomeIconType.NONE -> {
                    rootDrawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    toggle.isDrawerIndicatorEnabled = false
                    aBar.setHomeAsUpIndicator(null)
                    aBar.setDisplayHomeAsUpEnabled(false)
                    toggle.toolbarNavigationClickListener = null
                }
                HomeIconType.CLOSE,
                HomeIconType.BACK_ARROW -> {
                    rootDrawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

                    toggle.isDrawerIndicatorEnabled = false
                    aBar.setHomeAsUpIndicator(getHomeIcon(homeIcon.iconRes, toolbarStyle))
                    aBar.setDisplayHomeAsUpEnabled(true)
                    if (toggle.toolbarNavigationClickListener == null)
                        toggle.setToolbarNavigationClickListener { onBackPressed() }

                }
                HomeIconType.MENU -> {
                    rootDrawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
                    aBar.setDisplayHomeAsUpEnabled(false)
                    toggle.isDrawerIndicatorEnabled = true
                    toggle.drawerArrowDrawable.color =
                            ContextCompat.getColor(this, toolbarStyle.titleColorRes)
                    toggle.toolbarNavigationClickListener = null
                }
            }

            toggle.syncState()
        }
    }

    private fun initNavigationMenu() {
        rootNavigationView.setNavigationItemSelectedListener { item ->
            rootDrawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.item_dashboard -> if (getCurrentFragment() !is DashboardFragment)
                    presenter.onDashboardClick()
                R.id.item_make_purchase -> if (getCurrentFragment() !is PurchaseFragment)
                    presenter.onMakePurchaseClick()
                R.id.item_return_purchase -> if (getCurrentFragment() !is SearchFragment)
                    presenter.onMakeReturnClick()
                R.id.item_sign_out -> presenter.onSignOutClick()
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                back()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        back()
    }

    private fun back() {
        val currentFragment = getCurrentFragment()

        if (currentFragment is BaseFragment) {
            if (!currentFragment.onBackPressed())
                presenter.onBackCommandClick()
        } else
            presenter.onBackCommandClick()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                v.clearFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    //endregion

    //region ================= RootView =================

    override fun onError(throwable: Throwable) {
        alert(getString(R.string.error_title), throwable.message.orEmpty())
    }

    override fun onError(errorRes: Int) {
        alert(getString(R.string.error_title), getString(errorRes))
    }

    override fun showProgress() {
        getProgressDialog()?.show()
    }

    override fun hideProgress() {
        getProgressDialog()?.dismiss()
    }

    override fun showToolbar() {
        rootAppBar.visibility = View.VISIBLE
    }

    //endregion

    //region ================= SetData =================

    fun setAgentData(agentData: AgentData, storeIndex: Int) {
        rootNavigationView.getHeaderView(0)
                .let { view ->
                    val agentFullName = agentData.firstName + " " + agentData.lastName
                    view.mainDrawerUserName?.text = agentFullName

                    val agentStore =
                            when {
                                agentData.stores.isEmpty() -> ""
                                storeIndex in 0 until agentData.stores.size -> {
                                    Prefs.currentStoreIdx = storeIndex
                                    Prefs.currentStoreId = agentData.stores[storeIndex].store.id

                                    Prefs.tariffMin = agentData.stores[storeIndex].store.tariffMin
                                    Prefs.tariffMax = agentData.stores[storeIndex].store.tariffMax

                                    agentData.stores[storeIndex].store.traderName + " | " + agentData.stores[storeIndex].store.name
                                }
                                else -> {
                                    Prefs.currentStoreIdx = 0
                                    Prefs.currentStoreId = agentData.stores[0].store.id

                                    Prefs.tariffMin = agentData.stores[0].store.tariffMin
                                    Prefs.tariffMax = agentData.stores[0].store.tariffMax

                                    agentData.stores[0].store.traderName + " | " + agentData.stores[0].store.name
                                }
                            }

                    view.mainDrawerUserStore?.text = agentStore

                    if (agentData.avatar.isNotEmpty())
                        Glide.with(this)
                                .load(agentData.avatar)
                                .apply(RequestOptions()
                                        .error(R.drawable.avatar_bg)
                                        .placeholder(R.drawable.avatar_bg)
                                )
                                .into(view.mainDrawerAvatar)
                }
    }

    override fun onDestroy() {
        Glide.get(this).clearMemory()
        super.onDestroy()
    }

    override fun setRootFrameVisibility(visibility: Boolean) {
        runOnUiThread {
            rootContainer.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
        }
    }

    //endregion

    //region ================= Update Apk =================

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
                .setPositiveButton(R.string.update_found_ok) { _, _ -> presenter.loadApkFile() }
                .setNegativeButton(R.string.update_found_cancel) { _, _ -> }
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_STORAGE_PERMISSION -> {
                val granted = grantResults.contains(PackageManager.PERMISSION_GRANTED)
                if (granted) {
                    loadApkFile()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
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
        }
    }

    //endregion
}