package org.openhab.binding.rollease.internal;

import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.rollease.Utils;
import org.openhab.binding.rollease.handler.RollerHandler;
import org.osgi.service.component.annotations.Component;

import com.nahuellofeudo.rolleasecontroller.model.Roller;

/**
 * Implements the service that injects rollers pulled from the hub into OpenHab.
 * This saves time by not requiring users to manually register their rollers.
 * 
 * @author Nahuel Lofeudo
 *
 */
@Component(service = DiscoveryService.class, immediate = true, configurationPid = "org.openhab.binding.rollease.discovery")
public class RollerDiscoveryService extends AbstractDiscoveryService {

    public RollerDiscoveryService() {
        super(1000);
    }

    public RollerDiscoveryService(int timeout) throws IllegalArgumentException {
        super(timeout);
    }

    public RollerDiscoveryService(Set<ThingTypeUID> supportedThingTypes, int timeout,
            boolean backgroundDiscoveryEnabledByDefault) throws IllegalArgumentException {
        super(supportedThingTypes, timeout, backgroundDiscoveryEnabledByDefault);
    }

    public RollerDiscoveryService(Set<ThingTypeUID> supportedThingTypes, int timeout) throws IllegalArgumentException {
        super(supportedThingTypes, timeout);
    }

    @Override
    protected void startScan() {
    }

    public void addRoller(Roller newRoller, Bridge bridge) {
        ThingUID thingUID = Utils.thingUID(newRoller.getId());
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withBridge(bridge.getUID())
                .withLabel(newRoller.getName()).withThingType(RollerHandler.THING_TYPE_UID).build();
        this.thingDiscovered(discoveryResult);
    }

}
