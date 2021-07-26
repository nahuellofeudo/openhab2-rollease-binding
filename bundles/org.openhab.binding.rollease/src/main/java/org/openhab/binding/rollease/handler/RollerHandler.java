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
package org.openhab.binding.rollease.handler;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.rollease.Constants;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link RollerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Nahuel Lofeudo - Initial contribution
 */
public class RollerHandler extends BaseThingHandler {
    public static final ThingTypeUID THING_TYPE_UID = new ThingTypeUID(Constants.BINDING_ID, Constants.ROLLER);

    private final Logger logger = LoggerFactory.getLogger(RollerHandler.class);
    private final CancelableScheduledExecutorService executor;

    public RollerHandler(Thing thing) {
        super(thing);
        this.executor = new CancelableScheduledExecutorService();
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, Command command) {
        logger.debug("Received command " + command.toFullString() + " for channelUID " + channelUID.toString());
        if (command.getClass().equals(RefreshType.class)) {
            return;
        }
        if (command.getClass().equals(DecimalType.class)) {
            DecimalType position = (DecimalType) command;
            String rollerId = channelUID.getThingUID().getId();
            this.executor.schedule(rollerId,
                    new PositionUpdateTask(this.getHubHandler(), rollerId, position, this.executor));
        }
    }

    private HubHandler getHubHandler() {
        Bridge bridge = this.getBridge();
        if (bridge == null) {
            logger.error("Bridge cannot be null for thing " + this.getThing().toString());
            return null;
        } else {
            HubHandler hubHandler = ((HubHandler) (bridge.getHandler()));
            return hubHandler;
        }
    }
}
