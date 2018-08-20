package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

/**
 * Parse one of the two types of notifications from the hub when a roller changes position
 * 
 * @author Nahuel Lofeudo
 *
 */
public class AdjustPositionResponse1Parser extends BaseMessageParser {
    Logger logger = LoggerFactory.getLogger(AdjustPositionResponse1Parser.class);

    public AdjustPositionResponse1Parser(Hub response) {
        super(response);
    }

    @Override
    public Integer[] getSignature() {
        return new Integer[] { 0x2b, 0x01 };
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {
        logger.info("Parsing Adjust Position Response type 1");
        long id = this.toLongNumber(bytes, 14, 6);

        int percentClosed = bytes[32];

        int unknown1 = bytes[33];
        int unknown4 = bytes[35];
        int unknown3 = bytes[36];
        int unknown2 = bytes[37];

        Roller roller = this.hub.getRollers().get(id);
        if (roller == null) {
            logger.error(String.format("Received update for unknown roller %016x. Ignoring", id));
            return;
        }

        logger.info(String.format(
                "Received Adjust Position Response #1 for roller: %016x. Position %d%%. Unknown1: 0x%02x. Unknown2: 0x%02x. Unknown3: 0x%02x. Unknown4: 0x%02x",
                id, percentClosed, unknown1, unknown2, unknown3, unknown4));
        roller.setPercentClosed(percentClosed);
        return;
    }
}
