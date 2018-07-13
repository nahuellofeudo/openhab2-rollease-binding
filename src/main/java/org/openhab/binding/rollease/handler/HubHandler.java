package org.openhab.binding.rollease.handler;

import java.util.concurrent.Semaphore;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.rollease.Constants;
import org.openhab.binding.rollease.internal.RollerDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.Controller;
import com.nahuellofeudo.rolleasecontroller.listener.HubRollersListener;
import com.nahuellofeudo.rolleasecontroller.listener.HubStatusListener;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

public class HubHandler extends BaseBridgeHandler implements HubStatusListener, HubRollersListener {
    Hub hub;
    Controller hubController;
    Semaphore semaphore = new Semaphore(0);
    RollerDiscoveryService discoveryService;

    public HubHandler(Bridge bridge, RollerDiscoveryService discoveryService) throws Exception {
        super(bridge);
        this.discoveryService = discoveryService;

        String hostname = (String) bridge.getConfiguration().get("Hostname");
        if (hostname == null || hostname.isEmpty()) {
            throw new Exception("Tried to create hub with invalid host name");
        }

        // Create an instance of this hub
        this.hub = new Hub();
        this.hub.addHubStatusListener(this);
        this.hub.addRollerListener(this);
        this.hubController = new Controller(hub, hostname);
        hubController.connectAndRun();
        semaphore.acquire();
    }

    public static final ThingTypeUID THING_TYPE_UID = new ThingTypeUID(Constants.BINDING_ID, Constants.HUB);

    private final Logger logger = LoggerFactory.getLogger(RollerHandler.class);

    @Override
    public void handleCommand(@NonNull ChannelUID channelUID, Command command) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rollerAdded(Roller newRoller, Hub hub) {
        logger.info(String.format("Received notification of new roller. ID: %012x.", newRoller.getId()));
        this.discoveryService.addRoller(newRoller, this.getThing());
    }

    @Override
    public void rollerRemoved(Roller removedRoller, Hub hub) {
    }

    @Override
    public void hubStatusChanged(HubStatus currentStatus, Hub hub) {
        logger.info("Hub status changed to " + currentStatus.toString());
        if (currentStatus.equals(HubStatus.ONLINE)) {
            semaphore.release();
        }
    }

    public void setPosition(String id, DecimalType position) {

        Roller roller = this.hub.getRollerById(Long.parseLong(id, 16));
        try {
            hubController.adjustPosition(roller, position.intValue());
        } catch (Exception e) {
            logger.error("Could not adjust position of roller " + id, e);
        }
    }
}
