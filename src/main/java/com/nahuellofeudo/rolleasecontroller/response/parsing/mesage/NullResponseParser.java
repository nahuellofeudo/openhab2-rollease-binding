package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a null response parser. Just consume the input
 */
public class NullResponseParser implements MessageParser<Object> {
  Logger logger = LoggerFactory.getLogger(NullResponseParser.class);

  @Override
  public Object parse() {
    logger.info("Parsing Null Response...");
    return null;
  }

}
