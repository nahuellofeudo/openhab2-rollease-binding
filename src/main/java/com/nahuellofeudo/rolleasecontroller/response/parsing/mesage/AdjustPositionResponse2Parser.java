package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

public class AdjustPositionResponse2Parser extends BaseMessageParser {
    public AdjustPositionResponse2Parser(Hub response, LowLevelIO llio) {
        super(response, llio);
    }

    @Override
    public Object parse() throws IOException {
        logger.info("Parsing Adjust Position Response type 2");
        llio.readAndAssertHeader();
        int length = llio.readShort();
        llio.readAndAssert(0x23); // Type
        llio.readAndAssert(0x01); // ??
        llio.readShort(); // Sequence. Ignore
        llio.readAndAssert(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x06, 0x00);
        long id = llio.readLSBNumber(6);

        llio.readAndAssert(0x03, 0x01, 0x01, 0x00, 0x19, 0x04, 0x01, 0x03, 0x00, 0x03);
        int percentClosed = llio.readByte();
        int unknown1 = llio.readByte();
        llio.readByte(); // 0xff
        llio.readByte(); // CRC. Ignore

        Roller roller = this.response.getRollers().get(id);
        if (roller == null) {
            logger.error(String.format("Received update for unknown roller %016x. Ignoring", id));
            return null;
        }

        roller.setPercentClosed(percentClosed);
        logger.info(String.format("Received Adjust Position Response #2 for roller: %016x. Position %d%%.", id,
                percentClosed));
        return null;
    }
}
