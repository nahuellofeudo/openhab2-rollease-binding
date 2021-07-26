package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser;

/**
 * Implements a null response parser. Just consume the input
 */
public class NullResponseParser implements MessageParser {
    Logger logger = LoggerFactory.getLogger(NullResponseParser.class);

    @Override
    public Integer[] getSignature() {
        return null;
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {
        logger.info("Parsing Null Response...");
    }

    @Override
    public boolean canParse(Integer[] type) {
        return true;
    }
}
