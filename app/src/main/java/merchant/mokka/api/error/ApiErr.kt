package merchant.mokka.api.error

import merchant.mokka.utils.toFirstUpperCase

class ApiErr( var errorArray: Map<String, List<String>>) : Throwable() {

    fun loginOrSmsError(): Boolean =
            errorArray["manager"]?.size == 1 &&
                    errorArray["manager"]?.contains("неверный логин и/или смс-код") == true &&
                    (!errorArray.containsKey("password") ||
                            passwordEmpty() && errorArray["password"]?.size == 1) &&
                    (!errorArray.containsKey("password_confirmation") ||
                            passwordConfirmEmpty() && errorArray["password_confirmation"]?.size == 1)

    private fun passwordEmpty(): Boolean =
            errorArray["password"]?.contains("не может быть пустым") == true

    private fun passwordConfirmEmpty(): Boolean =
            errorArray["password_confirmation"]?.contains("не может быть пустым") == true

    fun loginOrPasswordError(): Boolean =
            errorArray["manager"]?.contains("неверный логин и/или пароль") == true

    fun codeError(): Boolean =
            errorArray["code"]?.contains("имеет неверное значение") == true ||
                    errorArray["confirmation_code"]?.contains("имеет неверное значение") == true ||
                    errorArray["confirmation_code"]?.contains("неправильный код") == true

    fun clientError(): Boolean =
            errorArray["client"]?.contains("имеет неверное значение") == true

    fun phoneError(): Boolean =
            errorArray["phone_number"]?.contains("имеет неверный формат") == true

    fun largeAmount(): Boolean =
            errorArray["amount"]?.contains("не может быть больше 100000 рублей") == true

    fun invalidClientData() = listOf("surname", "first_name", "patronymic", "cnp")
            .intersect(errorArray.keys).isNotEmpty()

    fun invalidClientDocument() = listOf("russian_passport").intersect(errorArray.keys).isNotEmpty()

    override val message: String?
        get() {
            val result = StringBuilder()
            for ((_, values) in errorArray) {
                if (values.isNotEmpty()) {
                    values.forEach {
                        result.append(it.toFirstUpperCase()).append("\n")
                    }
                }
            }
            return result.toString()
        }
}