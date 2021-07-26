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
package org.openhab.binding.rollease.internal;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.rollease.Utils;
import org.openhab.binding.rollease.handler.RollerHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.model.Roller;

/**
 * Implements the service that injects rollers pulled from the hub into OpenHab.
 * This saves time by not requiring users to manually register their rollers.
 * 
 * @author Nahuel Lofeudo
 *
 */
@Component(immediate = true)
@NonNullByDefault
public class RollerDiscoveryService extends AbstractDiscoveryService
        implements DiscoveryService, RolleaseDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(RollerDiscoveryService.class);

    public RollerDiscoveryService() {
        super(1000);
        logger.debug(" ----- Starting rollease discovery service (default timeout)");
    }

    public RollerDiscoveryService(int timeout) throws IllegalArgumentException {
        super(timeout);
        logger.debug(String.format("Starting rollease discovery service (Timeout: %d ms)", timeout));
    }

    public RollerDiscoveryService(Set<ThingTypeUID> supportedThingTypes, int timeout,
            boolean backgroundDiscoveryEnabledByDefault) throws IllegalArgumentException {
        super(supportedThingTypes, timeout, backgroundDiscoveryEnabledByDefault);
        logger.debug(String.format("Starting rollease discovery service (Timeout: %d ms). Supported thing types: %s",
                timeout, supportedThingTypes.toArray().toString()));
    }

    public RollerDiscoveryService(Set<ThingTypeUID> supportedThingTypes, int timeout) throws IllegalArgumentException {
        super(supportedThingTypes, timeout);
        logger.debug(String.format("Starting rollease discovery service (Timeout: %d ms). Supported thing types: %s",
                timeout, supportedThingTypes.toArray().toString()));
    }

    @Override
    protected void startScan() {
        logger.debug("Dummy scan triggered");
    }

    public void addRoller(@NonNull Roller newRoller, @NonNull Bridge bridge) {
        ThingUID thingUID = Utils.thingUID(bridge.getUID(), newRoller.getId());
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withBridge(bridge.getUID())
                .withLabel(newRoller.getName()).withThingType(RollerHandler.THING_TYPE_UID).build();
        logger.debug("Notifying OpenHab of new roller.");
        this.thingDiscovered(discoveryResult);
    }
}
