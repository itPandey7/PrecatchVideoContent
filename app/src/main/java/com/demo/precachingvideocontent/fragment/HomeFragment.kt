package com.demo.precachingvideocontent.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.app.tiktok.utils.Constants
import com.demo.precachingvideocontent.R
import com.demo.precachingvideocontent.adapter.StoriesPagerAdapter
import com.demo.precachingvideocontent.model.StoriesDataModel
import com.demo.precachingvideocontent.service.VideoPreLoadingService
import com.demo.precachingvideocontent.utils.Status
import com.demo.precachingvideocontent.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var storiesPagerAdapter: StoriesPagerAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        mainViewModel.users.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val dataList = it.data
                    storiesPagerAdapter = StoriesPagerAdapter(this, dataList ?: mutableListOf())
                    view_pager_stories.adapter = storiesPagerAdapter
                    startPreLoadingService(dataList as ArrayList<StoriesDataModel>)

                }
                Status.LOADING -> {

                }
                Status.ERROR -> {
                    //Handle Error

                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun startPreLoadingService(dataList: ArrayList<StoriesDataModel>) {
        val preloadingServiceIntent = Intent(context, VideoPreLoadingService::class.java)
        preloadingServiceIntent.putParcelableArrayListExtra(
            Constants.KEY_STORIES_LIST_DATA,
            dataList
        )
        context?.startService(preloadingServiceIntent)
    }

//    private fun startPreCaching(dataList: ArrayList<StoriesDataModel>) {
//        val urlList = arrayOfNulls<String>(dataList.size)
//        dataList.mapIndexed { index, storiesDataModel ->
//            urlList[index] = storiesDataModel.storyUrl
//        }
//        val inputData = Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList).build()
//        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
//            .build()
//        WorkManager.getInstance(requireContext())
//            .enqueue(preCachingWork)
//    }
}