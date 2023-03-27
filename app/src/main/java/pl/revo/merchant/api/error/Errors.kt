package pl.revo.merchant.api.error

class ApiNotImplementErr(message: String? = null) : Throwable(message)
class LargeDataError(message: String? = null) : Throwable(message)
class NetworkAvailableErr(message: String? = null) : Throwable(message)
class UnAuthorizedErr(message: String? = null) : Throwable(message)
class UnknownErr(message: String? = null) : Throwable(message)
class InvalidFinalizationResponse(message: String? = null) : Throwable(message)
class InvalidLamodaFinalization(message: String? = null) : Throwable(message)
class ServerException(message: String? = null) : Throwable(message)

class LivetexConnectionError(message: String? = null) : Throwable(message)
class LivetexDestinationError(message: String? = null) : Throwable(message)
class LivetexStateError(message: String? = null) : Throwable(message)
class LivetexHistoryError(message: String? = null) : Throwable(message)
class LivetexDialogError(message: String? = null) : Throwable(message)
class LivetexSendTextError(message: String? = null) : Throwable(message)