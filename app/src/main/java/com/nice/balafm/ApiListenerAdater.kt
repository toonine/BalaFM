package com.nice.balafm

import com.algebra.sdk.OnAccountListener
import com.algebra.sdk.OnChannelListener
import com.algebra.sdk.OnMediaListener
import com.algebra.sdk.OnSessionListener
import com.algebra.sdk.entity.Channel
import com.algebra.sdk.entity.Contact
import com.algebra.sdk.entity.HistoryRecord
import com.algebra.sdk.entity.UserProfile

interface OnAccountListenerDefault : OnAccountListener {
    override fun onAuthRequestReply(p0: Int, p1: Int, p2: String) {}

    override fun onChangePassWord(p0: Int, p1: Boolean) {}

    override fun onKickedOut(p0: Int, p1: Int) {}

    override fun onLogout() {}

    override fun onAuthResetPassReply(p0: Int, p1: Int) {}

    override fun onFriendStatusUpdate(p0: Int, p1: Int, p2: Int) {}

    override fun onCreateUser(uid: Int, result: Int, account: String?) {}

    override fun onSetStatusReturn(p0: Int, p1: Boolean) {}

    override fun onSelfLocationReported(p0: Int) {}

    override fun onLogger(p0: Int, p1: String?) {}

    override fun onSelfStateChange(p0: Int, p1: Int) {}

    override fun onFriendsSectionGet(p0: Int, p1: Int, p2: Int, p3: Int, p4: MutableList<Contact>?) {}

    override fun onHearbeatLost(p0: Int, p1: Int) {}

    override fun onSetNickName(p0: Int) {}

    override fun onShakeScreenAck(p0: Int, p1: Int, p2: Int) {}

    override fun onShakeScreenReceived(p0: Int, p1: String?, p2: String?) {}

    override fun onSelfLocationAvailable(p0: Int, p1: Double?, p2: Double?, p3: Double?) {}

    override fun onUserLocationNotify(p0: Int, p1: String?, p2: Double?, p3: Double?) {}

    override fun onAuthBindingReply(p0: Int, p1: Int, p2: String?) {}

    override fun onAskUnbind(p0: Int) {}

    override fun onLogin(uid: Int, result: Int, uProfile: UserProfile?) {}

    override fun onAuthRequestPassReply(p0: Int, p1: Int, p2: String?) {}

    override fun onSmsRequestReply(p0: Int) {}
}

interface OnChannelListenerDefault : OnChannelListener {
    override fun onChannelNameChanged(p0: Int, p1: Int, p2: Int, p3: String?) {

    }

    override fun onPubChannelCreate(p0: Int, p1: Int, p2: Int) {
    }

    override fun onCallMeetingStarted(p0: Int, p1: Int, p2: Int, p3: MutableList<Contact>?) {
    }

    override fun onPubChannelSearchResult(p0: Int, p1: MutableList<Channel>?) {
    }

    override fun onPubChannelDeleted(p0: Int, p1: Int) {
    }

    override fun onChannelMemberListGet(p0: Int, p1: Int, p2: Int, p3: MutableList<Contact>?) {
    }

    override fun onCallMeetingStopped(p0: Int, p1: Int) {
    }

    override fun onChannelListGet(uid: Int, dfltChannel: Channel?, chs: MutableList<Channel>?) {
    }

    override fun onChannelAdded(p0: Int, p1: Int, p2: Int, p3: String?) {
    }

    override fun onDefaultChannelSet(p0: Int, p1: Int, p2: Int) {
    }

    override fun onChannelMemberAdded(p0: Int, p1: Int, p2: MutableList<Contact>?) {
    }

    override fun onChannelMemberRemoved(p0: Int, p1: Int, p2: MutableList<Int>?) {
    }

    override fun onChannelRemoved(p0: Int, p1: Int, p2: Int) {
    }

    override fun onAdverChannelsGet(p0: Int, p1: Channel?, p2: MutableList<Channel>?) {
    }

    override fun onPubChannelUnfocusResult(p0: Int, p1: Int) {
    }

    override fun onPubChannelRenamed(p0: Int, p1: Int) {
    }

    override fun onPubChannelFocusResult(p0: Int, p1: Int) {
    }
}

interface OnSessionListenerDefault : OnSessionListener {
    override fun onSessionGet(p0: Int, p1: Int, p2: Int, p3: Int) {

    }

    override fun onDialogEstablished(p0: Int, p1: Int, p2: Int, p3: MutableList<Int>?) {
    }

    override fun onSessionEstablished(selfUserId: Int, type: Int, sessionId: Int) {
    }

    override fun onSessionPresenceRemoved(p0: Int, p1: Int, p2: MutableList<Int>?) {
    }

    override fun onDialogLeaved(p0: Int, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSessionReleased(p0: Int, p1: Int, p2: Int) {
    }

    override fun onSessionPresenceAdded(p0: Int, p1: Int, p2: MutableList<Contact>?) {
    }

    override fun onDialogPresenceAdded(p0: Int, p1: Int, p2: MutableList<Int>?) {
    }

    override fun onDialogPresenceRemoved(p0: Int, p1: Int, p2: MutableList<Int>?) {
    }
}

interface OnMediaListenerDefault : OnMediaListener {
    override fun onPttButtonPressed(p0: Int, p1: Int) {
    }

    override fun onBluetoothConnect(p0: Int) {
    }

    override fun onTalkReleaseConfirm(p0: Int, p1: Int) {
    }

    override fun onMediaSenderCutted(p0: Int, p1: Int) {
    }

    override fun onPlayLastSpeaking(p0: Int, p1: Int) {
    }

    override fun onMediaInitializedEnd(p0: Int, p1: Int, p2: Int) {
    }

    override fun onStartPlaying(p0: Int, p1: Int, p2: Int, p3: Int) {
    }

    override fun onNewSpeakingCatched(hisRec: HistoryRecord?) {
    }

    override fun onPlayStopped(p0: Int) {
    }

    override fun onMediaSenderReport(p0: Long, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onRecorderMeter(p0: Int, p1: Int) {
    }

    override fun onTalkRequestQueued(p0: Int, p1: Int, p2: Int) {
    }

    override fun onBluetoothBatteryGet(p0: Int) {
    }

    override fun onTalkTransmitBroken(p0: Int, p1: Int) {
    }

    override fun onSomeoneAttempt(p0: Int, p1: Int, p2: Int) {
    }

    override fun onThatAttemptQuit(p0: Int, p1: Int, p2: Int) {
    }

    override fun onSomeoneSpeaking(speaker: Int, ctype: Int, sessionId: Int, tag: Int, dur10ms: Int) {
    }

    override fun onMediaReceiverReport(p0: Long, p1: Int, p2: Int, p3: Int, p4: Int) {
    }

    override fun onThatoneSayOver(p0: Int, p1: Int) {
    }

    override fun onPlayLastSpeakingEnd(p0: Int) {
    }

    override fun onTalkRequestConfirm(p0: Int, p1: Int, p2: Int, p3: Int, p4: Boolean) {
    }

    override fun onTalkRequestDeny(p0: Int, p1: Int, p2: Int) {
    }

    override fun onPlayerMeter(p0: Int, p1: Int) {
    }
}