package com.sendbird.calls.quickstart.groupcall.room

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sendbird.calls.Room
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.FragmentParticipantListBinding

class ParticipantListFragment : Fragment() {

    lateinit var binding: FragmentParticipantListBinding
    val args: ParticipantListFragmentArgs by navArgs()
    lateinit var room: Room

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_participant_list, container, false)
        room = SendBirdCall.getCachedRoomById(args.roomId) ?: throw IllegalStateException("Fragment $this should have Room instance.")
        initView()
        return binding.root
    }

    private fun initView() {
        binding.participantListTextViewTitle.text = String.format(
            getString(R.string.participant_list_title),
            room.participants.size
        )

        binding.participantListImageViewClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.participantListRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ParticipantListAdapter(room.roomId).apply {
                participants = room.participants
            }
        }
    }
}
