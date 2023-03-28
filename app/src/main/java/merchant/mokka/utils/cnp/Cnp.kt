package merchant.mokka.utils.cnp

import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

//import java.util.Arrays;
//import java.util.List;
/**
 * The CNP has the following structure:
 * SAALLZZJJNNNC
 * S = sex digit, also encodes century of birth
 * AA = year of birth, last two digits
 * LL = month of birth, 01-12
 * ZZ = day of birth, 01-31
 * JJ = county where birth was registered,
 * or for people born before 1978, county where
 * the person resided when CNP was issued.
 * Typical values for this field are 01-39, 41-46, and,
 * starting with 1981, also 51 and 52.
 * but CNPs with other values exist as well.
 * NNN = ordinal
 * TODO Unclear if the ordinal (NNN) can be 000 or starts at 001
 * C = check digit.
 */
class Cnp(sex: Sex, birthYear: Int, birthMonth: Int, birthDay: Int, county: Int, ordinal: Int, bornAbroad: Boolean) : RomanianPersonalNumber() {
    val sex: Sex
    val birthYear: Int
    val birthMonth: Int
    val birthDay: Int
    val county: Int
    val ordinal: Int
    val isBornAbroad: Boolean

    override fun stringify12(): String {
        return String.format("%s%02d%02d%02d%02d%03d", sexChar(), birthYear % 100, birthMonth, birthDay, county, ordinal)
    }

    private fun sexChar(): Char {
        val century = birthYear / 100 * 100
        val maleDigit: Int
        maleDigit = when (century) {
            1800 -> 3
            1900 -> if (isBornAbroad) {
                7
            } else {
                1
            }
            2000 -> 5
            else -> throw RuntimeException("Should never be reached!")
        }
        return if (sex == Sex.MALE) {
            Character.forDigit(maleDigit, 10)
        } else {
            Character.forDigit(maleDigit + 1, 10)
        }
    }

    override fun toString(): String {
        return "CNP: " + stringify()
    }

    companion object {
        private fun isValidDate(year: Int, month: Int, day: Int): Boolean {
            return try {
                val df = SimpleDateFormat("yyyy-MM-dd")
                df.isLenient = false
                df.parse(String.format("%04d-%02d-%02d", year, month, day))
                true
            } catch (e: Exception) {
                false
            }
        }

        //    private static boolean isValidCounty(int county) {
        //        return VALID_COUNTIES.contains(county);
        //        TODO some CNPs were issued with nonstandard counties, especially to citizens born abroad
        //    }
        private fun isNumeric(s: String): Boolean {
            return try {
                BigInteger(s)
                true
            } catch (e: NumberFormatException) {
                false
            }
        }

        fun isValid(cnpStr: String): Boolean {
            return fromString(cnpStr) != null
        }

        fun fromString(cnpStr: String): Cnp? {
            // TODO should collect exceptions
            if (cnpStr.length != 13) {
                return null
                //throw new CNPException( CNPException.V_FORMAT );
            }
            if (!isNumeric(cnpStr)) {
                return null
                //throw new CNPException( CNPException.V_FORMAT );
            }
            if (checkDigit(cnpStr.substring(0, 12)) != cnpStr[12]) {
                return null
                //throw new IllegalArgumentException("Check digit does not match");
            }
            val sexChar = cnpStr[0]
            val sex: Sex
            sex = when (sexChar) {
                '1', '3', '5', '7' -> Sex.MALE
                '2', '4', '6', '8' -> Sex.FEMALE
                else -> return null
            }
            val century: Int
            century = when (sexChar) {
                '1', '2', '7', '8' -> 1900
                '3', '4' -> 1800
                '5', '6' -> 2000
                else -> return null
            }
            val bornAbroad = sexChar == '7' || sexChar == '8'
            val birthYear = century + cnpStr.substring(1, 3).toInt()
            val birthMonth = cnpStr.substring(3, 5).toInt()
            val birthDay = cnpStr.substring(5, 7).toInt()
            if (!isValidDate(birthYear, birthMonth, birthDay)) {
                return null
                // throw new CNPException( CNPException.V_DATA );
            }
            val county = cnpStr.substring(7, 9).toInt()
            val ordinal = cnpStr.substring(9, 12).toInt()
            return Cnp(sex, birthYear, birthMonth, birthDay, county, ordinal, bornAbroad)
        }

        const val MIN_BIRTH_YEAR = 1800
        const val MAX_BIRTH_YEAR = 2099 //    public static final List<Integer> VALID_COUNTIES = Arrays.asList(new Integer[] {
        //             1,  2,  3,  4,  5,  6,  7,  8,  9, 10,
        //            11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
        //            21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
        //            31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        //            41, 42, 43, 44, 45, 46,
        //            51, 52
        //    });
    }

    init {
        require(!(birthYear < MIN_BIRTH_YEAR || birthYear > MAX_BIRTH_YEAR)) { "birthYear is out of range" }
        require(!(bornAbroad && (birthYear < 1900 || birthYear > 1999))) { "birthYear is out of range for persons born abroad" }
        require(isValidDate(birthYear, birthMonth, birthDay)) { "Birth date is invalid" }

//        if(! isValidCounty(county)) {
//            throw new IllegalArgumentException("County is invalid");
//        }
//
        require(!(ordinal < 0 || ordinal > 999)) { "Ordinal is out of range" }
        this.sex = sex
        this.birthYear = birthYear
        this.birthMonth = birthMonth
        this.birthDay = birthDay
        this.county = county
        this.ordinal = ordinal
        isBornAbroad = bornAbroad
    }

    fun checkAge(age: Int = 18): Boolean {
        val now = with(Calendar.getInstance()) {
            add(Calendar.YEAR, -age)
            time
        }
        val birthDate = with(Calendar.getInstance()) {
            set(Calendar.YEAR, birthYear)
            set(Calendar.MONTH, birthMonth)
            set(Calendar.DAY_OF_MONTH, birthDay)
            time
        }

        return now.after(birthDate)
    }

    fun birthDate() = with(Calendar.getInstance()) {
        set(Calendar.YEAR, birthYear)
        set(Calendar.MONTH, birthMonth)
        set(Calendar.DAY_OF_MONTH, birthDay)
        time
    }
}

fun String.cnp() = Cnp.fromString(this)
fun String.isValidCnp(isDemo: Boolean = false) = isDemo || Cnp.isValid(this)