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
package org.openhab.binding.rollease.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.rollease.handler.HubHandler;
import org.openhab.binding.rollease.handler.RollerHandler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link RolleaseHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Nahuel Lofeudo - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "org.openhab.binding.rollease")
public class RolleaseHandlerFactory extends BaseThingHandlerFactory {
    private final Logger logger = LoggerFactory.getLogger(RolleaseHandlerFactory.class);
    RollerDiscoveryService discoveryService;

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS;

    static {
        SUPPORTED_THING_TYPES_UIDS = new HashSet<>();
        SUPPORTED_THING_TYPES_UIDS.add(HubHandler.THING_TYPE_UID);
        SUPPORTED_THING_TYPES_UIDS.add(RollerHandler.THING_TYPE_UID);
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(@Nullable Thing thing) {
        if (thing == null) {
            logger.error("Rollease createHandler called with Null parameter. Returning null.");
            return null;
        }
        logger.info("Creating handler for thing " + thing.getLabel() + " (" + thing.getThingTypeUID() + ")");

        if (thing.getThingTypeUID().equals(HubHandler.THING_TYPE_UID)) {
            try {
                return new HubHandler((Bridge) thing, this.discoveryService);
            } catch (Exception e) {
                logger.error("Error creating Hub Handler", e);
            }
        } else if (thing.getThingTypeUID().equals(RollerHandler.THING_TYPE_UID)) {
            return new RollerHandler(thing);
        } else {
            logger.error("Rollease createHandler called with unknown thing " + thing.toString() + " type "
                    + thing.getThingTypeUID());
        }
        return null;
    }

    @Reference
    public void setDiscoveryService(DiscoveryService discoveryService) {
        this.discoveryService = (RollerDiscoveryService) discoveryService;
    }

    public void unsetDiscoveryService(DiscoveryService discoveryService) {
        this.discoveryService = null;
    }

}
