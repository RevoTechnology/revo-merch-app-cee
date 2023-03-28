package merchant.mokka

import org.junit.Test
import merchant.mokka.utils.cnp.cnp
import merchant.mokka.utils.isNewVersion

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun check_cnp() {
        listOf("5000 221 261 857").forEach { code ->
            val cnp = code.cnp()
            val isValid = cnp != null
            val checkAge18 = cnp?.checkAge(18)
            val checkAge20 = cnp?.checkAge(20)
            val checkAge21 = cnp?.checkAge(21)

//            println("code=$code, result=$cnp, isValid=$isValid, date=${cnp?.birthDay}/${cnp?.birthMonth}/${cnp?.birthYear}")
//            println("checkAge18=$checkAge18, checkAge20=$checkAge20, checkAge21=$checkAge21")
        }
    }

    @Test
    fun check_version() {
        assert("2.0.18".isNewVersion()) { println("1") }
        assert("1.0.0".isNewVersion()) { println("2e") }

    }
}

