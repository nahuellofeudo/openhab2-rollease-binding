package com.nahuellofeudo.rolleasecontroller.response.parsing.mesage;

import org.slf4j.Logger;

import com.nahuellofeudo.rolleasecontroller.LowLevelIO;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser;

public abstract class BaseMessageParser implements MessageParser {
    protected Logger logger;
    protected Hub hub;
    protected LowLevelIO llio;

    public BaseMessageParser(Hub hub) {
        this.hub = hub;
    }

    @Override
    public boolean canParse(Integer[] type) {
        if (type.length != 2) {
            return false;
        }

        Integer[] signature = this.getSignature();
        return (type[0].equals(signature[0]) && type[1].equals(signature[1]));
    }

    /* Private utility methods */
    protected long toLongNumber(Integer[] bytes, int first, int length) {
        long value = 0;
        for (int x = 0; x < length; x++) {
            long byteRead = ((bytes[first + x] & 0xff));
            long shiftedByte = (byteRead << (x * 8));
            value |= shiftedByte;
        }
        return value;
    }

    protected int toInteger(Integer[] bytes, int first, int length) {
        return (int) (toLongNumber(bytes, first, length) & 0xffffffff);
    }

    protected String toString(Integer[] bytes, int first, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int x = first; x < first + length; x++) {
            stringBuilder.append((char) ((byte) (bytes[x] & 0xff)));
        }
        return stringBuilder.toString();
    }

    protected String toDynamicString(Integer[] bytes, int first) {
        int length = this.toInteger(bytes, first, 2);
        return this.toString(bytes, first + 2, length);
    }

}
