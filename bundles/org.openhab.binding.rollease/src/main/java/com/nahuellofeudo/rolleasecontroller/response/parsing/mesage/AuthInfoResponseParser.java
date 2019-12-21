package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.model.Hub;

/**
 * Parse authentication info from hub
 *
 * @author Nahuel Lofeudo
 *
 */
public class AuthInfoResponseParser extends BaseMessageParser {
    Logger logger = LoggerFactory.getLogger(AuthInfoResponseParser.class);

    public AuthInfoResponseParser(Hub response) {
        super(response);
    }

    @Override
    public Integer[] getSignature() {
        return new Integer[] { 0x08, 0x00 };
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {
        logger.info("Parsing Auth Info...");

        hub.setUsername(this.toDynamicString(bytes, 16));
        hub.setPassword(this.toDynamicString(bytes, 16));
    }
}
