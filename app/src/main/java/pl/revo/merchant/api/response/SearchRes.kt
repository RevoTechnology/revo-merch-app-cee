package pl.revo.merchant.api.response

import pl.revo.merchant.model.SearchData

data class SearchRes(
        val orders: List<SearchData>
)