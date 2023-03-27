package pl.revo.merchant.ui.returns.search

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import pl.revo.merchant.common.IBaseView
import pl.revo.merchant.model.SearchData

interface SearchView : IBaseView {
    @StateStrategyType(AddToEndSingleStrategy::class)
    fun setData(items: List<SearchData>)
}