package com.sendbird.calls.quickstart.groupcall.room

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexboxLayoutManager
import com.sendbird.calls.LocalParticipant
import com.sendbird.calls.Participant
import com.sendbird.calls.ParticipantState
import com.sendbird.calls.quickstart.groupcall.R
import com.sendbird.calls.quickstart.groupcall.databinding.ItemParticipantBinding
import com.sendbird.calls.quickstart.groupcall.util.dpToPixel
import org.webrtc.RendererCommon

class ParticipantAdapter(
    participants: List<Participant>
) : RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {

    var participants: List<Participant> = participants
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var parentWidth: Int = 0
    var parentHeight: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        parentWidth = parent.width
        parentHeight = parent.height
        return ParticipantViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_participant,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        setViewHolderLayout(holder, position)
        holder.bind(participants[position])
    }

    override fun getItemCount(): Int = participants.size

    private fun setViewHolderLayout(holder: ParticipantViewHolder, position: Int) {
        val spacing = holder.binding.root.context.dpToPixel(4)

        // calculate layout position type
        val type = if (position % 2 == 0) {
            if (itemCount % 2 == 1 && itemCount - 1 == position) {
                ViewHolderPositionType.CENTER
            } else {
                ViewHolderPositionType.LEFT
            }
        } else {
            ViewHolderPositionType.RIGHT
        }

        // set margins
        val leftMargin = when (type) {
            ViewHolderPositionType.LEFT -> 0
            ViewHolderPositionType.RIGHT -> spacing.div(2)
            ViewHolderPositionType.CENTER -> 0
        }

        val rightMargin = when (type) {
            ViewHolderPositionType.LEFT -> spacing.div(2)
            ViewHolderPositionType.RIGHT -> 0
            ViewHolderPositionType.CENTER -> 0
        }

        val topMargin = if (position / 2 >= 1) {
            spacing
        } else {
            0
        }

        // calculate width and height
        val width = if (itemCount == 1) {
            parentWidth
        } else {
            parentWidth.div(2)
        }.minus(leftMargin).minus(rightMargin)

        val numberOfRows = itemCount.plus(1).div(2)
        val height = parentHeight.div(numberOfRows).minus(topMargin)

        // calculate video view's ratio
        val parentRatio = width.toDouble().div(height)

        val expectedRatio = 3.0.div(4)
        val currentRow = position.div(2) + 1
        var gravity = when (type) {
            ViewHolderPositionType.RIGHT -> Gravity.START
            ViewHolderPositionType.LEFT -> Gravity.END
            ViewHolderPositionType.CENTER -> Gravity.CENTER_HORIZONTAL
        }

        gravity = when {
            currentRow.times(2) < numberOfRows.plus(1) -> gravity.or(Gravity.BOTTOM)
            currentRow.times(2) > numberOfRows.plus(1) -> gravity.or(Gravity.TOP)
            else -> gravity.or(Gravity.CENTER_VERTICAL)
        }

        holder.binding.root.layoutParams = FlexboxLayoutManager.LayoutParams(width, height).apply {
            setMargins(leftMargin, topMargin, rightMargin, 0)
        }

        holder.binding.participantRelativeLayoutVideoView.layoutParams = LinearLayout.LayoutParams(
            if (parentRatio > expectedRatio) height.times(expectedRatio).toInt() else width,
            if (parentRatio > expectedRatio) height else width.div(expectedRatio).toInt()
        )

        holder.binding.participantLinearLayout.gravity = gravity
    }

    enum class ViewHolderPositionType {
        LEFT, RIGHT, CENTER
    }

    class ParticipantViewHolder(
        val binding: ItemParticipantBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(participant: Participant) {
            val context = binding.root.context
            val userId = String.format(
                context.getString(R.string.user_id_template),
                participant.user.userId
            )

            binding.participantTextViewUserId.text = userId

            binding.participantImageViewAudioMuted.visibility = if (participant.isAudioEnabled) {
                View.GONE
            } else {
                View.VISIBLE
            }

            if (participant.isVideoEnabled) {
                binding.participantSendbirdVideoView.visibility = View.VISIBLE
                binding.participantImageViewProfile.visibility = View.GONE
            } else {
                binding.participantSendbirdVideoView.visibility = View.GONE
                binding.participantImageViewProfile.visibility = View.VISIBLE
                val radius = context.dpToPixel(40)
                participant.user.profileUrl?.let {
                    Glide.with(context)
                        .load(it)
                        .apply(
                            RequestOptions()
                                .transform(RoundedCorners(radius))
                                .error(R.drawable.icon_avatar)
                        )
                        .into(binding.participantImageViewProfile)
                }
            }

            val isStreaming = participant is LocalParticipant
                    || participant.state == ParticipantState.CONNECTED

            if (isStreaming && participant.videoView != binding.participantSendbirdVideoView) {
                participant.videoView = binding.participantSendbirdVideoView.apply {
                    setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
                }
            }
        }
    }
}
