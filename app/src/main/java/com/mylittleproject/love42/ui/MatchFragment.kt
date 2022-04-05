package com.mylittleproject.love42.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.mylittleproject.love42.R
import com.mylittleproject.love42.databinding.FragmentMatchBinding
import com.mylittleproject.love42.tools.NAME_TAG

class MatchFragment : Fragment() {

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private val adapter = MatchListAdapter()

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
        binding.rvMatches.adapter = adapter
        binding.rvMatches.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.rvMatches.addItemDecoration(GridSpacingItemDecoration(2, 50, true))
    }

    private fun subscribeToObservables() {
        mainViewModel.matchProfiles.observe(viewLifecycleOwner) {
            Log.d(NAME_TAG, "Matches: $it")
            adapter.submitList(it.ifEmpty { null })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}