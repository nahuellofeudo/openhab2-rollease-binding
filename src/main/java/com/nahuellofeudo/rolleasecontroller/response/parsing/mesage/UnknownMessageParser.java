package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UnknownMessageParser implements MessageParser<Object> {
  Logger logger = LoggerFactory.getLogger(UnknownMessageParser.class);
  LowLevelIO llio;

  public UnknownMessageParser(LowLevelIO llio) {
    this.llio = llio;
  }

  @Override
  public Object parse() throws IOException {
    logger.info("Parsing unknown message...");
    llio.readAndAssertHeader();
    int length = llio.readLSBShort();
    StringBuffer sb = new StringBuffer();
    sb.append(String.format("Unknown packet received: [%02x, %02x", length >> 8, length & 0xff));

    for (; length > 0; length--) {
      sb.append(String.format(", %02x", llio.readByte()));
    }

    sb.append("]");

    logger.warn(sb.toString());
    return null;
  }

  private void appendByte(StringBuffer sb, Integer i) {
    sb.append(", ");
    sb.append(String.format(", %2x", i));
  }
}
