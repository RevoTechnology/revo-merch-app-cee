package pl.revo.merchant.ui.chat

interface IChatPresenter {
    val username: String?

    fun initLivetex()
    fun history()
    fun send(text: String)
    fun onResume()
    fun onPause()
}