package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

/**
 * The most important message parser.
 * Parses the list of rollers managed by the hub and their positions (0% to 100% closed)
 *
 * @author Nahuel Lofeudo
 *
 */
public class RollerListParser extends BaseMessageParser {
    private Logger logger = LoggerFactory.getLogger(RollerListParser.class);

    public RollerListParser(Hub hub) {
        super(hub);
    }

    @Override
    public Integer[] getSignature() {
        return new Integer[] { 0x21, 0x01 };
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {
        logger.info("Parsing Roller List...");
        int ptr = 0x0e;

        int numberOfRollers = bytes[ptr];
        ptr++;

        for (int roller = 0; roller < numberOfRollers; roller++) {

            // Parse Roller ID
            ptr += 4;
            long id = this.toLongNumber(bytes, ptr, 6);
            ptr += 6;

            // Parse room ID
            ptr += 2;
            String roomId = this.toDynamicString(bytes, ptr);
            ptr += 2 + roomId.length();

            // Parse roller name
            ptr += 7;
            String name = this.toDynamicString(bytes, ptr);
            ptr += 2 + name.length();

            // Parse Battery level
            ptr += 0x0a;
            int batteryLevel = bytes[ptr];
            ptr += 1;

            // Parse Percent Closed
            ptr += 5;
            int percentClosed = bytes[ptr];
            ptr += 2;

            if (hub.getRollerById(id) == null) {
                Roller newRoller = new Roller(id, name, roomId, batteryLevel, percentClosed);
                logger.info(String.format("New roller: %s", newRoller.toString()));
                this.hub.addRoller(newRoller);
            } else {
                Roller existingRoller = hub.getRollerById(id);
                logger.info(String.format("Updating existing roller %016x: Closed: %d%% | Battery?: %d%%", id,
                        percentClosed, batteryLevel));
                existingRoller.setPercentOpen(100 - percentClosed);
                existingRoller.setBatteryLevel(batteryLevel);
            }
        }
        return;
    }
}
