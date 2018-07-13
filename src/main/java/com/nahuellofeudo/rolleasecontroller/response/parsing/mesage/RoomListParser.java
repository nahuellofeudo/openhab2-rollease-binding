package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RoomListParser implements com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser<Object> {
  Logger logger = LoggerFactory.getLogger(RoomListParser.class);
  Hub response;
  LowLevelIO llio;

  public RoomListParser(Hub response, LowLevelIO llio) {
    this.response = response;
    this.llio = llio;
  }

  @Override
  public Object parse() throws IOException {
    logger.info("Parsing Room List...");
    llio.readAndAssertHeader();

    llio.readAndAssert(0x70, 0x00, 0x01, 0x01); // ???
    llio.readLSBShort();                        // Sequence number. Ignore

    llio.readAndAssert(
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x10, 0x00, 0x01);

    int numberOfRooms = llio.readShort();

    for (; numberOfRooms > 0; numberOfRooms--) {
      // Parse a room
      llio.readAndAssert(0x00, 0x02); // Header
      String id = llio.readLSBString();
      llio.readAndAssert(0x0f, 0x00, 0x01, 0x00);
      int number = llio.readByte();
      llio.readAndAssert(0x0e, 0x00);
      String name = llio.readLSBString();
      response.addRoom(new Room(id, name, number));
    }

    // Read checksum
    llio.readShort();

    return null;
  }
}
