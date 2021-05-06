package com.sendbird.calls.quickstart.groupcall.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.FragmentAppInfoBinding

class AppInfoFragment : Fragment() {
    lateinit var binding: FragmentAppInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_info, container, false)
        binding.appInfoTextViewId.text = SendBirdCall.applicationId
        binding.appInfoImageViewLeftArrow.setOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }
}