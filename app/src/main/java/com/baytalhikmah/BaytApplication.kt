package com.baytalhikmah

import android.app.Application
import android.content.Context
import com.baytalhikmah.data.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BaytApplication : Application() {
    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        applicationScope.launch { container.initialize() }
    }
}

val Context.appContainer: AppContainer
    get() = (applicationContext as BaytApplication).container
