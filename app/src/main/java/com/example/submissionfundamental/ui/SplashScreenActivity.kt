package com.example.submissionfundamental.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.submissionfundamental.R
import com.example.submissionfundamental.data.SettingPreferences
import com.example.submissionfundamental.data.dataStore
import com.example.submissionfundamental.databinding.ActivitySplashScreenBinding
import com.example.submissionfundamental.ui.ViewModelFactory.ViewModelFactory
import com.example.submissionfundamental.ui.viewmodel.SplashScreenViewModel


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private lateinit var splashScreenViewModel: SplashScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash_screen)

        splashScreenViewModel = obtainViewModel(this@SplashScreenActivity)
        splashScreenViewModel.getThemeSettings().observe(this@SplashScreenActivity) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.imageView.setImageResource(R.drawable.icon_github)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.imageView.setImageResource(R.drawable.github_logo)
            }
        }

        val imgLogo: ImageView = findViewById(R.id.imageView)

        val zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        imgLogo.startAnimation(zoomInAnimation)

        Handler().postDelayed({
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun obtainViewModel(activity: AppCompatActivity): SplashScreenViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, SettingPreferences.getInstance(application.dataStore))
        return ViewModelProvider(activity, factory).get(SplashScreenViewModel::class.java)
    }

    companion object {
        var SPLASH_TIME_OUT: Long = 1500
    }
}