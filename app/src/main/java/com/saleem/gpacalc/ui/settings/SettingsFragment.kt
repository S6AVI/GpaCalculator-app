package com.saleem.gpacalc.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.saleem.gpacalc.R
import com.saleem.gpacalc.data.preferencesmanager.GpaSystem
import com.saleem.gpacalc.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.settings_fragment) {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = SettingsFragmentBinding.bind(view)


      viewLifecycleOwner.lifecycleScope.launch {
          val result = viewModel.preferencesFlow.first()

//            if (result == 1) {
//                binding.rgGpaSystem.check(binding.rb4.id)
//            } else {
//                binding.rgGpaSystem.check(binding.rb5.id)
//            }
          binding.rb4.isChecked = result == GpaSystem.FOUR
          binding.rb5.isChecked = result == GpaSystem.FIVE
        }


        binding.rgGpaSystem.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rb4.id) {
                viewModel.onGpaSystemChanged(GpaSystem.FOUR)
            } else {
                viewModel.onGpaSystemChanged(GpaSystem.FIVE)
            }

        }
    }
}