package com.mylittleproject.love42.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.FragmentSignInBinding
import com.mylittleproject.love42.keys.SEOUL_SIGN_IN_URL
import com.mylittleproject.love42.tools.EventObserver

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = signInViewModel
        subscribeToObservables()
    }

    private fun subscribeToObservables() {
        signInViewModel.signInClickEvent.observe(viewLifecycleOwner, EventObserver {
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(SEOUL_SIGN_IN_URL))
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}