package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HubInfoEndParser implements com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser<Object> {
  Logger logger = LoggerFactory.getLogger(HubInfoEndParser.class);
  Hub response;
  LowLevelIO llio;

  public HubInfoEndParser(Hub response, LowLevelIO llio) {
    this.response = response;
    this.llio = llio;
  }

  @Override
  public Object parse() throws IOException {
    logger.info("Parsing Controller Info End...");
    llio.readAndAssertHeader();
    int length = llio.readLSBShort() - 1;
    llio.readByte();
    for (; length > 0; length--) {
      llio.readByte();
    }
    return null;
  }
}
