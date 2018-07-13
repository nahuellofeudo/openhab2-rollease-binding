package com.nahuellofeudo.rolleasecontroller.listener;

import com.nahuellofeudo.rolleasecontroller.model.Hub;

public interface HubStatusListener {
    public enum HubStatus {
        OFFLINE,
        CONNECTING,
        ERROR,
        ONLINE
    }
    public void hubStatusChanged(HubStatus currentStatus, Hub hub);
}
