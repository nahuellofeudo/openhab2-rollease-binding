package com.nahuellofeudo.rolleasecontroller.response.parsing;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.UnknownMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.*;

public class HeaderParser {
  Logger logger = LoggerFactory.getLogger(HeaderParser.class);
  Map<List<Integer>, MessageParser> parsers;
  MessageParser defaultParser;
  LowLevelIO llio;

  public HeaderParser(LowLevelIO llio) {
    parsers = new HashMap<>();
    this.llio = llio;
  }

  public void registerMessageParser(MessageParser parser, List<Integer> signature) throws Exception {
    List<Integer> path = new LinkedList<>();

    for (Integer s : signature) {
      path.add(s);
      if (this.getParserFor(path) != null) {
        throw new Exception("The signature already exists:");
      }
    }
    this.parsers.put(signature, parser);
  }

  public void registerMessageParser(MessageParser parser, Integer... signature) throws Exception {
    registerMessageParser(parser, Arrays.asList(signature));
  }

  public void registerMessageParser(MessageParser parser, Long signature1, Integer signature2, Long signature3) throws Exception {
    List<Integer> signature = new LinkedList<>();

    signature.add((int) (signature1 >> 24) & 0xff);
    signature.add((int) (signature1 >> 16) & 0xff);
    signature.add((int) (signature1 >> 8) & 0xff);
    signature.add((int) (signature1 >> 0) & 0xff);

    signature.add(signature2);

    signature.add((int) (signature3 >> 24) & 0xff);
    signature.add((int) (signature3 >> 16) & 0xff);
    signature.add((int) (signature3 >> 8) & 0xff);
    signature.add((int) (signature3 >> 0) & 0xff);

    registerMessageParser(parser, signature);
  }

  public void registerMessageParser(MessageParser parser, Long signature1, Long signature2) throws Exception {
    List<Integer> signature = new LinkedList<>();

    signature.add((int) (signature1 >> 24) & 0xff);
    signature.add((int) (signature1 >> 16) & 0xff);
    signature.add((int) (signature1 >> 8) & 0xff);
    signature.add((int) (signature1 >> 0) & 0xff);

    signature.add((int) (signature2 >> 24) & 0xff);
    signature.add((int) (signature2 >> 16) & 0xff);
    signature.add((int) (signature2 >> 8) & 0xff);
    signature.add((int) (signature2 >> 0) & 0xff);

    registerMessageParser(parser, signature);
  }

  public MessageParser getParserFor(List<Integer> signature) {
    return this.parsers.get(signature);
  }

  /**
   * Reads a series of bytes until a header matches
   *
   * @return the appropriate messageParser
   */
  public MessageParser parseNextHeader() throws IOException {
    List<Integer> signature = new LinkedList<>();

    for (int i = 0; i < 4; i++) {
      Integer b = llio.readByte();
      signature.add(b);
    }

    int responseCodeLength = 4;
    Integer b = llio.readByte();
    signature.add(b);
    if ((b & 0xff) > 0x7f) {
      // Extended header. Read this byte and 4 more
      responseCodeLength = 5;
    }

    for (int read = 1; read < responseCodeLength; read++) {
      b = llio.readByte();
      signature.add(b);
    }

    MessageParser parser = this.getParserFor(signature);
    if (parser != null) {
      return parser;
    } else {
      logger.info(String.format(String.format("No signature found for header [%s] using default handler",
              this.formatSignature(signature))));
      return this.getDefaultParser();
    }
  }

  private String formatSignature(List<Integer> signature) {
    StringBuffer sb = new StringBuffer();

    for (Integer b : signature) {
      if (sb.length() != 0) sb.append(", ");
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }

  public MessageParser getDefaultParser() {
    return defaultParser;
  }

  public void setDefaultParser(MessageParser defaultParser) {
    this.defaultParser = defaultParser;
  }
}
