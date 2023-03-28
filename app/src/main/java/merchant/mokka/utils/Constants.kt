package merchant.mokka.utils

object Constants {
    const val TAG = "TAG_APP"

    const val SMS_CODE_LENGTH = 4
    const val PIN_CODE_LENGTH = 4

    const val MIN_AGE = 18
    const val MAX_AGE = 120

    const val SCAN_DOCUMENT_MASK = "*"
    const val SCAN_TIME_OUT = "15.0"

    const val POSTAL_CODE_MASK = "__-___"
    const val DATE_MASK = "__.__.____"

    const val SMS_VERIFY_RETRY_SECONDS = 30
    const val SMS_VERIFY_RETRY_DELAY = 500L

    const val BARCODE_BACKGROUND = 0xf5f5f5

    val CURRENCY = when {
        isRuLocale() -> "руб."
        isRoLocale() -> "RON"
        isBgLocale() -> "лв."
        else -> "zł"
    }
    const val MASKED_DOC_POSTFIX = "_masked"

    const val LOCALE_RU = "ru"
    const val LOCALE_PL = "merchant"
    const val LOCALE_RO = "ro"
    const val LOCALE_BG = "bg"
}
