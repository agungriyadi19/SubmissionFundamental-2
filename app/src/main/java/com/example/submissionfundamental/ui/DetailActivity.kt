package com.example.submissionfundamental.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.submissionfundamental.R
import com.example.submissionfundamental.databinding.ActivityDetailBinding
import com.example.submissionfundamental.ui.adapter.PagerAdapter
import com.example.submissionfundamental.ui.viewmodel.DetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val id = intent.getIntExtra(EXTRA_ID, 0)
        val avatarUrl = intent.getStringExtra(EXTRA_AVATAR)
        val bundle = Bundle()
        bundle.putString(EXTRA_USERNAME, username)

        val btnBack: ImageButton = findViewById(R.id.btn_back)
        btnBack.setOnClickListener(this)

        viewModel = ViewModelProvider(this).get(
            DetailViewModel::class.java
        )

        viewModel.setUserDetail(username.toString())
        viewModel.getUserDetail().observe(this) {
            if (it != null) {

                binding.apply {
                    tvNama.text = it.name
                    tvUsername.text = it.login
                    tvLocation.text = it.location
                    tvFollower.text = "${it.followers} Followers"
                    tvFollowing.text = "${it.following} Following"

                    Glide.with(this@DetailActivity)
                        .load(it.avatarUrl)
                        .centerCrop()
                        .into(imgAvatar)
                }

                binding.progressBar.visibility = View.GONE
            }
        }


        var _isChecked = false
        CoroutineScope(Dispatchers.IO).launch {
            val count = viewModel.checkUser(id)
            withContext(Dispatchers.Main){
                if (count != null){
                    if (count>0){
                        binding.toggleFav.isChecked = true
                        _isChecked = true
                    }else{
                        binding.toggleFav.isChecked = false
                        _isChecked = false
                    }
                }
            }
        }

        binding.toggleFav.setOnClickListener {
            _isChecked = !_isChecked
            val safeUsername = username ?: ""
            val safeAvatarUrl = avatarUrl ?: ""
            if (_isChecked){
                viewModel.addToFavorite(safeUsername, id, safeAvatarUrl)
            } else {
                viewModel.removeFromFavorite(id)
            }
            binding.toggleFav.isChecked = _isChecked
        }

        val pagerAdapter = PagerAdapter(this, supportFragmentManager, bundle)
        binding.apply {
            vpFoll.adapter = pagerAdapter
            tlFoll.setupWithViewPager(vpFoll)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back -> {
                val moveBack = Intent(this@DetailActivity, MainActivity::class.java)
                startActivity(moveBack)
            }
        }
    }

    companion object{
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_ID = "extra_id"
        const val EXTRA_AVATAR = "extra_avatar"
    }
}