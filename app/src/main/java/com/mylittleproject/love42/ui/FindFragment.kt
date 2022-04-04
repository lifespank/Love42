package com.mylittleproject.love42.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.FragmentFindBinding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindFragment : Fragment() {

    private val mainViewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private var _binding: FragmentFindBinding? = null
    private val binding get() = _binding!!
    private val adapter = UserInfoListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCardStackView()
        mainViewModel.downloadCandidates()
        subscribeToObservables()
    }

    private fun initCardStackView() {
        binding.csvCandidates.layoutManager = CardStackLayoutManager(requireContext())
        binding.csvCandidates.adapter = adapter
    }

    private fun subscribeToObservables() {
        mainViewModel.candidateProfiles.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}