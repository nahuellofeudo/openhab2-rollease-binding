package com.nahuellofeudo.rolleasecontroller.listener;

import com.nahuellofeudo.rolleasecontroller.model.Roller;

public interface RollerStateListener {
    /**
     * Notifies a listener that a roller's position changed
     * @param roller the Roller object that represents this roller
     * @param oldPosition the old position as a percentage closed (0 == all open, 100 == all closed)
     */
    public void rollerPositionChanged(Roller roller, int oldPosition);

    // Not implemented at this time
    //public void rollerBatteryChanged(Roller roller, int oldBatteryLevel);
}
