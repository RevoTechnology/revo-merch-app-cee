package merchant.mokka.utils

import android.content.res.Resources

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.dpf: Float
    get() = this * Resources.getSystem().displayMetrics.density