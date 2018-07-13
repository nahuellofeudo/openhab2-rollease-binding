package com.nahuellofeudo.rolleasecontroller.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.nahuellofeudo.rolleasecontroller.listener.HubRollersListener;
import com.nahuellofeudo.rolleasecontroller.listener.HubRoomsListener;
import com.nahuellofeudo.rolleasecontroller.listener.HubStatusListener;
import com.nahuellofeudo.rolleasecontroller.listener.HubStatusListener.HubStatus;
import com.nahuellofeudo.rolleasecontroller.listener.RollerStateListener;

public class Hub {
    Map<String, Room> rooms;
    Map<Long, Roller> rollers;
    private String firmwareVersion;
    private String username;
    private String password;
    private HubStatus currentStatus;

    // These keep track of listeners
    private Set<HubStatusListener> hubStatusListeners;
    private Set<HubRoomsListener> roomsListeners;
    private Set<HubRollersListener> rollersListeners;
    private Set<RollerStateListener> rollerStateListeners;

    public Hub() {
        this.rooms = new HashMap<String, Room>();
        this.rollers = new HashMap<Long, Roller>();
        hubStatusListeners = new HashSet<>();
        roomsListeners = new HashSet<>();
        rollersListeners = new HashSet<>();
        rollerStateListeners = new HashSet<>();
        currentStatus = HubStatus.OFFLINE;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getUsername() {
        return username;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addRoom(Room room) {
        this.rooms.put(room.getId(), room);
        for (HubRoomsListener listener : roomsListeners) {
            listener.roomAdded(room, this);
        }
    }

    public void addRoller(Roller roller) {
        this.rollers.put(roller.getId(), roller);
        roller.addStateListeners(this.rollerStateListeners);
        for (HubRollersListener listener : rollersListeners) {
            listener.rollerAdded(roller, this);
        }
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<Long, Roller> getRollers() {
        return rollers;
    }

    public void addHubStatusListener(HubStatusListener listener) {
        hubStatusListeners.add(listener);
    }

    public void addRoomListener(HubRoomsListener listener) {
        roomsListeners.add(listener);
    }

    public void addRollerListener(HubRollersListener listener) {
        rollersListeners.add(listener);
    }

    public Roller getRollerById(long id) {
        return rollers.get(id);
    }

    public HubStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(HubStatus currentStatus) {
        this.currentStatus = currentStatus;
        for (HubStatusListener listener : this.hubStatusListeners) {
            listener.hubStatusChanged(currentStatus, this);
        }
    }

}
