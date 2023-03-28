package merchant.mokka.ui.returns.search

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import merchant.mokka.common.IBaseView
import merchant.mokka.model.SearchData

interface SearchView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(items: List<SearchData>)
}