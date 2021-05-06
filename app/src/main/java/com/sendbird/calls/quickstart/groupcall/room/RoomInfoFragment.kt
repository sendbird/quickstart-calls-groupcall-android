package com.sendbird.calls.quickstart.groupcall.room

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.FragmentRoomInfoBinding

class RoomInfoFragment : Fragment() {
    lateinit var binding: FragmentRoomInfoBinding
    private val args: RoomInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_room_info, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        binding.fragmentRoomInfoTextViewRoomId.text = args.roomId
        binding.fragmentRoomInfoTextViewCreatedBy.text =
            SendBirdCall.getCachedRoomById(args.roomId)?.createdBy ?: ""
        binding.roomInfoImageViewLeftArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
