package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseMessageParser implements MessageParser<Object> {
  protected Logger logger;
  protected Hub response;
  protected LowLevelIO llio;

  public BaseMessageParser(Hub response, LowLevelIO llio) {
    this.response = response;
    this.llio = llio;
    this.logger = logger = LoggerFactory.getLogger(this.getClass());
  }
}
