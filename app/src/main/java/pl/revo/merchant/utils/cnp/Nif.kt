package pl.revo.merchant.utils.cnp

import java.math.BigInteger

/**
 * A NIF is kind of a CNP but without inner structure
 * and with the leading digit '9'.
 * The CNP check digit rules apply.
 */
class Nif(
        /** Digits without leading '9' and without final check digit  */
        private val digits11: String) : RomanianPersonalNumber() {
    override fun stringify12(): String {
        return "9" + digits11
    }

    override fun toString(): String {
        return "NIF: " + stringify()
    }

    companion object {
        fun isValid(digits: String): Boolean {
            return fromString(digits) != null
        }

        fun fromString(digits: String): Nif? {
            if (digits.length != 13) {
                return null
                // throw new IllegalArgumentException("Digits don't represent a NIF");
            }
            if (!isNumeric(digits)) {
                return null
                //throw new IllegalArgumentException("Digits don't represent a NIF");
            }
            if (digits[0] != '9') {
                return null
                //throw new IllegalArgumentException("Digits don't represent a NIF");
            }
            return if (RomanianPersonalNumber.Companion.checkDigit(digits.substring(0, 12)) != digits[12]) {
                null
                //throw new IllegalArgumentException("Check digit does not match");
            } else Nif(digits.substring(1, 12))
        }

        private fun isNumeric(s: String): Boolean {
            return try {
                BigInteger(s)
                true
            } catch (e: NumberFormatException) {
                false
            }
        }
    }

}