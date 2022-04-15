package com.mylittleproject.love42.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
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
        initRecyclerView()
        subscribeToObservables()
    }

    private fun initRecyclerView() {
        adapter = MatchListAdapter(mainViewModel)
        binding.rvMatches.apply {
            this.adapter = this@MatchFragment.adapter
            layoutManager =
                StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                }
            addItemDecoration(GridSpacingItemDecoration(2, 50))
        }
    }

    private fun subscribeToObservables() {
        mainViewModel.matches.observe(viewLifecycleOwner) {
            Log.d(NAME_TAG, "Matches: $it")
            if (it.isNullOrEmpty()) {
                adapter.submitList(null)
                Log.d(NAME_TAG, "match empty")
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