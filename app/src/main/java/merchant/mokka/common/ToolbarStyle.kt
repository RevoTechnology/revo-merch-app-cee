package merchant.mokka.common

import androidx.annotation.ColorRes
import merchant.mokka.R

enum class ToolbarStyle(
        @ColorRes val titleColorRes: Int,
        @ColorRes val bkgColorRes: Int
) {
    LIGHT   (R.color.black, R.color.bg),
    ACCENT  (R.color.white, R.color.colorAccent),
    DARK    (R.color.white, R.color.brown)
}