package com.mylittleproject.love42.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mylittleproject.love42.databinding.FragmentSetProfileBinding
import com.mylittleproject.love42.tools.NAME_TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetProfileFragment : Fragment() {

    private var _binding: FragmentSetProfileBinding? = null
    private val binding get() = _binding!!
    private val setProfileViewModel: SetProfileViewModel by viewModels()

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
        val code = requireActivity().intent.data?.getQueryParameter(PARAMETER_KEY)
        if (code != null) {
            Log.d(NAME_TAG, "Code received: $code")
            setProfileViewModel.fetchAccessToken(code)
        } else {
            Log.w(NAME_TAG, "No code received")
            setProfileViewModel.fetchAccessToken(code)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PARAMETER_KEY = "code"
        const val GRANT_TYPE = "authorization code"
    }
}