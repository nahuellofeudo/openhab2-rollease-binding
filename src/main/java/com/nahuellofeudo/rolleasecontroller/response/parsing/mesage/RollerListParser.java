package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

public class RollerListParser implements com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser<Object> {
    private Logger logger = LoggerFactory.getLogger(RollerListParser.class);
    private Hub hub;
    private LowLevelIO llio;

    public RollerListParser(Hub hub, LowLevelIO llio) {
        this.hub = hub;
        this.llio = llio;
    }

    @Override
    public Object parse() throws IOException {
        logger.info("Parsing Roller List...");

        llio.readAndAssertHeader();
        llio.readAndAssert(0x66, 0x01, 0x21, 0x01);
        llio.readLSBShort(); // Sequence number. Ignore

        llio.readAndAssert(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x01, 0x00);

        int numberOfRollers = llio.readByte();

        for (; numberOfRollers > 0; numberOfRollers--) {
            llio.readAndAssert(0x01, 0x01, 0x06, 0x00);

            long id = llio.readLSBNumber(6);
            llio.readAndAssert(0x00, 0x02);
            String roomId = llio.readLSBString();
            llio.readAndAssert(0x0f, 0x00, 0x01, 0x00, 0x04, 0x0e, 0x00);
            String name = llio.readLSBString();
            llio.readAndAssert(0x02, 0x01, 0x02, 0x00, 0x31, 0x02, 0x03, 0x01, 0x01, 0x00);
            int batteryLevel = llio.readByte();
            llio.readAndAssert(0x04, 0x01, 0x03);
            int state = llio.readShort();
            int percentClosed = llio.readByte();
            int unknown2 = llio.readByte();

            if (hub.getRollerById(id) == null) {
                Roller newRoller = new Roller(id, name, roomId, batteryLevel, state, percentClosed, unknown2);
                logger.info(String.format("New roller: %s", newRoller.toString()));
                this.hub.addRoller(newRoller);
            } else {
                Roller existingRoller = hub.getRollerById(id);
                logger.info(
                        String.format("Updating existing roller %016x: Closed: %d%% | Battery?: %d%% | Unknown: %2x",
                                id, percentClosed, batteryLevel, unknown2));
                existingRoller.setPercentClosed(percentClosed);
                existingRoller.setBatteryLevel(batteryLevel);
                existingRoller.setUnknown2(unknown2);
            }
        }

        llio.readShort(); // Ignore Checksum
        return null;
    }

}
