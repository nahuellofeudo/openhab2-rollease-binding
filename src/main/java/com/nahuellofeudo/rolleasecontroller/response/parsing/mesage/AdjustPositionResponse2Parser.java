package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

public class AdjustPositionResponse2Parser extends BaseMessageParser {
    Logger logger = LoggerFactory.getLogger(AdjustPositionResponse2Parser.class);

    public AdjustPositionResponse2Parser(Hub response) {
        super(response);
    }

    @Override
    public Integer[] getSignature() {
        return new Integer[] { 0x23, 0x01 };
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {
        logger.info("Parsing Adjust Position Response type 2");
        long id = this.toLongNumber(bytes, 14, 6);

        int percentClosed = bytes[30];
        // int unknown1 = bytes[31];

        Roller roller = this.hub.getRollers().get(id);
        if (roller == null) {
            logger.error(String.format("Received update for unknown roller %016x. Ignoring", id));
            return;
        }

        roller.setPercentClosed(percentClosed);
        logger.info(String.format("Received Adjust Position Response #2 for roller: %016x. Position %d%%.", id,
                percentClosed));
        return;
    }

}
