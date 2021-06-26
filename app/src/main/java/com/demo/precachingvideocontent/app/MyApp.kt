package com.demo.precachingvideocontent.app

import android.app.Application
import android.content.Context
import com.demo.precachingvideocontent.di.module.appModule
import com.demo.precachingvideocontent.di.module.viewModelModule
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp: Application() {
    companion object {
        var simpleCache: SimpleCache? = null
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        startKoin {
            androidContext(this@MyApp)
            modules(listOf(appModule,viewModelModule))
        }

        val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024)
        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)

        if (simpleCache == null) {
            simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
        }
    }
}