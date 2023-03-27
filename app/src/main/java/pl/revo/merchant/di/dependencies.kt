package pl.revo.merchant.di

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import pl.revo.merchant.api.ApiService
import pl.revo.merchant.api.HttpClient
import pl.revo.merchant.api.HttpService
import pl.revo.merchant.api.MockData
import pl.revo.merchant.model.AutoAgentData
import pl.revo.merchant.model.MemoryCashedData
import pl.revo.merchant.utils.SessionUtils
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