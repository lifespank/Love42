package com.mylittleproject.love42.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.FragmentMyProfileBinding
import com.mylittleproject.love42.tools.EventObserver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileFragment : Fragment() {

    private val mainViewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val imageURI = data?.data
                mainViewModel.setImageURI(imageURI.toString())
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = mainViewModel
        binding.ivProfile.clipToOutline = true
        setChip()
        mainViewModel.downloadProfile()
        subscribeToObservables()
    }

    private fun setChip() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, SetProfileActivity.ITEMS)
        binding.tvLanguage.setAdapter(adapter)
    }

    private fun subscribeToObservables() {
        mainViewModel.preferredLanguages.observe(viewLifecycleOwner) {
            binding.cgLanguages.removeAllViews()
            val newContext = ContextThemeWrapper(
                requireContext(),
                com.google.android.material.R.style.Widget_Material3_Chip_Input
            )
            it.forEach { language ->
                binding.cgLanguages.addView(
                    Chip(newContext)
                        .apply {
                            text = language
                            isCloseIconVisible = true
                            setOnCloseIconClickListener {
                                mainViewModel.removeLanguage(text.toString())
                            }
                        })
            }
        }
        mainViewModel.manualLanguagePopUpEvent.observe(viewLifecycleOwner, EventObserver {
            val editText = TextInputEditText(requireContext())
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.preferred_language)
                .setView(editText)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    if (!editText.text.isNullOrBlank()) {
                        mainViewModel.addLanguage(editText.text.toString())
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        })
        mainViewModel.popUpSlackIDDescriptionEvent.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.dialog_what_is_slack_id)
                .setNeutralButton(R.string.close) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        })
        mainViewModel.snackBarEvent.observe(viewLifecycleOwner, EventObserver { stringID ->
            Snackbar.make(binding.root, stringID, Snackbar.LENGTH_SHORT).show()
        })
        mainViewModel.loadProfileImageEvent.observe(viewLifecycleOwner, EventObserver {
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
        mainViewModel.deletePopUpEvent.observe(viewLifecycleOwner, EventObserver {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_account)
                .setMessage(R.string.are_you_sure)
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(R.string.confirm) { dialog, _ ->
                    mainViewModel.deleteAccount()
                }
                .show()
        })
        mainViewModel.goToSignInActivityEvent.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(R.id.action_myProfileFragment_to_signInActivity)
            requireActivity().finish()
        })
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
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

    private fun allPermissionGranted() = SetProfileActivity.REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}