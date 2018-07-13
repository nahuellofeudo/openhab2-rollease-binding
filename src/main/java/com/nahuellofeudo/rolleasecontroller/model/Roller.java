package com.nahuellofeudo.rolleasecontroller.model;


import com.nahuellofeudo.rolleasecontroller.listener.RollerStateListener;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Roller {
  private long id;
  private String name;
  private String roomId;
  private int unknown1;
  private int state;
  private int percentClosed;
  private long unknown2;
  private Set<RollerStateListener> stateListeners;

  public Roller(long id, String name, String roomId, int unknown1, int state, int percentClosed, long unknown2) {
    this.id = id;
    this.name = name;
    this.roomId = roomId;
    this.unknown1 = unknown1;
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
    return String.format("Roller ID: %016x | %s | Batt %d%% ?| State? %02x | Pos: %d%% | ??? %02x",
            this.id, this.name, this.unknown1, this.state, this.percentClosed, unknown2);
  }

  public int getPercentClosed() {
    return percentClosed;
  }

  public void setPercentClosed(int percentClosed) {
    int oldPercentClosed = percentClosed;
    this.percentClosed = percentClosed;
    for (RollerStateListener listener: stateListeners) {
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
  }

  public void addStateListeners(Set<RollerStateListener> rollerStateListeners) {
    this.stateListeners.addAll(rollerStateListeners);
  }

  public void setUnknown1(int unknown1) {
    this.unknown1 = unknown1;
  }

  public void setState(int state) {
    this.state = state;
  }

  public void setUnknown2(long unknown2) {
    this.unknown2 = unknown2;
  }
}

