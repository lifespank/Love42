package com.mylittleproject.love42.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.mylittleproject.love42.MainActivity
import com.mylittleproject.love42.databinding.ActivitySignInBinding
import com.mylittleproject.love42.keys.SEOUL_SIGN_IN_URL
import com.mylittleproject.love42.tools.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = signInViewModel
        subscribeToObservables()
    }

    private fun subscribeToObservables() {
        signInViewModel.signInClickEvent.observe(this, EventObserver {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(this, Uri.parse(SEOUL_SIGN_IN_URL))
        })
        signInViewModel.accessToken.observe(this, EventObserver { accessToken ->
            accessToken?.let {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}