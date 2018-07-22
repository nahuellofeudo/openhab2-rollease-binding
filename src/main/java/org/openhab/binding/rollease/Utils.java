package org.openhab.binding.rollease;

import org.eclipse.smarthome.core.thing.ThingUID;

public class Utils {
    public static ThingUID thingUID(long id) {
        return new ThingUID(Constants.BINDING_ID, Constants.ROLLER, String.format("%012x", id));
    }

    public static long id(ThingUID uid) {
        return Long.parseLong(uid.getId(), 16);
    }
}
