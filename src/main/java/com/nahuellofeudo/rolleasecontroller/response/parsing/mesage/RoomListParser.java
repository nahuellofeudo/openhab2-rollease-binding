package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Room;

/**
 * Parses the list of rooms.
 * It's not clear at this point if this information is useful or not.
 *
 * @author Nahuel Lofeudo
 *
 */
public class RoomListParser extends BaseMessageParser {
    Logger logger = LoggerFactory.getLogger(RoomListParser.class);

    public RoomListParser(Hub hub) {
        super(hub);
    }

    @Override
    public Integer[] getSignature() {
        return new Integer[] { 0x01, 0x01 };
    }

    @Override
    public void parse(Integer[] bytes) throws IOException {
        logger.info("Parsing Room List...");
        int ptr = 0x0e;

        int numberOfRooms = bytes[ptr];
        ptr++;

        for (int x = 0; x < numberOfRooms; x++) {

            // Parse room Id
            ptr += 2;
            String roomId = this.toDynamicString(bytes, ptr);
            ptr += 2 + roomId.length();

            // Parse room name
            ptr += 7;
            String roomName = this.toDynamicString(bytes, ptr);
            ptr += 2 + roomName.length();

            this.hub.addRoom(new Room(roomId, roomName, x));
        }
    }
}
