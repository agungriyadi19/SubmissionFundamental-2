package com.example.submissionfundamental.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionfundamental.R
import com.example.submissionfundamental.data.SettingPreferences
import com.example.submissionfundamental.data.dataStore
import com.example.submissionfundamental.data.response.User
import com.example.submissionfundamental.data.response.UserGithubResponse
import com.example.submissionfundamental.data.retrofit.ApiClient
import com.example.submissionfundamental.databinding.ActivityMainBinding
import com.example.submissionfundamental.ui.ViewModelFactory.ViewModelFactory
import com.example.submissionfundamental.ui.adapter.UserAdapter
import com.example.submissionfundamental.ui.viewmodel.MainViewModel
import com.example.submissionfundamental.ui.viewmodel.UserViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var adapter: UserAdapter
    private lateinit var mainViewModel: MainViewModel

    private var USER_ID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        mainViewModel = obtainViewModel(this@MainActivity)

        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
                binding.switchTheme.setThumbIconResource(R.drawable.baseline_nightlight_24)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
                binding.switchTheme.setThumbIconResource(R.drawable.baseline_light_mode_24)
            }
        }

        adapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Intent(this@MainActivity, DetailActivity::class.java).also {
                    it.putExtra(DetailActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(DetailActivity.EXTRA_ID, data.id)
                    it.putExtra(DetailActivity.EXTRA_AVATAR, data.avatarUrl)
                    startActivity(it)
                }
            }

        })
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            UserViewModel::class.java
        )

        binding.apply {
            rvSearchuser.layoutManager = LinearLayoutManager(this@MainActivity)
            rvSearchuser.setHasFixedSize(true)
            rvSearchuser.adapter = adapter


            btnSearch.setOnClickListener {
                searchUser()
            }

            favoriteMenu.setOnClickListener(View.OnClickListener {
                startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
            })

            switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                mainViewModel.saveThemeSetting(isChecked)
            }

            searchUser.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                    searchUser()

                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            viewModel.getSearchUser().observe(this@MainActivity, {
                if (it != null) {
                    adapter.setList(it)
                    showLoading(false)

                }
            })

        }

        searchUser(isDefaultSearch = true)
    }

    private fun searchUser() {
        binding.apply {
            val query = searchUser.text.toString()
            if (query.isEmpty()) return
            showLoading(true)
            viewModel.setSearchUser(query)

        }

        binding.searchUser.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                USER_ID = binding.searchUser.text.toString().trim()
                searchUser()
                true
            } else {
                false
            }
        }
    }


    private fun searchUser(isDefaultSearch: Boolean = false) {
        showLoading(true)
        totalUser()
    }

    private fun totalUser() {
        // Call API to search users
        val username =
            binding.searchUser.text.toString().takeIf { it.isNotBlank() } ?: "agungriyadi"
        val client = ApiClient.apiInstance.searchUsers(username)
        client.enqueue(object : Callback<UserGithubResponse> {
            override fun onResponse(
                call: Call<UserGithubResponse>,
                response: Response<UserGithubResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val totalCount = responseBody.totalCount
                        showUserFoundCount(totalCount)

                        adapter.setList(responseBody.items)
                    }
                } else {
                    Log.e("TAG", "onFailure: ${response.message()}")
                    // Show error message to user
                    showToast("Failed to search users. Please try again later.")
                }
            }

            override fun onFailure(call: Call<UserGithubResponse>, t: Throwable) {
                showLoading(false)
                Log.e("TAG", "onFailure: ${t.message}")
                // Show error message to user
                showToast("Failed to search users. Please check your internet connection.")
            }
        })
    }

    private fun showUserFoundCount(count: Int) {
        binding.userFound.text = "$count User Found"
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {

    }

    private fun obtainViewModel(activity: AppCompatActivity): MainViewModel {
        val factory = ViewModelFactory.getInstance(application, SettingPreferences.getInstance(application.dataStore))
        return ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }

}