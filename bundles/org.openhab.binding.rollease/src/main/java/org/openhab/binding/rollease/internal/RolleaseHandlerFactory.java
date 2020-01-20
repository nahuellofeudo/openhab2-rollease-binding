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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
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
@Component(immediate = true)
@NonNullByDefault
public class RolleaseHandlerFactory extends BaseThingHandlerFactory implements ThingHandlerFactory{
    private final Logger logger = LoggerFactory.getLogger(RolleaseHandlerFactory.class);
    private @Nullable RolleaseDiscoveryService discoveryService;

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS =
        (Set<ThingTypeUID>) Collections.unmodifiableSet(
            Stream.of(
                HubHandler.THING_TYPE_UID,
                RollerHandler.THING_TYPE_UID
            ).collect(Collectors.toSet())
        );

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        logger.info("Rollease: SupportsThingType " + thingTypeUID.toString());
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    public RolleaseHandlerFactory() {
        super();
        logger.info(" ----- Created RolleaseHandlerFactory.");
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
    public void setDiscoveryService(RolleaseDiscoveryService discoveryService) {
        this.discoveryService = (RollerDiscoveryService) discoveryService;
    }

    public void unsetDiscoveryService(RolleaseDiscoveryService discoveryService) {
        this.discoveryService = null;
    }

}
