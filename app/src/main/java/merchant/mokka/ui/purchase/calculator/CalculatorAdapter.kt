package merchant.mokka.ui.purchase.calculator

import android.view.LayoutInflater
import merchant.mokka.R
import merchant.mokka.common.BaseRecyclerViewAdapter
import merchant.mokka.model.TariffData

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