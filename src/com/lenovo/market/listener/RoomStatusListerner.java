package com.lenovo.market.listener;

import org.jivesoftware.smackx.muc.ParticipantStatusListener;

import android.util.Log;

public class RoomStatusListerner implements ParticipantStatusListener {

    @Override
    public void joined(String participant) {
    }

    @Override
    public void left(String participant) {
    }

    @Override
    public void kicked(String participant, String actor, String reason) {
        Log.e("kicked", "=============");
        Log.d("participant", participant);
        Log.i("actor", actor);
        Log.e("reason", reason);
    }

    @Override
    public void voiceGranted(String participant) {
    }

    @Override
    public void voiceRevoked(String participant) {
    }

    @Override
    public void banned(String participant, String actor, String reason) {
    }

    @Override
    public void membershipGranted(String participant) {
    }

    @Override
    public void membershipRevoked(String participant) {
    }

    @Override
    public void moderatorGranted(String participant) {
    }

    @Override
    public void moderatorRevoked(String participant) {
    }

    @Override
    public void ownershipGranted(String participant) {
    }

    @Override
    public void ownershipRevoked(String participant) {
    }

    @Override
    public void adminGranted(String participant) {
    }

    @Override
    public void adminRevoked(String participant) {
    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {
    }
}
