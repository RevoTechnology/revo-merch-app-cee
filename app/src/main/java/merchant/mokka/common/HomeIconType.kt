package merchant.mokka.common

import androidx.annotation.DrawableRes
import merchant.mokka.R

enum class HomeIconType(
        @DrawableRes val iconRes: Int?
) {
    MENU        (R.drawable.ic_menu),
    BACK_ARROW  (R.drawable.ic_arrow_back),
    CLOSE       (R.drawable.ic_close),
    NONE        (null)
}