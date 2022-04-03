package com.mylittleproject.love42.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mylittleproject.love42.MainActivity
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.ActivitySetProfileBinding
import com.mylittleproject.love42.tools.EventObserver
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetProfileBinding
    private val setProfileViewModel: SetProfileViewModel by viewModels()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val imageURI = data?.data
                setProfileViewModel.setImageURI(imageURI.toString())
            }
        }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent()
                    .apply {
                        type = "image/*"
                        action = Intent.ACTION_GET_CONTENT
                    }
                resultLauncher.launch(intent)
            } else {
                Snackbar.make(binding.root, R.string.permission_needed, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = setProfileViewModel
        binding.ivProfile.clipToOutline = true
        val code = intent.data?.getQueryParameter(PARAMETER_KEY)
        if (code != null) {
            Log.d(NAME_TAG, "Code received: $code")
            setProfileViewModel.fetchAccessToken(code)
        } else {
            Log.w(NAME_TAG, "No code received")
            setProfileViewModel.fetchAccessToken(code)
        }
        subscribeToObservables()
        setChip()
    }

    private fun setChip() {
        val adapter = ArrayAdapter(this, R.layout.list_item, ITEMS)
        binding.tvLanguage.setAdapter(adapter)
    }

    private fun subscribeToObservables() {
        setProfileViewModel.redirectToSignInActivityEvent.observe(
            this,
            EventObserver {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            })
        setProfileViewModel.loadProfileImageEvent.observe(this, EventObserver {
            if (allPermissionGranted()) {
                val intent = Intent()
                    .apply {
                        type = "image/*"
                        action = Intent.ACTION_GET_CONTENT
                    }
                resultLauncher.launch(intent)
            } else {
                requestPermissions()
            }
        })
        setProfileViewModel.popUpSlackIDDescriptionEvent.observe(this, EventObserver {
            MaterialAlertDialogBuilder(this)
                .setView(R.layout.dialog_what_is_slack_id)
                .setNeutralButton(R.string.close) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        })
        setProfileViewModel.preferredLanguages.observe(this) {
            binding.cgLanguages.removeAllViews()
            val newContext = ContextThemeWrapper(
                this,
                com.google.android.material.R.style.Widget_Material3_Chip_Input
            )
            it.forEach { language ->
                binding.cgLanguages.addView(Chip(newContext)
                    .apply {
                        text = language
                        isCloseIconVisible = true
                        setOnCloseIconClickListener {
                            setProfileViewModel.removeLanguage(text.toString())
                        }
                    })
            }
        }
        setProfileViewModel.manualLanguagePopUpEvent.observe(this, EventObserver {
            val editText = TextInputEditText(this)
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.preferred_language)
                .setView(editText)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    if (!editText.text.isNullOrBlank()) {
                        setProfileViewModel.addLanguage(editText.text.toString())
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        })
        setProfileViewModel.fillOutSlackMemberIDEvent.observe(this, EventObserver {
            Snackbar.make(binding.root, R.string.fill_out_slack, Snackbar.LENGTH_SHORT).show()
        })
        setProfileViewModel.moveToMainEvent.observe(this, EventObserver {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (shouldProvideRationale) {
            Snackbar.make(binding.root, R.string.permission_needed, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.confirm) {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                .show()
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val PARAMETER_KEY = "code"
        val REQUIRED_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        val ITEMS = listOf(
            "Ada",
            "BASIC",
            "C",
            "C++",
            "C#",
            "COBOL",
            "D",
            "Dart",
            "Fortran",
            "Go",
            "Haskell",
            "Java",
            "Javascript",
            "Kotlin",
            "LISP",
            "Lua",
            "MATLAB",
            "Objective-C",
            "Perl",
            "PHP",
            "Python",
            "R",
            "Ruby",
            "Scala",
            "Smalltalk",
            "Swift",
            "Something else"
        )
    }
}