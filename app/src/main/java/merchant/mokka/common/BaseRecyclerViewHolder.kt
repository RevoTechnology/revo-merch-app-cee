package merchant.mokka.common

import android.view.View

abstract class BaseRecyclerViewHolder<T>(
        view: View
): androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    abstract fun bindView(
            data: T,
            onItemClick: ((item: T, position: Int) -> Unit)?
    )
}