package com.nahuellofeudo.rolleasecontroller.response;

/**
 * The response to the Connect operation
 * 
 * @author Nahuel Lofeudo
 *
 */
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
