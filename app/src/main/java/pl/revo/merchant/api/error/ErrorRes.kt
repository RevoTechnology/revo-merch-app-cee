package pl.revo.merchant.api.error

data class ErrorRes(
        val errors: Map<String, List<String>>
)