package com.nahuellofeudo.rolleasecontroller.listener;

import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

public interface HubRollersListener {
    /**
     * Notifies an listener of a roller being added to the hub.
     * Called AFTER the roller has been registered with the Controller structure
     * @param newRoller
     */
    public void rollerAdded(Roller newRoller, Hub hub);

    /**
     * Notifies an listener that a roller has been removed from the hub.
     * Called BEFORE the roller is removed from the Controller structure
     * @param removedRoller
     */
    public void rollerRemoved(Roller removedRoller, Hub hub);
}
