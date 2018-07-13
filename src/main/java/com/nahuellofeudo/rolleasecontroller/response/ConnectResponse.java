package com.nahuellofeudo.rolleasecontroller.response;

public class ConnectResponse {
  private String identifier;

  public ConnectResponse(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
}
