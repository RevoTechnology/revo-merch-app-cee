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
        isRoLocale() -> "RON"
        isBgLocale() -> "лв."
        else -> "zł"
    }
    const val MASKED_DOC_POSTFIX = "_masked"

    enum class STANDS(val link: Links) {
        TEST(
            Links(
                pl = "https://test-backend.revoplus.pl/",
                ro = "https://test-backend.mokka.ro/",
                bg = "https://test-backend.mokka.bg/"
            )
        ),
        PROD(
            Links(
                pl = "https://b.revoplus.pl/",
                ro = "https://b.mokka.ro/",
                bg = "https://b.mokka.bg/"
            )
        ),
        STAGE(
            Links(
                pl = "https://stage-backend.revoplus.pl/",
                ro = "https://stage-backend.mokka.ro/",
                bg = "https://stage-backend.mokka.bg/"
            )
        ),
        DEMO(
            Links(
                pl = "https://demo-backend.mokka.pl/",
                ro = "https://demo-backend.mokka.ro/",
                bg = "https://demo-backend.mokka.bg/"
            )
        ),
        DEV1(
            Links(
                pl = "https://b.dev1.mokka.pl/",
                ro = "https://b.dev1.mokka.ro/",
                bg = "https://b.dev1.mokka.bg/"
            )
        ),
        DEV2(
            Links(
                pl = "https://b.dev2.mokka.pl/",
                ro = "https://b.dev2.mokka.ro/",
                bg = "https://b.dev2.mokka.bg/"
            )
        );

        fun getLinkByLanguage(): String {
            return when {
                isRoLocale() -> link.ro
                isPlLocale() -> link.pl
                isBgLocale() -> link.bg
                else -> ""
            }
        }

        companion object {
            fun getByEnv(env: String): STANDS {
                return when (env) {
                    "stage" -> STAGE
                    "prod" -> PROD
                    "otest" -> DEMO
                    else -> STAGE
                }
            }

            fun getByLink(link: String): STANDS {
                return values().firstOrNull { it.link.containsLink(link) } ?: STAGE
            }
        }
    }

    data class Links(
        val pl: String,
        val ro: String,
        val bg: String
    ) {
        fun containsLink(link: String) = pl == link || ro == link || bg == link
    }

}
