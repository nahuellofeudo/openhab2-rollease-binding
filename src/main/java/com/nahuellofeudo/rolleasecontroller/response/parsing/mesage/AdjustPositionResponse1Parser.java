package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;

public class AdjustPositionResponse1Parser extends BaseMessageParser {
    public AdjustPositionResponse1Parser(Hub response, LowLevelIO llio) {
        super(response, llio);
    }

    @Override
    public Object parse() throws IOException {
        logger.info("Parsing Adjust Position Response type 1");
        llio.readAndAssertHeader();
        int length = llio.readShort();
        llio.readAndAssert(0x2b); // Type
        llio.readAndAssert(0x01); // ??
        llio.readShort(); // Sequence. Ignore
        llio.readAndAssert(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x06, 0x00);
        long id = llio.readLSBNumber(6);

        llio.readAndAssert(0x41, 0x02, 0x01, 0x00, 0x0e, 0x42, 0x02, 0x01, 0x00, 0x04, 0x43, 0x02, 0x0e, 0x00);
        int percentClosed = llio.readByte();
        int unknown1 = llio.readByte();
        llio.readAndAssert(0x64);
        int unknown4 = llio.readByte();
        int unknown3 = llio.readByte();
        int unknown2 = llio.readByte();
        llio.readAndAssert(0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x09, 0x44);
        llio.readByte(); // 0xff
        llio.readByte(); // CRC. Ignore

        Roller roller = this.response.getRollers().get(id);
        if (roller == null) {
            logger.error(String.format("Received update for unknown roller %016x. Ignoring", id));
            return null;
        }

        logger.info(String.format(
                "Received Adjust Position Response #1 for roller: %016x. Position %d%%. Unknown1: 0x%02x. Unknown2: 0x%02x. Unknown3: 0x%02x. Unknown4: 0x%02x",
                id, percentClosed, unknown1, unknown2, unknown3, unknown4));
        roller.setPercentClosed(percentClosed);
        return null;
    }
}
