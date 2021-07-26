/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.rollease;

import org.openhab.core.thing.ThingUID;

/**
 * This class contains some utility methods used in multiple places in the rest of the code.
 *
 * @author Nahuel Lofeudo
 *
 */
public class Utils {
    public static ThingUID thingUID(ThingUID bridgeUID, long id) {
        return new ThingUID(Constants.BINDING_ID, Constants.ROLLER, bridgeUID.getId(), String.format("%012x", id));
    }

    public static long id(ThingUID uid) {
        return Long.parseLong(uid.getId(), 16);
    }
}
