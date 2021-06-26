package com.demo.precachingvideocontent.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.precachingvideocontent.R
import com.demo.precachingvideocontent.model.StoriesDataModel
import com.demo.precachingvideocontent.utils.NetworkHelper
import com.demo.precachingvideocontent.utils.Resource
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class MainViewModel(
    private val networkHelper: NetworkHelper,
    private val context: Context
) : ViewModel() {

    private val _users = MutableLiveData<Resource<List<StoriesDataModel>>>()
    val users: LiveData<Resource<List<StoriesDataModel>>>
        get() = _users

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _users.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                val mockData = context.resources?.openRawResource(R.raw.stories_data)
                val dataString = mockData?.bufferedReader()?.readText()
                val gson = Gson()
                val storiesType = object : TypeToken<ArrayList<StoriesDataModel>>() {}.type
                val storiesDataModelList =
                    gson.fromJson<ArrayList<StoriesDataModel>>(dataString, storiesType)
                _users.postValue(Resource.success(storiesDataModelList))
            } else _users.postValue(Resource.error("No internet connection", null))
        }
    }


}