package org.openhab.binding.rollease.internal;

import com.nahuellofeudo.rolleasecontroller.model.Roller;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;

public interface RolleaseDiscoveryService extends DiscoveryService {
  void addRoller(@NonNull Roller newRoller, @NonNull Bridge thing);
}
