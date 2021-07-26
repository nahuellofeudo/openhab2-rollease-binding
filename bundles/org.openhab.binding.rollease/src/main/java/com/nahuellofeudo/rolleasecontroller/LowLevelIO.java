package com.nahuellofeudo.rolleasecontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * All the routines that handle low-level I/O
 */
public class LowLevelIO {
    InputStream dis;
    OutputStream dos;
    ByteBuffer bb;
    long checksum;
    int sequenceNumber;

    public LowLevelIO(InputStream dis, OutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        bb = ByteBuffer.allocate(10240);
        bb.clear();
        this.checksum = 0;
    }

    private void putByte(byte b) {
        bb.put(b);
        checksum += b;
    }

    public void resetChecksum() {
        checksum = 0;
    }

    public void writeChecksum() {
        bb.put((byte) (this.checksum & 0xff));
    }

    public void flush() throws IOException {
        dos.write(bb.array(), 0, bb.position());
        dos.flush();
        bb.clear();
    }

    public void writeInt(Long val) throws IOException {
        putByte((byte) ((val >> 24) & 0xff));
        putByte((byte) ((val >> 16) & 0xff));
        putByte((byte) ((val >> 8) & 0xff));
        putByte((byte) ((val) & 0xff));
    }

    public void writeShort(int val) throws IOException {
        putByte((byte) ((val >> 8) & 0xff));
        putByte((byte) ((val) & 0xff));
    }

    public long readInt() throws IOException {
        long val = 0;
        val += dis.read() << 24;
        val += dis.read() << 16;
        val += dis.read() << 8;
        val += dis.read();
        return val;
    }

    public int readShort() throws IOException {
        int val = 0;
        val += dis.read() << 8;
        val += dis.read();
        return val;
    }

    public int readLSBShort() throws IOException {
        int val = 0;
        val += dis.read();
        val += dis.read() << 8;
        return val;
    }

    public void writeByte(int val) throws IOException {
        putByte((byte) val);
    }

    public int readByte() throws IOException {
        return dis.read();
    }

    public String readLSBString() throws IOException {
        int length = readLSBShort();

        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < length; x++) {
            sb.append((char) dis.read());
        }
        return sb.toString();
    }

    public String readString() throws IOException {
        int length = readShort();

        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < length; x++) {
            sb.append((char) dis.read());
        }
        return sb.toString();
    }

    public void readAndAssert(long... dataInts) throws IOException {
        for (long dataInt : dataInts) {
            readAndAssert((int) ((dataInt >> 24) & 0xff), (int) ((dataInt >> 16) & 0xff), (int) ((dataInt >> 8) & 0xff),
                    (int) (dataInt & 0xff));
        }
    }

    public void readAndAssert(int... dataBytes) throws IOException {
        int[] readBytes = new int[dataBytes.length];
        for (int x = 0; x < dataBytes.length; x++) {
            readBytes[x] = readByte();
        }
        if (Arrays.equals(readBytes, dataBytes))
            return;

        // The sequence doesn't match
        throw new ProtocolException(String.format("Sequence of bytes does not match. Expected: %s. Received: %s",
                toStr(dataBytes), toStr(readBytes)));
    }

    public void readAndAssertHeader(int subheader, long expectedResponse) throws IOException {
        readAndAssert(Constants.HEADER);
        readAndAssert(subheader);
        readAndAssert(expectedResponse);
        readAndAssert(0x06);
        for (byte c : Constants.ID_STRING.getBytes()) {
            readAndAssert((int) c);
        }
    }

    public void readAndAssertHeader(long expectedResponse) throws IOException {
        readAndAssert(Constants.HEADER);
        readAndAssert(expectedResponse);
        readAndAssert(0x06);
        for (byte c : Constants.ID_STRING.getBytes()) {
            readAndAssert((int) c);
        }
    }

    public void readAndAssertHeader() throws IOException {
        readAndAssert(0x06);
        for (byte c : Constants.ID_STRING.getBytes()) {
            readAndAssert((int) c);
        }
    }

    public String toStr(int[] dataBytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int x = 0; x < dataBytes.length; x++) {
            if (x > 0)
                sb.append(", ");
            sb.append(String.format("%x", (byte) (dataBytes[x] & 0xff)));
        }
        sb.append("]");
        return (sb.toString());
    }

    public String toStr(List<Integer> dataBytes) {
        int[] arr = new int[dataBytes.size()];
        for (int x = 0; x < arr.length; x++) {
            arr[x] = dataBytes.get(x);
        }
        return toStr(arr);
    }

    public void writeString(String val) throws IOException {
        writeShort(val.length());
        writeNakedString(val);
    }

    public void writeShortString(String val) {
        putByte((byte) val.length());
        writeNakedString(val);
    }

    public void writeNakedString(String val) {
        byte[] bytes = val.getBytes();
        for (int x = 0; x < val.length(); x++) {
            putByte(bytes[x]);
        }
    }

    public void writeBytes(int... dataBytes) throws IOException {
        for (int dataByte : dataBytes) {
            writeByte(dataByte);
        }
    }

    public void writeInts(long... dataInts) throws IOException {
        for (long dataInt : dataInts) {
            writeInt(dataInt);
        }
    }

    public void writeCommandHeader(long setidCommand) throws IOException {
        writeInt(Constants.HEADER);
        writeInt(setidCommand);
        writeByte(0x05);
        writeNakedString(Constants.ID_STRING);
    }

    public long readLSBNumber(int length) throws IOException {
        // Read 6 byte IDs - LSB First
        long value = 0;
        for (int i = 0; i < 6; i++) {
            value |= (((long) this.readByte()) << (i * 8));
        }
        return value;
    }

    public void writeLSBNumber(long value, int length) throws IOException {
        for (int i = length - 1; i >= 0; i--) {
            this.writeByte((int) (value & 0xff));
            value = value >> 8;
        }
    }

    public void writeSequenceNumber() throws IOException {
        this.writeLSBNumber(sequenceNumber++, 2);
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
