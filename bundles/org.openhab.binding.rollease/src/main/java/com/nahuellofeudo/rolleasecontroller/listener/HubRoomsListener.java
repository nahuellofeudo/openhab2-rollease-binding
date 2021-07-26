package com.nahuellofeudo.rolleasecontroller.listener;

import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Room;

public interface HubRoomsListener {
    /**
     * Notifies an listener of a room being added to the hub.
     * Called AFTER the room has been registered with the Controller structure
     * 
     * @param newRoom
     */
    public void roomAdded(Room newRoom, Hub hub);

    /**
     * Notifies an listener that the room has been removed from the hub.
     * Called BEFORE the room is removed from the Controller structure
     * 
     * @param removedRoom
     */
    public void roomRemoved(Room removedRoom, Hub hub);
}
