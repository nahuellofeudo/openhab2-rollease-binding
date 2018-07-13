package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PingResponseParser implements MessageParser<Object> {
  Logger logger = LoggerFactory.getLogger(PingResponseParser.class);
  LowLevelIO llio;

  public PingResponseParser(LowLevelIO llio) {
    this.llio = llio;
  }

  @Override
  public Object parse() throws IOException {
    logger.info("Parsing Ping response...");
    return null;
  }
}