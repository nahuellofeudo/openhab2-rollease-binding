package com.nahuellofeudo.rolleasecontroller.response.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;

public class HeaderParser {
    Logger logger = LoggerFactory.getLogger(HeaderParser.class);
    List<MessageParser> parsers;
    MessageParser defaultParser;
    LowLevelIO llio;

    public HeaderParser(LowLevelIO llio) {
        parsers = new ArrayList<>(20);
        this.llio = llio;
    }

    public void registerMessageParser(MessageParser parser) throws Exception {
        this.parsers.add(parser);
    }

    /**
     * Reads a series of bytes until a header matches
     *
     * @return the appropriate messageParser
     */
    public void parseNext() throws IOException {
        Integer messageType[] = new Integer[4];
        boolean hadExtendedHeader = false;

        // Header (version?)
        llio.readAndAssert(0x00, 0x00, 0x00, 0x03);

        messageType[0] = llio.readByte();
        if (messageType[0] > 127) {
            // We have an extended header. Ignore it
            messageType[0] = llio.readByte();
            hadExtendedHeader = true;
        }
        messageType[1] = llio.readByte();
        messageType[2] = llio.readByte();
        messageType[3] = llio.readByte();

        switch (messageType[0]) {
            case 0x03:
                switch (messageType[3]) {
                    case 0x16:
                        logger.info("Received ping response.");
                        break;
                    case 0x91:
                        if (hadExtendedHeader) {
                            this.loadAndParse();
                        } else {
                            logger.info("Received empty message.");
                            break;
                        }
                    default:
                        logger.warn("Received unknown header: [" + Integer.toString(16) + ", " + Integer.toString(16)
                                + ", " + Integer.toString(16) + ", " + Integer.toString(16) + "]");
                }
                break;
            default:
                this.loadAndParse();
        }
    }

    /**
     * Read header and length, load message into memory
     * then identify correct parser and delegate the parsing into it
     *
     * @throws IOException
     */
    private void loadAndParse() throws IOException {
        Integer bytes[];

        llio.readAndAssertHeader();
        int length = llio.readLSBShort();
        bytes = new Integer[length];

        for (int x = 0; x < length; x++) {
            bytes[x] = llio.readByte();
        }

        Integer[] type = new Integer[] { bytes[0], bytes[1] };
        MessageParser parser = this.getParserFor(type);
        if (parser != null) {
            parser.parse(bytes);
        } else {
            logger.error(String.format("Parser not found for type [%2xd, %2xd].", type[0], type[1]));
            this.defaultParser.parse(bytes);
        }
    }

    private MessageParser getParserFor(Integer[] type) {
        for (MessageParser candidate : this.parsers) {
            if (candidate.canParse(type)) {
                return candidate;
            }
        }
        return null;
    }

    public MessageParser getDefaultParser() {
        return defaultParser;
    }

    public void setDefaultParser(MessageParser defaultParser) {
        this.defaultParser = defaultParser;
    }
}
