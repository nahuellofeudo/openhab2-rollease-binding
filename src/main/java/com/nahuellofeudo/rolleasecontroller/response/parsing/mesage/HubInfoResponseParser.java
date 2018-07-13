package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HubInfoResponseParser implements com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser<Hub> {
  Logger logger = LoggerFactory.getLogger(HubInfoResponseParser.class);
  Hub response;
  LowLevelIO llio;

  public HubInfoResponseParser(Hub response, LowLevelIO llio) {
    this.response = response;
    this.llio = llio;
  }

  @Override
  public Hub parse() throws IOException {
    logger.info("Parsing Controller Info...");
    // ---------------------------- FIRST FRAME: FW version --------------------
    llio.readAndAssertHeader();
    int length = llio.readShort();
    llio.readAndAssert(0x16, 0x00);

    int unknown1 = llio.readByte();
    int flags = llio.readByte();

    llio.readAndAssert(
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00);

    // Firmware version
    llio.readAndAssert(0x53, 0x02);
    String firmareVersion = llio.readLSBString();

    // Unknown
    llio.readAndAssert(0x54, 0x02);
    llio.readLSBString();

    // Unknown
    llio.readAndAssert(0x55, 0x02);
    llio.readLSBString();

    // Unknown
    llio.readAndAssert(0x56, 0x02, 0x00, 0x00);
    llio.readAndAssert(0x57, 0x02, 0x00, 0x00);

    llio.readAndAssert(0xff, 0x6c);

    // Build response
    response.setFirmwareVersion(firmareVersion);
    return response;
  }
}
