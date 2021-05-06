package com.sendbird.calls.quickstart.groupcall.room

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sendbird.calls.Participant
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.ItemParticipantListShareRoomIdBinding
import com.sendbird.calls.quickstart.groupcall.databinding.ItemParticipantListUserBinding
import com.sendbird.calls.quickstart.groupcall.util.copyText
import com.sendbird.calls.quickstart.groupcall.util.dpToPixel
import com.sendbird.calls.quickstart.groupcall.util.showToast

class ParticipantListAdapter(
    val roomId: String
) : RecyclerView.Adapter<ParticipantListAdapter.BaseViewHolder>() {
    var participants: List<Participant> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_PARTICIPANT -> {
                ParticipantViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_participant_list_user,
                        parent,
                        false
                    )
                )
            }

            VIEW_TYPE_SHARE_ROOM_ID -> {
                ShareRoomIdViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_participant_list_share_room_id,
                        parent,
                        false
                    )
                )
            }

            else -> throw IllegalStateException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val participant = participants.getOrNull(position)
        holder.bind(participant)
    }

    override fun getItemCount(): Int {
        return participants.size.plus(1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < participants.size) {
            VIEW_TYPE_PARTICIPANT
        } else {
            VIEW_TYPE_SHARE_ROOM_ID
        }
    }

    abstract class BaseViewHolder(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        abstract fun bind(participant: Participant?)
    }

    inner class ParticipantViewHolder(
        val binding: ItemParticipantListUserBinding
    ): BaseViewHolder(binding.root) {
        override fun bind(participant: Participant?) {
            binding.participantListItemImageViewProfile.apply {
                layoutParams = layoutParams.apply {
                    width = context.dpToPixel(36)
                    height = context.dpToPixel(36)
                }
            }

            val radius = context.dpToPixel(18)
            Glide.with(context)
                .load(participant?.user?.profileUrl)
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(radius))
                        .error(R.drawable.icon_avatar)
                )
                .into(binding.participantListItemImageViewProfile)

            binding.participantListItemTextViewName.text = if (participant?.user?.nickname.isNullOrEmpty()) {
                context.getString(R.string.no_nickname)
            } else {
                participant?.user?.nickname
            }

            binding.participantListItemTextViewUserId.text = if (participant?.user?.userId.isNullOrEmpty()) {
                context.getString(R.string.no_nickname)
            } else {
                String.format(context.getString(R.string.user_id_template), participant?.user?.userId)
            }
        }
    }

    inner class ShareRoomIdViewHolder(
        val binding: ItemParticipantListShareRoomIdBinding
    ): BaseViewHolder(binding.root) {
        override fun bind(participant: Participant?) {
            binding.participantListItemImageViewProfile.apply {
                layoutParams = layoutParams.apply {
                    width = context.dpToPixel(24)
                    height = context.dpToPixel(24)
                }
            }

            binding.root.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, roomId)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
        }
    }

    companion object {
        const val VIEW_TYPE_PARTICIPANT = 0
        const val VIEW_TYPE_SHARE_ROOM_ID = 1
    }
}