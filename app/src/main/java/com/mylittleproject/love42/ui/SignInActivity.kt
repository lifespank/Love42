package com.mylittleproject.love42.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.snackbar.Snackbar
import com.mylittleproject.love42.MainActivity
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.ActivitySignInBinding
import com.mylittleproject.love42.keys.SEOUL_SIGN_IN_URL
import com.mylittleproject.love42.tools.EventObserver
import com.mylittleproject.love42.tools.NAME_TAG
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
        signInViewModel.initialCheck()
        subscribeToObservables()
    }

    private fun subscribeToObservables() {
        signInViewModel.signInClickEvent.observe(this, EventObserver {
            try {
                CustomTabsIntent
                    .Builder()
                    .build()
                    .launchUrl(this, Uri.parse(SEOUL_SIGN_IN_URL))
            } catch (e: Exception) {
                Log.w(NAME_TAG, "Invalid URL", e)
                Snackbar.make(binding.root, R.string.invalid_url, Snackbar.LENGTH_SHORT).show()
            }
        })
        signInViewModel.accessToken.observe(this, EventObserver { accessToken ->
            Log.d(NAME_TAG, "Token: $accessToken")
            val intent = Intent(this, SetProfileActivity::class.java)
            startActivity(intent)
            finish()
        })
        signInViewModel.moveToMainEvent.observe(this, EventObserver {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}