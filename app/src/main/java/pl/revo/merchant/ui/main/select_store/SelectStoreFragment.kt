package pl.revo.merchant.ui.main.select_store

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_select_store.*
import kotlinx.android.synthetic.main.fragment_select_store.view.*
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseFragment
import pl.revo.merchant.common.BaseRecyclerViewAdapter
import pl.revo.merchant.common.HomeIconType
import pl.revo.merchant.common.ToolbarStyle
import pl.revo.merchant.model.AgentData
import pl.revo.merchant.model.StoreData
import pl.revo.merchant.ui.root.RootActivity
import pl.revo.merchant.utils.getDeviceInfo

class SelectStoreFragment : BaseFragment(), SelectStoreView {

    companion object {
        fun getInstance() = SelectStoreFragment()
    }

    override val layoutResId = R.layout.fragment_select_store
    override val titleResId = R.string.select_store_title
    override val homeIconType = HomeIconType.NONE
    override val toolbarStyle = ToolbarStyle.LIGHT

    @InjectPresenter
    lateinit var presenter : SelectStorePresenter

    @ProvidePresenter
    fun providePresenter() = SelectStorePresenter(injector)

    private lateinit var adapter : BaseRecyclerViewAdapter<StoreData>

    override fun initView(view: View, savedInstanceState: Bundle?) {
        with (view) {
            selectStoreList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = BaseRecyclerViewAdapter(
                    layout = R.layout.item_select_store,
                    items = mutableListOf(),
                    holderFactory = { SelectStoreHolder(it) },
                    onItemClick = { _, position ->
                        presenter.onItemClick(position)
                    }
            )
            selectStoreList.adapter = adapter
        }

        selectStoreBack.setOnClickListener { presenter.onBackClick() }
    }

    override fun setAgentInfo(agentData: AgentData?) {
        selectStoreAgentName.text = agentData?.firstName
        adapter.addItems(
                agentData?.stores?.map { it.store }.orEmpty()
        )
    }

    override fun updateRootAgentInfo(agentData: AgentData?, position: Int) {
        agentData?.let {
            (activity as RootActivity).setAgentData(it, position)
        }
    }

    override fun onBackPressed(): Boolean {
        alert(
                title = "",
                message = requireContext().getString(R.string.select_store_confirm_logout),
                positive = { presenter.onBackClick() },
                negative = { }
        )
        return true
    }

    override fun updateDeviceInfo(logStep: String) {
        presenter.deviceLogs(context.getDeviceInfo(logStep))
    }
}