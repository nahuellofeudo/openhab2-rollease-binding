package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.model.Hub;

public class HubInfoResponseParser extends BaseMessageParser {
    Logger logger = LoggerFactory.getLogger(HubInfoResponseParser.class);

    public HubInfoResponseParser(Hub response) {
        super(response);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Integer[] getSignature() {
        return new Integer[] { 0x16, 0x00 };
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {

        String firmareVersion = this.toString(bytes, 38, 8);

        // Build response
        hub.setFirmwareVersion(firmareVersion);
    }
}
