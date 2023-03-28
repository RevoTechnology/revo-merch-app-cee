package merchant.mokka.api.error

data class ErrorRes(
        val errors: Map<String, List<String>>
)