package com.demo.precachingvideocontent.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.demo.precachingvideocontent.fragment.StoryViewFragment
import com.demo.precachingvideocontent.model.StoriesDataModel


class StoriesPagerAdapter(fragment: Fragment, val dataList: List<StoriesDataModel> = mutableListOf()) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return StoryViewFragment.newInstance(dataList[position])
    }
}