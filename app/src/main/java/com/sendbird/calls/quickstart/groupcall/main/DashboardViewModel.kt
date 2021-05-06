package com.sendbird.calls.quickstart.groupcall.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sendbird.calls.*
import com.sendbird.calls.handler.CompletionHandler
import com.sendbird.calls.handler.RoomHandler
import com.sendbird.calls.quickstart.groupcall.util.Resource
import com.sendbird.calls.quickstart.groupcall.util.Status

class DashboardViewModel : ViewModel() {
    private val _createdRoomId: MutableLiveData<Resource<String>> = MutableLiveData()
    val createdRoomId: LiveData<Resource<String>> = _createdRoomId

    private val _fetchedRoomId: MutableLiveData<Resource<String>> = MutableLiveData()
    val fetchedRoomId: LiveData<Resource<String>> = _fetchedRoomId

    fun createAndEnterRoom() {
        if (_createdRoomId.value?.status == Status.LOADING) {
            return
        }

        _createdRoomId.postValue(Resource.loading(null))
        val params = RoomParams(RoomType.SMALL_ROOM_FOR_VIDEO)
        SendBirdCall.createRoom(params, object : RoomHandler {
            override fun onResult(room: Room?, e: SendBirdException?) {
                if (e != null) {
                    _createdRoomId.postValue(Resource.error(e.message, e.code, null))
                } else {
                    room?.enter(EnterParams().setAudioEnabled(true).setVideoEnabled(true), object : CompletionHandler {
                        override fun onResult(e: SendBirdException?) {
                            if (e != null) {
                                _createdRoomId.postValue(Resource.error(e.message, e.code, null))
                            } else {
                                _createdRoomId.postValue(Resource.success(room.roomId))
                            }
                        }
                    })
                }
            }
        })
    }

    fun fetchRoomById(roomId: String) {
        if (roomId.isEmpty()) {
            return
        }

        if (_fetchedRoomId.value?.status == Status.LOADING) {
            return
        }

        _fetchedRoomId.postValue(Resource.loading(null))
        SendBirdCall.fetchRoomById(roomId, object : RoomHandler {
            override fun onResult(room: Room?, e: SendBirdException?) {
                if (e != null) {
                    _fetchedRoomId.postValue(Resource.error(e.message, e.code, null))
                } else {
                    _fetchedRoomId.postValue(Resource.success(room?.roomId))
                }
            }
        })
    }
}
