package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser;

public class UnknownMessageParser implements MessageParser {
    Logger logger = LoggerFactory.getLogger(UnknownMessageParser.class);

    @Override
    public Integer[] getSignature() {
        return null;
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {
        logger.info("Parsing unknown message...");
        int length = bytes.length;
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("Unknown packet received: [%02x, %02x", length >> 8, length & 0xff));

        for (int x = 0; x < length; x++) {
            sb.append(String.format(", %02x", bytes[x] & 0xff));
        }

        sb.append("]");

        logger.warn(sb.toString());
    }

    @Override
    public boolean canParse(Integer[] type) {
        return true;
    }
}
