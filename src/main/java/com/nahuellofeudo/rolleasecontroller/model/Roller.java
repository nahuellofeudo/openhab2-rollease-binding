package com.nahuellofeudo.rolleasecontroller.model;

import java.util.HashSet;
import java.util.Set;

import com.nahuellofeudo.rolleasecontroller.listener.RollerStateListener;

public class Roller {
    private long id;
    private String name;
    private String roomId;
    private int batteryLevel;
    private int state;
    private int percentClosed;
    private long unknown2;
    private Set<RollerStateListener> stateListeners;

    public Roller(long id, String name, String roomId, int unknown1, int state, int percentClosed, long unknown2) {
        this.id = id;
        this.name = name;
        this.roomId = roomId;
        this.batteryLevel = unknown1;
        this.state = state;
        this.percentClosed = percentClosed;
        this.unknown2 = unknown2;
        this.stateListeners = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Roller ID: %016x | %s | Batt %d%% ?| State? %02x | Pos: %d%% | ??? %02x", this.id,
                this.name, this.batteryLevel, this.state, this.percentClosed, unknown2);
    }

    public int getPercentClosed() {
        return percentClosed;
    }

    public void setPercentClosed(int percentClosed) {
        int oldPercentClosed = percentClosed;
        this.percentClosed = percentClosed;
        for (RollerStateListener listener : stateListeners) {
            listener.rollerPositionChanged(this, oldPercentClosed);
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
        listener.rollerPositionChanged(this, this.getPercentClosed());
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

    public void setState(int state) {
        this.state = state;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setUnknown2(long unknown2) {
        this.unknown2 = unknown2;
    }
}
