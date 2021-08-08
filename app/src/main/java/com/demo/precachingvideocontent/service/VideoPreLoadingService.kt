package com.demo.precachingvideocontent.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.app.tiktok.utils.Constants.KEY_STORIES_LIST_DATA
import com.demo.precachingvideocontent.app.MyApp
import com.demo.precachingvideocontent.model.StoriesDataModel
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VideoPreLoadingService :
    IntentService(VideoPreLoadingService::class.java.simpleName) {
    private val TAG = "VideoPreLoadingService"

    private lateinit var mContext: Context
    private var cachingJob: Job? = null
    private var videosList: ArrayList<StoriesDataModel>? = null
    private lateinit var httpDataSourceFactory: HttpDataSource.Factory
    private lateinit var defaultDataSourceFactory: DefaultDataSourceFactory
    private lateinit var cacheDataSourceFactory: CacheDataSource
    private val simpleCache: SimpleCache? = MyApp.simpleCache


    override fun onHandleIntent(intent: Intent?) {
        mContext = applicationContext

        httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        defaultDataSourceFactory = DefaultDataSourceFactory(
            this, httpDataSourceFactory
        )

        cacheDataSourceFactory = simpleCache?.let {
            CacheDataSource.Factory()
                .setCache(it)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
                .createDataSource()
        }!!

        if (intent != null) {
            val extras = intent.extras
            videosList = extras?.getParcelableArrayList<StoriesDataModel>(KEY_STORIES_LIST_DATA)

            if (!videosList.isNullOrEmpty()) {
                preCacheVideo(videosList)
            }
        }
    }

    private fun preCacheVideo(videosList: ArrayList<StoriesDataModel>?) {
        var videoUrl: StoriesDataModel? = null
        if (!videosList.isNullOrEmpty()) {
            videoUrl = videosList[0]
            videosList.removeAt(0)
        } else {
            stopSelf()
        }
        if (!videoUrl?.storyUrl.isNullOrEmpty()) {
            val videoUri = Uri.parse(videoUrl?.storyUrl)
            val dataSpec = DataSpec(videoUri)

            val progressListener =
                CacheWriter.ProgressListener { requestLength, bytesCached, newBytesCached ->
                    val downloadPercentage: Double = (bytesCached * 100.0
                            / requestLength)

                    Log.d(TAG, "downloadPercentage $downloadPercentage videoUri: $videoUri")
                }

            cachingJob = GlobalScope.launch(Dispatchers.IO) {
                cacheVideo(dataSpec, progressListener)
                preCacheVideo(videosList)
            }
        }
    }

    private fun cacheVideo(
        dataSpec: DataSpec,
        progressListener: CacheWriter.ProgressListener
    ) {
        runCatching {
            CacheWriter(
                cacheDataSourceFactory,
                dataSpec,
                null,
                progressListener
            ).cache()
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cachingJob?.cancel()
    }
}