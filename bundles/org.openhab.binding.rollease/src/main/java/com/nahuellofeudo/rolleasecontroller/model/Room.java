package com.nahuellofeudo.rolleasecontroller.model;

import java.util.Observable;

public class Room extends Observable {
  private String id;
  private String name;
  private int number;

  public Room(String id, String name, int number) {
    this.id = id;
    this.name = name;
    this.number = number;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }
}
