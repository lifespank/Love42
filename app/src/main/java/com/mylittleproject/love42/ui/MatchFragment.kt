package com.mylittleproject.love42.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.FragmentMatchBinding
import com.mylittleproject.love42.tools.EventObserver
import com.mylittleproject.love42.tools.NAME_TAG

class MatchFragment : Fragment() {

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private lateinit var adapter: MatchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.downloadMatches()
        initRecyclerView()
        subscribeToObservables()
    }

    private fun initRecyclerView() {
        adapter = MatchListAdapter(mainViewModel)
        binding.rvMatches.adapter = adapter
        binding.rvMatches.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.rvMatches.addItemDecoration(GridSpacingItemDecoration(2, 50, true))
    }

    private fun subscribeToObservables() {
        mainViewModel.matchProfiles.observe(viewLifecycleOwner) {
            Log.d(NAME_TAG, "Matches: $it")
            if (it.isNullOrEmpty()) {
                adapter.submitList(null)
                binding.tvNoMatches.isVisible = true
            } else {
                adapter.submitList(it)
                binding.tvNoMatches.isVisible = false
            }
        }
        mainViewModel.selectProfileEvent.observe(viewLifecycleOwner, EventObserver {
            Log.d(NAME_TAG, "Moving to profile...")
            findNavController().navigate(R.id.action_matchFragment_to_matchedProfileFragment)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}