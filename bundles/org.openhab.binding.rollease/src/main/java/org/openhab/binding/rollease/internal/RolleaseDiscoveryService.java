package org.openhab.binding.rollease.internal;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.Bridge;

import com.nahuellofeudo.rolleasecontroller.model.Roller;

public interface RolleaseDiscoveryService extends DiscoveryService {
    void addRoller(@NonNull Roller newRoller, @NonNull Bridge thing);
}
