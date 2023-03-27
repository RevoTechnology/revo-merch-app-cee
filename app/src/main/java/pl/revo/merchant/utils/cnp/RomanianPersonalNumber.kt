package pl.revo.merchant.utils.cnp

/** Either a CNP or a NIF  */
abstract class RomanianPersonalNumber {
    protected abstract fun stringify12(): String
    fun stringify(): String {
        val str12 = stringify12()
        return str12 + checkDigit(str12)
    }

    companion object {
        fun isValid(digits13: String): Boolean {
            return fromString(digits13) != null
        }

        fun fromString(digits13: String): RomanianPersonalNumber? {
            return if (digits13[0] == '9') {
                Nif.Companion.fromString(digits13)
            } else {
                Cnp.Companion.fromString(digits13)
            }
        }

        private val CHECK_DIGIT_FACTORS = intArrayOf(2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9)
         fun checkDigit(digits12: String): Char {
            var v = 0
            for (i in 0..11) v += Character.digit(digits12[i], 10) * CHECK_DIGIT_FACTORS[i]
            v %= 11
            if (v == 10) v = 1
            return Character.forDigit(v, 10)
        }
    }
}