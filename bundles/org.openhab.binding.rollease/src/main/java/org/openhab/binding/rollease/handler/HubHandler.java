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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.rollease.Constants;
import org.openhab.binding.rollease.Utils;
import org.openhab.binding.rollease.internal.RolleaseDiscoveryService;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.Controller;
import com.nahuellofeudo.rolleasecontroller.listener.HubRollersListener;
import com.nahuellofeudo.rolleasecontroller.listener.HubStatusListener;
import com.nahuellofeudo.rolleasecontroller.listener.RollerStateListener;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

public class HubHandler extends BaseBridgeHandler
        implements HubStatusListener, HubRollersListener, RollerStateListener {
    private static final Logger logger = LoggerFactory.getLogger(HubHandler.class);
    public static final ThingTypeUID THING_TYPE_UID = new ThingTypeUID(Constants.BINDING_ID, Constants.HUB);

    private Hub hub;
    private Controller hubController;
    private RolleaseDiscoveryService discoveryService;

    public HubHandler(Bridge bridge, RolleaseDiscoveryService discoveryService) throws Exception {
        super(bridge);
        this.discoveryService = discoveryService;

        String hostname = (String) bridge.getConfiguration().get("Hostname");
        if (hostname == null || hostname.isEmpty()) {
            throw new Exception("Tried to create hub with invalid host name");
        }

        // Create an instance of this hub
        logger.debug("Creating hub Thing for host " + hostname);
        this.hub = new Hub();
        this.hub.addHubStatusListener(this);
        this.hub.addRollerListener(this);
        this.hubController = new Controller(hub, hostname);
        addHub(hub);
    }

    @Override
    public void initialize() {
        hubController.connectAndRun();
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, Command command) {
        // Nothing to see here, folks.
    }

    @Override
    public void rollerAdded(Roller newRoller, Hub hub) {
        logger.debug(String.format("Received notification of new roller. ID: %012x.", newRoller.getId()));
        this.discoveryService.addRoller(newRoller, this.getThing());
        newRoller.addStateListener(this);
        this.rollerPositionChanged(newRoller, 0);
    }

    @Override
    public void rollerRemoved(Roller removedRoller, Hub hub) {
    }

    @Override
    public void hubStatusChanged(HubStatus currentStatus, Hub hub) {
        logger.debug("Hub status changed to " + currentStatus.toString());
        if (currentStatus.equals(HubStatus.ONLINE)) {
        }
    }

    public void setOpenPosition(String id, DecimalType position) {

        Roller roller = this.getRollerFromId(id);
        try {
            hubController.adjustOpenPosition(roller, position.intValue());
        } catch (Exception e) {
            logger.error("Could not adjust open position of roller " + id, e);
        }
    }

    public void setPosition(String id, DecimalType position) {

        Roller roller = this.getRollerFromId(id);
        try {
            hubController.adjustPosition(roller, position.intValue());
        } catch (Exception e) {
            logger.error("Could not adjust closed position of roller " + id, e);
        }
    }

    public void registerRollerListener(String id, RollerStateListener listener) {
        Roller roller = this.getRollerFromId(id);
        roller.addStateListener(listener);
    }

    public Roller getRollerFromId(String id) {
        return this.hub.getRollerById(Long.parseLong(id, 16));
    }

    /*
     * We keep a static list of hubs because the ThingUID is the only data that is kept by OpenHab to link
     * the OpenHAB side with the model.
     */
    private static List<Hub> hubs = new LinkedList<Hub>();

    private static synchronized void addHub(Hub hub) {
        hubs.add(hub);
    }

    public static synchronized Roller getRollerForId(Long id) {
        for (Hub hub : hubs) {
            Roller roller = hub.getRollerById(id);
            if (roller != null) {
                return roller;
            }
        }
        logger.error("Could not find any hubs containing roller ID " + id.toString());
        return null;
    }

    @Override
    public void rollerPositionChanged(Roller roller, int oldPosition) {
        ChannelUID positionChannel = new ChannelUID(Utils.thingUID(roller.getId()), Constants.PERCENTAGE_OPEN);
        updateState(positionChannel, new DecimalType(roller.getPercentOpen()));
    }

    @Override
    public void rollerBatteryChanged(Roller roller, int oldBatteryLevel) {
        ChannelUID positionChannel = new ChannelUID(Utils.thingUID(roller.getId()), Constants.BATTERY_LEVEL);
        updateState(positionChannel, new DecimalType(roller.getBatteryLevel()));
    }
}
