package pl.revo.merchant.ui.purchase.calculator

import android.view.LayoutInflater
import pl.revo.merchant.R
import pl.revo.merchant.common.BaseRecyclerViewAdapter
import pl.revo.merchant.model.TariffData

class CalculatorAdapter(
        val inflater: LayoutInflater,
        items: MutableList<TariffData>,
        onItemClick: ((item: TariffData, position: Int) -> Unit)? = null
) : BaseRecyclerViewAdapter<TariffData>(
        layout = R.layout.item_calculator,
        items = items,
        holderFactory = { v -> CalculatorHolder(v, inflater) },
        onItemClick = onItemClick
)