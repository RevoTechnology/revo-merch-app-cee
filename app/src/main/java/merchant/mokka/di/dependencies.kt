package merchant.mokka.di

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import merchant.mokka.api.ApiService
import merchant.mokka.api.HttpClient
import merchant.mokka.api.HttpService
import merchant.mokka.api.MockData
import merchant.mokka.model.AutoAgentData
import merchant.mokka.model.MemoryCashedData
import merchant.mokka.utils.SessionUtils
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

fun dependencies() = Kodein.Module {
    bind<Cicerone<Router>>() with singleton { Cicerone.create() }

    bind<NavigatorHolder>() with singleton {
        val cicerone: Cicerone<Router> = kodein.instance()
        cicerone.navigatorHolder
    }

    bind<Router>() with singleton {
        val cicerone: Cicerone<Router> = kodein.instance()
        cicerone.router
    }

    bind<HttpClient>() with singleton {
        HttpClient()
    }

    bind<MemoryCashedData>() with singleton { MemoryCashedData() }
    bind<AutoAgentData>() with singleton { AutoAgentData() }


    bind<MockData>() with singleton {
        val context = kodein.instance<Context>()
        MockData(context)
    }

    bind<ApiService>() with singleton {
        val client = kodein.instance<HttpClient>()
        val context = kodein.instance<Context>()
        val memoryCashedData = kodein.instance<MemoryCashedData>()
        val mockData = kodein.instance<MockData>()
        HttpService(client, context, memoryCashedData, mockData)
    }

    bind<SessionUtils>() with singleton {
        SessionUtils()
    }
}