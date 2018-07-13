/**
 * Copyright (c) 2014,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.rollease.handler;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.rollease.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link RollerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Nahuel Lofeudo - Initial contribution
 */
public class RollerHandler extends BaseThingHandler {
    public RollerHandler(Thing thing) {
        super(thing);
    }

    private final Logger logger = LoggerFactory.getLogger(RollerHandler.class);
    public static final ThingTypeUID THING_TYPE_UID = new ThingTypeUID(Constants.BINDING_ID, Constants.ROLLER);

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, Command command) {
        logger.debug("Received command " + command.toFullString() + " for channelUID " + channelUID.toString());
        if (command.getClass().equals(RefreshType.class)) {
            return;
        }
        if (command.getClass().equals(DecimalType.class)) {
            DecimalType position = (DecimalType) command;
            ((HubHandler) (this.getBridge().getHandler())).setPosition(channelUID.getThingUID().getId(), position);
        }

    }

    public static ThingUID thingUID(long id) {
        return new ThingUID(Constants.BINDING_ID, Constants.ROLLER, String.format("%012x", id));
    }

    public static long id(ThingUID uid) {
        return Long.parseLong(uid.getId(), 16);
    }

}
