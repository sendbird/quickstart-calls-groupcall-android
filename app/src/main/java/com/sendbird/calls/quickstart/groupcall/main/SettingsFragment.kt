package com.sendbird.calls.quickstart.groupcall.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.FragmentSettingsBinding
import com.sendbird.calls.quickstart.groupcall.signin.SignInActivity
import com.sendbird.calls.quickstart.groupcall.util.Status
import com.sendbird.calls.quickstart.groupcall.util.dpToPixel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel = SettingsViewModel()
    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        setViewEventListener()
        setUserInfo()
        observeViewModel()
        return binding.root
    }

    private fun setUserInfo() {
        val user = SendBirdCall.currentUser
        binding.settingsTextViewUserId.text = if (user?.userId.isNullOrEmpty()) {
            getString(R.string.no_nickname)
        } else {
            String.format(getString(R.string.user_id_template), user?.userId)
        }

        binding.settingsTextViewUserName.text = if (user?.nickname.isNullOrEmpty()) {
            getString(R.string.no_nickname)
        } else {
            user?.nickname
        }

        val radius = activity?.dpToPixel(32) ?: 0
        Glide.with(this)
            .load(user?.profileUrl)
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(radius))
                    .error(R.drawable.icon_avatar)
            )
            .into(binding.settingsImageViewProfile)
    }

    private fun setViewEventListener() {
        binding.settingsConstraintLayoutAppInfo.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAppInfoFragment()
            findNavController().navigate(action)
        }

        binding.settingsConstraintLayoutSignOut.setOnClickListener { viewModel.deauthenticate() }
    }

    private fun observeViewModel() {
        viewModel.deauthenticateLiveData.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {
                    val intent = Intent(activity, SignInActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }

                Status.ERROR -> {}
                Status.LOADING -> {}
            }
        }
    }
}
