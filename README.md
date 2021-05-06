# Sendbird Calls DirectCall for Android Quickstart

![Platform](https://img.shields.io/badge/platform-android-orange.svg)
![Languages](https://img.shields.io/badge/language-kotlin-orange)

## Introduction

Sendbird Calls SDK for Android is used to initialize, configure, and build voice and video group calling functionality into your Android client app. In this repository, you will find the steps you need to take before implementing the Calls SDK into a project, and a sample app which contains the code for implementing group calls.

### More about Sendbird Calls for Android

Find out more about Sendbird Calls for Android on [Calls for Android doc](https://sendbird.com/docs/calls/v1/android/getting-started/about-calls-sdk). If you need any help in resolving any issues or have questions, visit [our community](https://community.sendbird.com).

<br />

## Before getting started

This section shows you the prerequisites you need for testing Sendbird Calls for Android sample app.

### Requirements

The minimum requirements for Calls SDK for Android sample are:

- Android 4.1 (API level 16) or higher
- Java 8 or higher
- Gradle 3.4.0 or higher
- Calls SDK for Android 1.6.0 or higher

For more details on **installing and configuring the Calls SDK for Android**, refer to [Calls for Android doc](https://sendbird.com/docs/calls/v1/android/getting-started/install-calls-sdk#2-step-2-install-the-calls-sdk).

<br />

## Getting started

If you would like to try the sample app specifically fit to your usage, you can do so by following the steps below.

### Create a Sendbird application

 1. Login or Sign-up for an account on [Sendbird Dashboard](https://dashboard.sendbird.com).
 2. Create or select a calls-enabled application on the dashboard.
 3. Note your Sendbird application ID for future reference.

### Create test users

 1. On the Sendbird dashboard, navigate to the **Users** menu.
 2. Create at least two or more new users.
 3. Note the `user_id` of each user for future reference.

### Specify the Application ID

To run the sample Android app on the Sendbird application specified earlier, your Sendbird application ID must be specified. On the sample client app’s source code, replace `SAMPLE_APP_ID` with `APP_ID` which you can find on your Sendbird application information. 

```kotlin
// Constants.kt
const val SENDBIRD_APP_ID = "SAMPLE_APP_ID"
```

### Build and run the sample app

1. Build and run the sample app on your Android device.
2. Install the application onto at least two separate devices for each test user you created earlier.
3. If there are no two devices available, you can use an emulator to run the application instead.

For more detail on how to build and run an Android application, refer to [Android Documentation](https://developer.android.com/studio/run).

<br />

## Making your first group call

### Create a room

You can choose to create a room that supports up to 6 participants with video or a room that supports up to 25 participants with audio. When a user creates a room in your client app, the room’s status becomes `OPEN` and a `roomId` is generated.

You can create a room through the Calls API as shown below by using the `createRoom()` method.

```kotlin
val params = RoomParams(RoomType.SMALL_ROOM_FOR_VIDEO)
SendBirdCall.createRoom(params, object : RoomHandler {
    override fun onResult(room: Room?, e: SendBirdException?) {
         if (room == null || e != null) {
              // Handle error.
              return
         }

         // `room` is created with a unique identifier `room.roomId`.
    }
})
```

> Note: To delete a room, you have to do so explicitly through platform API which will be provided soon.

### Enter a room

A user can search the room with `roomId` to participate in a group call at any time. When a user enters a room, a participant is created with a unique `participantId` to represent the user in the room.

To enter a room, you must first acquire the room instance from Sendbird server by using `roomId` of the room. To fetch the most up-to-date room instance from Sendbird server, use the `SendBirdCall.fetchRoomById()` method. Also, you can use the `SendBirdCall.getCachedRoomById()` method that returns the most recently cached room instance from Sendbird Calls SDK.

```kotlin
SendBirdCall.fetchRoomById(ROOM_ID, object : RoomHandler {
    override fun onResult(room: Room?, e: SendBirdException?) {
         if (room == null || e != null) {
              // Handle error.
              return
         }

         // `room` with the identifier `ROOM_ID` is fetched from Sendbird Server.
    }
})

val room = SendBirdCall.getCachedRoomById(ROOM_ID)
// Returns the most recently cached room with the identifier `ROOM_ID` from the SDK.
//If there is no such room with the given room ID, `null` is returned.
```

> Note: When a user enters a room with multiple devices, a new participant for the same user will be created for each device or browser tab.
   
Once the room is retrieved, call the `enter()` method to enter the room.
```kotlin
room.enter(params, object : CompletionHandler {
    override fun onResult(e: SendBirdException?) {
        if (e != null) {
            // Handle error.
        }

        // User has successfully entered `room`.
    }
})
```

> Note: you have to share the roomId with other users for them to enter the room for group calls.

### Exit a room

To leave a room, call `Room.exit()`. On the room handlers of the remaining participants, the `RoomListener.onRemoteParticipantExited()` method will be called.

```kotlin
room.exit()
```

### Interact within a room

Participant’s actions, such as turning on or off their microphone or camera, in a room are handled by the participant objects.

To control the media of a local user, you can use the following methods from the Room.localParticipant obejct:

```kotlin
// Mutes the local participant's audio.
room.localParticipant?.muteMicrophone()

// Unmutes the local participant's audio.
room.localParticipant?.unmuteMicrophone()

// Stops the local participant's vido.
room.localParticipant?.stopVideo()

// Starts the local participant's video.
room.localParticipant?.startVideo()

// Switches the local partipant's front and back cameras.
room.localParticipant?.switchCamera(completionHandler)
```

### Display video view

When there is a participant in the room, a media stream is established between a participant and Sendbird server to support group calls.You can configure the user interface for participants in a room by using the properties in `Participant`.

#### Receive media stream
The following is the process of how participants can send and receive media streams in a room:

**Step 1**: To send a media stream, a participant who would like to send its media stream has to be connected to Sendbird server.

**Step 2**: To receive a media stream, a participant who would like to receive a media stream from another participant has to be connected to the media server. Once connected, `onRemoteParticipantStreamStarted()` will be invoked which notifies that the receiving media stream has started.

**Step 3**: Add a view to show the received media stream.

To receive a video stream from a local participant, configure `videoView` property as shown below:
```kotlin
room.enter(params, object : CompletionHandler {
    override fun onResult(e: SendBirdException?) {
        if (e != null) {
            // Handle error.
        }

        // User has successfully entered `room`.
        val localParticipantVideoView: SendBirdVideoView = findViewById(R.id.local_participant_video_view)
        room.localParticipant?.videoView = localParticipantVideoView

    }
})
```

To receive a video stream from a remote participant, configure the `videoView` property as shown below:
```kotlin
room.addListener(UNIQUE_ID, object : RoomListener {
    ...
    override fun onRemoteParticipantEntered(participant: RemoteParticipant) {
        val remoteParticipantVideoView: SendBirdVideoView = findViewById(R.id.remote_participant_video_view)
        participant.videoView = remoteParticipant
    }
    ...
})
```

#### Manage video layout
You can show participants in gallery view as they enter or exit the room by using `RecyclerView` and `GridLayoutManager` which dynamically change views. You can set the number of items to be the count of `room.participants` and the custom `ViewHolder` to represent a participant.

When the below methods in `RoomListener` are called, information for `room.participants` gets updated and the number of items are changed accordingly. To have the custom `ViewHolder` added or removed, you need to call `adapter.notifyDataSetChanged()` for the methods.

#### Show default image for user
If a participant is not connected to the call or has turned off their video, you can set an default image to show on the screen for that participant whose view will otherwise be shown as black to other participants. To check whether a participant has turned on their video or is connected to the room for a video call, refer to the `isVideoEnabled` and the state properties of a `Participant` object.

It is recommended to show an image such as the user’s profile image as the default image when the `onRemoteParticipantEntered()` method is invoked.

When the `onRemoteParticipantStreamStarted()` method is invoked, create a new `SendBirdVideoView` and set it to the participant by using `participant.videoView` and remove the default image.

### Handle events in a room

A user can receive events such as other participants entering or leaving the room or changing their media settings only about a room that the user currently participates in.

#### Add event listener
Add event listener for the user to receive events that occur in a room that the user joins as a participant.

```kotlin
room.addListener(UNIQUE_ID, MyRoomListener())

class MyRoomListener : RoomListener {
}
```

#### Receive events on enter and exit
When a participant joins or leaves the room, other participants in the room will receive the following events.
```kotlin
class MyRoomListener : RoomListener {
    ...
    // Called when a remote participant has entered a room.
    override fun onRemoteParticipantEntered(participant: RemoteParticipant) {}

    // Called when a remote participant has exited a room.
    override fun onRemoteParticipantExited(participant: RemoteParticipant) {}
    ...
}
```

#### Receive events on media settings
A local participant can change the media settings such as muting their microphone or turning off their camera using `muteMicrophone()` or `stopVideo()`. Other participants will receive events that notify them of the corresponding media setting changes.

```kotlin
class MyRoomListener : RoomListener {
    // Called when a remote participant's video settings have changed.
    override fun onRemoteVideoSettingsChanged(participant: RemoteParticipant) {}

    // Called when a remote participant's audio settings have changed.
    override fun onRemoteAudioSettingsChanged(participant: RemoteParticipant) {}
}
```

#### Remove event listener
To stop receiving events about a room, remove the registered listeners as shown below:
```kotlin
// Removes a listener that have the matching identifier.
room.removeListener(UNIQUE_ID)

// Removes all listeners to stop receiving events about a room.
room.removeAllListeners()
```

### Reconnect to media stream

When a participant loses media stream in a room due to connection issues, Sendbird Calls SDK automatically tries to reconnect the participant’s media streaming in the room. If the Calls SDK fails to reconnect for about 40 seconds, an error will be returned with the error code `ERR_LOCAL_PARTICIPANT_LOST_CONNECTION`.
```kotlin
class MyRoomListener : RoomListener {
// Called when an error has occurred.
    override fun onError(e: SendBirdException, participant: Participant?) {
        if (e.code == SendBirdError.ERR_LOCAL_PARTICIPANT_LOST_CONNECTION) {
                // handle reconnection failure here.
                // Clear resources for group calls.
          }
     }
}
```



<br />

## Reference

For further detail on Sendbird Calls for Android, refer to [Sendbird Calls SDK for Android README](https://github.com/sendbird/sendbird-calls-android/blob/master/README.md).
