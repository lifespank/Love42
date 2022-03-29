package com.mylittleproject.love42.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.FragmentSetProfileBinding
import com.mylittleproject.love42.tools.EventObserver
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetProfileFragment : Fragment() {

    private var _binding: FragmentSetProfileBinding? = null
    private val binding get() = _binding!!
    private val setProfileViewModel: SetProfileViewModel by viewModels()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val imageURI = data?.data
                setProfileViewModel.setImageURI(imageURI.toString())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = setProfileViewModel
        binding.ivProfile.clipToOutline = true
        val code = requireActivity().intent.data?.getQueryParameter(PARAMETER_KEY)
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
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        binding.tvLanguage.setAdapter(adapter)
    }

    private fun subscribeToObservables() {
        setProfileViewModel.redirectToSignInActivityEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val intent = Intent(requireContext(), SignInActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            })
        setProfileViewModel.loadProfileImageEvent.observe(viewLifecycleOwner, EventObserver {
            val intent = Intent()
                .apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                }
            resultLauncher.launch(intent)
        })
        setProfileViewModel.popUpSlackIDDescriptionEvent.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.dialog_what_is_slack_id)
                .setNeutralButton(R.string.close) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        })
        setProfileViewModel.preferredLanguages.observe(viewLifecycleOwner) {
            binding.cgLanguages.removeAllViews()
            val newContext = ContextThemeWrapper(
                requireContext(),
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
        setProfileViewModel.manualLanguagePopUpEvent.observe(viewLifecycleOwner, EventObserver {
            val editText = TextInputEditText(requireContext())
            MaterialAlertDialogBuilder(requireContext())
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PARAMETER_KEY = "code"
        val items = listOf(
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