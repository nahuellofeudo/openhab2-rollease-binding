package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Authentication info
 */
public class AuthInfoResponseParser implements com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser<Object> {
  Logger logger = LoggerFactory.getLogger(AuthInfoResponseParser.class);
  Hub response;
  LowLevelIO llio;

  public AuthInfoResponseParser(Hub response, LowLevelIO llio) {
    this.response = response;
    this.llio = llio;
  }

  @Override
  public Object parse() throws IOException {
    logger.info("Parsing Auth Info...");
    // ---------------------------- Auth info --------------------
    llio.readAndAssertHeader();
    int length = llio.readShort();
    llio.readLSBString();                      // Unknown

    llio.readAndAssert(                        // Unknown
            0x10, 0x00, 0x01, 0x00,
            0x01);

    llio.readAndAssert(0x12, 0x00);            // Username
    String username = llio.readLSBString();

    llio.readAndAssert(0x13, 0x00);            // Password
    String password = llio.readLSBString();

    llio.readAndAssert(0x14, 0x00, 0x01, 0x00, 0x01); // Unknown
    llio.readAndAssert(0xff, 0x95);

    response.setUsername(username);
    response.setPassword(password);

    return null;
  }
}
