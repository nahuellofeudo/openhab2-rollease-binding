package com.nahuellofeudo.rolleasecontroller.listener;

import com.nahuellofeudo.rolleasecontroller.model.Roller;

public interface RollerStateListener {
    /**
     * Notifies a listener that a roller's position changed
     *
     * @param roller      the Roller object that represents this roller
     * @param oldPosition the old position as a percentage closed (0 == all open, 100 == all closed)
     */
    public void rollerPositionChanged(Roller roller, int oldPosition);

    /**
     * Notifies a listener that a roller's battery level changed
     *
     * @param roller   the Roller object that represents this roller
     * @param oldValue the old battery level
     */
    public void rollerBatteryChanged(Roller roller, int oldBatteryLevel);
}
