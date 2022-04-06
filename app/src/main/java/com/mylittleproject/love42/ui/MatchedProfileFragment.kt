package com.mylittleproject.love42.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.FragmentMatchedProfileBinding
import com.mylittleproject.love42.tools.EventObserver
import com.mylittleproject.love42.tools.NAME_TAG

class MatchedProfileFragment : Fragment() {

    private var _binding: FragmentMatchedProfileBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchedProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        subscribeToObservables()
    }

    private fun subscribeToObservables() {
        mainViewModel.openSchemeEvent.observe(viewLifecycleOwner, EventObserver { url ->
            try {
                Intent()
                    .apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(url)
                    }.also {
                        startActivity(it)
                    }
            } catch (e: Exception) {
                Log.w(NAME_TAG, "Invalid URL", e)
                Snackbar.make(binding.root, R.string.invalid_url, Snackbar.LENGTH_SHORT).show()
            }
        })
        mainViewModel.openURLEvent.observe(viewLifecycleOwner, EventObserver { url ->
            try {
                CustomTabsIntent
                    .Builder()
                    .build()
                    .launchUrl(requireContext(), Uri.parse(url))
            } catch (e: Exception) {
                Log.w(NAME_TAG, "Invalid URL", e)
                Snackbar.make(binding.root, R.string.invalid_url, Snackbar.LENGTH_SHORT).show()
            }
        })
        mainViewModel.sendEmailEvent.observe(viewLifecycleOwner, EventObserver { urls ->
            try {
                Log.d(NAME_TAG, "Sending email:$urls")
                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                selectorIntent.data = Uri.parse("mailto:")
                Intent()
                    .apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_EMAIL, urls)
                        selector = selectorIntent
                    }
                    .also {
                        startActivity(Intent.createChooser(it, getString(R.string.sending_email)))
                    }
            } catch (e: Exception) {
                Log.w(NAME_TAG, "Email failed", e)
            }
        })
        mainViewModel.selectedPreferredLanguages.observe(viewLifecycleOwner) {
            it.forEach { language ->
                binding.cgLanguages.addView(
                    Chip(requireContext())
                        .apply {
                            text = language
                        })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}