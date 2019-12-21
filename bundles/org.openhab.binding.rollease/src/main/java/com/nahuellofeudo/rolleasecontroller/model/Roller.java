package com.nahuellofeudo.rolleasecontroller.model;

import java.util.HashSet;
import java.util.Set;

import com.nahuellofeudo.rolleasecontroller.listener.RollerStateListener;

public class Roller {
    private long id;
    private String name;
    private String roomId;
    private int batteryLevel;
    private int percentOpen;
    private Set<RollerStateListener> stateListeners;

    public Roller(long id, String name, String roomId, int unknown1, int percentOpen) {
        this.id = id;
        this.name = name;
        this.roomId = roomId;
        this.batteryLevel = unknown1;
        this.percentOpen = percentOpen;
        this.stateListeners = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Roller ID: %016x | %s | Batt %d%% ?| Pos: %d%%", this.id, this.name, this.batteryLevel,
                this.percentOpen);
    }

    public int getPercentOpen() {
        return percentOpen;
    }

    public void setPercentOpen(int percentOpen) {
        if (percentOpen > 100) percentOpen = 100;
        if (percentOpen < 0) percentOpen = 0;

        int oldPercentOpen = this.percentOpen;
        this.percentOpen = percentOpen;
        for (RollerStateListener listener : stateListeners) {
            listener.rollerPositionChanged(this, oldPercentOpen);
        }
    }

    public String getName() {
        return name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void addStateListener(RollerStateListener listener) {
        this.stateListeners.add(listener);
        listener.rollerPositionChanged(this, this.getPercentOpen());
        listener.rollerBatteryChanged(this, this.batteryLevel);
    }

    public void addStateListeners(Set<RollerStateListener> rollerStateListeners) {
        for (RollerStateListener l : rollerStateListeners) {
            this.addStateListener(l);
        }
    }

    public void setBatteryLevel(int batteryLevel) {
        int oldBatteryLevel = this.batteryLevel;
        this.batteryLevel = batteryLevel;
        for (RollerStateListener listener : stateListeners) {
            listener.rollerBatteryChanged(this, oldBatteryLevel);
        }

    }

    public int getBatteryLevel() {
        return batteryLevel;
    }
}
