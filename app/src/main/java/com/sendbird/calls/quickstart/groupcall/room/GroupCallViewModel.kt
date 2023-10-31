package com.sendbird.calls.quickstart.groupcall.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sendbird.calls.*
import com.sendbird.calls.handler.CompletionHandler
import com.sendbird.calls.quickstart.groupcall.util.Resource

class GroupCallViewModel(
    roomId: String
) : ViewModel() {
    private val _participants: MutableLiveData<List<Participant>> = MutableLiveData()
    val participants: LiveData<List<Participant>> = _participants

    private val _localParticipant: MutableLiveData<LocalParticipant> = MutableLiveData()
    val localParticipant: LiveData<LocalParticipant> = _localParticipant

    private val _isExited: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val isExited: LiveData<Resource<Boolean>> = _isExited

    private val _currentAudioDevice: MutableLiveData<AudioDevice> = MutableLiveData()
    val currentAudioDevice: LiveData<AudioDevice> = _currentAudioDevice

    private val room: Room? = SendBirdCall.getCachedRoomById(roomId)

    init {
        _participants.postValue(sortedParticipants)
        room?.addListener(TAG, RoomListenerImpl())
        _currentAudioDevice.value = room?.currentAudioDevice
    }

    fun exit() {
        room?.let {
            _isExited.postValue(Resource.loading(null))
            try {
                it.exit()
                _isExited.postValue(Resource.success(true))
            } catch (e: SendBirdException) {
                _isExited.postValue(Resource.error(e.message, e.code, false))
            }
        }
    }

    fun muteMicrophone() {
        room?.localParticipant?.muteMicrophone()
        _localParticipant.postValue(room?.localParticipant)
        _participants.postValue(sortedParticipants)
    }

    fun unmuteMicrophone() {
        room?.localParticipant?.unmuteMicrophone()
        _localParticipant.postValue(room?.localParticipant)
        _participants.postValue(sortedParticipants)
    }

    fun stopLocalVideo() {
        room?.localParticipant?.stopVideo()
        _localParticipant.postValue(room?.localParticipant)
        _participants.postValue(sortedParticipants)
    }

    fun startLocalVideo() {
        room?.localParticipant?.startVideo()
        _localParticipant.postValue(room?.localParticipant)
        _participants.postValue(sortedParticipants)
    }

    fun switchCamera() {
        room?.localParticipant?.switchCamera(object : CompletionHandler {
            override fun onResult(e: SendBirdException?) {
                Log.d(TAG, "switchCamera() result: $e")
            }
        })
    }

    fun selectAudioDevice(audioDevice: AudioDevice) {
        room?.selectAudioDevice(audioDevice, object : CompletionHandler {
            override fun onResult(e: SendBirdException?) {
                Log.d(TAG, "selectAudioDevice() result: $e")
            }
        })
    }

    fun getAvailableAudioDevices(): Set<AudioDevice> {
        return room?.availableAudioDevices ?: setOf()
    }

    private val sortedParticipants: List<Participant>
        get() {
            if (room == null) {
                return listOf()
            }

            return room.participants.sortedBy {
                if (it is LocalParticipant) {
                    0
                } else {
                    it.enteredAt
                }
            }
        }

    inner class RoomListenerImpl : RoomListener {
        override fun onAudioDeviceChanged(
            currentAudioDevice: AudioDevice?,
            availableAudioDevices: Set<AudioDevice>
        ) {
            Log.d(
                TAG,
                "onAudioDeviceChanged() called with: currentAudioDevice = $currentAudioDevice, availableAudioDevices = $availableAudioDevices"
            )

            _currentAudioDevice.postValue(currentAudioDevice)
        }

        override fun onCustomItemsDeleted(deletedKeys: List<String>) {
        }

        override fun onCustomItemsUpdated(updatedKeys: List<String>) {
        }

        override fun onDeleted() {
        }

        override fun onError(e: SendBirdException, participant: Participant?) {
            Log.d(TAG, "onError() called with: e = $e, participant = $participant")
            if (e.code == SendBirdError.ERR_LOCAL_PARTICIPANT_LOST_CONNECTION) {
                // reconnection failed.
            }
        }

        override fun onInvitationAccepted(roomInvitation: RoomInvitation) {
        }

        override fun onInvitationCanceled(roomInvitation: RoomInvitation) {
        }

        override fun onInvitationDeclined(roomInvitation: RoomInvitation) {
        }

        override fun onLocalParticipantDisconnected(participant: LocalParticipant) {
        }

        override fun onLocalParticipantReconnected(participant: LocalParticipant) {
        }

        override fun onRemoteAudioSettingsChanged(participant: RemoteParticipant) {
            Log.d(TAG, "onRemoteAudioSettingsChanged() called with: participant = $participant")
            _participants.postValue(_participants.value)
        }

        override fun onRemoteParticipantEntered(participant: RemoteParticipant) {
            Log.d(TAG, "onRemoteParticipantEntered() called with: participant = $participant")
            _participants.postValue(sortedParticipants)
        }

        override fun onRemoteParticipantExited(participant: RemoteParticipant) {
            Log.d(TAG, "onRemoteParticipantExited() called with: participant = $participant")
            _participants.postValue(sortedParticipants)
        }

        override fun onRemoteParticipantStreamStarted(participant: RemoteParticipant) {
            Log.d(TAG, "onRemoteParticipantStreamStarted() called with: participant = $participant")
            _participants.postValue(sortedParticipants)
        }

        override fun onRemoteVideoSettingsChanged(participant: RemoteParticipant) {
            Log.d(TAG, "onRemoteVideoSettingsChanged() called with: participant = $participant")
            _participants.postValue(_participants.value)
        }
    }

    companion object {
        val TAG: String = GroupCallViewModel::class.java.simpleName
    }
}
