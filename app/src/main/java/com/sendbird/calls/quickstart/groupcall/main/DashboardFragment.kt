package com.sendbird.calls.quickstart.groupcall.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdError
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.FragmentDashboardBinding
import com.sendbird.calls.quickstart.groupcall.preview.PreviewActivity
import com.sendbird.calls.quickstart.groupcall.room.RoomActivity
import com.sendbird.calls.quickstart.groupcall.util.*

class DashboardFragment : Fragment() {
    lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel = DashboardViewModel()
    private val previewLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_ENTER_FAIL) {
            val errorCode = result.data?.getIntExtra(EXTRA_ENTER_ERROR_CODE, -1)
            val errorMessage = if (errorCode == SendBirdError.ERR_PARTICIPANTS_LIMIT_EXCEEDED_IN_ROOM) {
                getString(R.string.dashboard_can_not_enter_room_max_participants_count_exceeded)
            } else {
                result.data?.getStringExtra(EXTRA_ENTER_ERROR_MESSAGE)
            } ?: UNKNOWN_SENDBIRD_ERROR

            activity?.showAlertDialog(
                getString(R.string.dashboard_can_not_enter_room),
                errorMessage
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        setUserInfo()
        setViewEventListeners()
        observeViewModel()
        return binding.root
    }

    private fun setViewEventListeners() {
        binding.linearLayoutDashboard.setOnClickListener {
            activity?.hideKeyboard()
            binding.editTextRoomId.clearFocus()
        }

        binding.editTextRoomId.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.textViewEnter.visibility = View.VISIBLE
            } else {
                if (binding.editTextRoomId.text?.toString().isNullOrEmpty()) {
                    binding.textViewEnter.visibility = View.GONE
                }
            }
        }

        binding.dashboardCreateRoomButton.setOnClickListener {
            viewModel.createAndEnterRoom()
        }

        binding.textViewEnter.setOnClickListener(this::onEnterButtonClicked)
    }

    private fun onEnterButtonClicked(v: View) {
        val roomId = binding.editTextRoomId.text?.toString() ?: return
        viewModel.fetchRoomById(roomId)
    }

    private fun setUserInfo() {
        val user = SendBirdCall.currentUser
        binding.textViewUserId.text = if (user?.userId.isNullOrEmpty()) {
            getString(R.string.no_nickname)
        } else {
            String.format(getString(R.string.user_id_template), user?.userId)
        }

        binding.textViewUserName.text = if (user?.nickname.isNullOrEmpty()) {
            getString(R.string.no_nickname)
        } else {
            user?.nickname
        }

        val radius = activity?.dpToPixel(17) ?: 0
        Glide.with(this)
            .load(user?.profileUrl)
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(radius))
                    .error(R.drawable.icon_avatar)
            )
            .into(binding.imageViewUserProfile)
    }

    private fun observeViewModel() {
        viewModel.createdRoomId.observe(requireActivity()) { resource ->
            Log.d("DashboardFragment", "observe() resource: $resource")
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> resource.data?.let { goToRoomActivity(it) }
                Status.ERROR -> {
                    val message = if (resource?.errorCode == SendBirdError.ERR_INVALID_PARAMS) {
                        getString(R.string.dashboard_invalid_room_params)
                    } else {
                        resource?.message
                    }
                    activity?.showAlertDialog(
                        getString(R.string.dashboard_can_not_create_room),
                        message ?: UNKNOWN_SENDBIRD_ERROR
                    )
                }
            }
        }

        viewModel.fetchedRoomId.observe(requireActivity()) { resource ->
            Log.d("DashboardFragment", "observe() resource: $resource")
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> resource.data?.let { goToPreviewActivity(it) }
                Status.ERROR -> {
                    activity?.showAlertDialog(
                        getString(R.string.dashboard_incorrect_room_id),
                        if (resource?.errorCode == 400200) {
                            getString(R.string.dashboard_incorrect_room_id_body)
                        } else {
                            resource?.message ?: UNKNOWN_SENDBIRD_ERROR
                        }
                    )
                }
            }
        }
    }

    private fun goToPreviewActivity(roomId: String) {
        val intent = Intent(context, PreviewActivity::class.java).apply {
            putExtra(EXTRA_ROOM_ID, roomId)
        }

        previewLauncher.launch(intent)
    }

    private fun goToRoomActivity(roomId: String) {
        val intent = Intent(context, RoomActivity::class.java).apply {
            putExtra(EXTRA_ROOM_ID, roomId)
            putExtra(EXTRA_IS_NEWLY_CREATED, true)
        }

        startActivity(intent)
    }
}
