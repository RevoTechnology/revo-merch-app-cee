package pl.revo.merchant.common

import androidx.annotation.ColorRes
import pl.revo.merchant.R

enum class ToolbarStyle(
        @ColorRes val titleColorRes: Int,
        @ColorRes val bkgColorRes: Int
) {
    LIGHT   (R.color.black, R.color.bg),
    ACCENT  (R.color.white, R.color.colorAccent),
    DARK    (R.color.white, R.color.brown)
}