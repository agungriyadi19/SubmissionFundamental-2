package com.example.submissionfundamental.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submissionfundamental.data.response.User
import com.example.submissionfundamental.data.response.UserGithubResponse
import com.example.submissionfundamental.data.retrofit.ApiClient
import retrofit2.Call
import retrofit2.Response

class UserViewModel : ViewModel() {

    val listUsers = MutableLiveData<ArrayList<User>>()
    val totalCount = MutableLiveData<Int>()

    fun setSearchUser(query: String) {
        ApiClient.apiInstance
            .searchUsers(query)
            .enqueue(object : retrofit2.Callback<UserGithubResponse> {
                override fun onResponse(
                    call: Call<UserGithubResponse>,
                    response: Response<UserGithubResponse>
                ) {
                    if (response.isSuccessful) {
                        listUsers.postValue(response.body()?.items)
                        totalCount.postValue(response.body()?.totalCount)
                    }
                }

                override fun onFailure(call: Call<UserGithubResponse>, t: Throwable) {
                    Log.e("MainActivity", "onFailure: ${t.message}")
                }

            })
    }

    fun getSearchUser(): LiveData<ArrayList<User>> {
        return listUsers
    }

    fun getTotalCount(): LiveData<Int> {
        return totalCount
    }
}