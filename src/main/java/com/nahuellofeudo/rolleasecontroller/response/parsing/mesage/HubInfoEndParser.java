package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.model.Hub;

public class HubInfoEndParser extends BaseMessageParser {
    Logger logger = LoggerFactory.getLogger(HubInfoEndParser.class);

    public HubInfoEndParser(Hub response) {
        super(response);
    }

    @Override
    public Integer[] getSignature() {
        return new Integer[] { 0x41, 0x01 };
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {
    }
}
