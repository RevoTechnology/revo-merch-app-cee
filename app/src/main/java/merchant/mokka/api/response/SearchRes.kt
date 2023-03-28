package merchant.mokka.api.response

import merchant.mokka.model.SearchData

data class SearchRes(
        val orders: List<SearchData>
)