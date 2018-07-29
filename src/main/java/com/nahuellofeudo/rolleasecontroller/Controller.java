package com.nahuellofeudo.rolleasecontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nahuellofeudo.rolleasecontroller.listener.HubStatusListener.HubStatus;
import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;
import com.nahuellofeudo.rolleasecontroller.response.ConnectResponse;
import com.nahuellofeudo.rolleasecontroller.response.LoginResponse;
import com.nahuellofeudo.rolleasecontroller.response.parsing.HeaderParser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.MessageParser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.AdjustPositionResponse1Parser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.AdjustPositionResponse2Parser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.AuthInfoResponseParser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.HubInfoEndParser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.HubInfoResponseParser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.RollerListParser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.RoomListParser;
import com.nahuellofeudo.rolleasecontroller.response.parsing.mesage.UnknownMessageParser;

public class Controller {
    Logger logger = LoggerFactory.getLogger(Controller.class);
    LowLevelIO llio;
    Thread controllerThread;
    Thread pingerThread;
    HeaderParser headerParser;
    Semaphore globalLock;

    // State is kept here
    Hub hub;
    String host;
    int port;

    public Controller(Hub hub, String host) {
        this(hub, host, 12416);
    }

    public Controller(Hub hub, String host, int port) {
        this.host = host;
        this.port = port;
        this.hub = hub;
    }

    public ConnectResponse connect() throws IOException, InterruptedException {
        // Send command
        llio.writeInts(Constants.HEADER, Constants.CONNECT_COMMAND);
        llio.flush();

        // read responses
        llio.readAndAssert(Constants.HEADER, Constants.CONNECT_RESPONSE);
        String deviceid = llio.readString();
        return new ConnectResponse(deviceid);
    }

    public LoginResponse login(ConnectResponse deviceID) throws IOException, InterruptedException {
        // Send login command
        llio.writeInts(Constants.HEADER, Constants.LOGIN_COMMAND);
        llio.writeString(deviceID.getIdentifier());
        llio.flush();

        // Expect login response
        llio.readAndAssert(Constants.HEADER, Constants.LOGIN_RESPONSE);

        int val = llio.readByte();
        if (val != 0) {
            throw new ProtocolException(String.format("Login response value is wrong: %x", val));
        }
        llio.flush();
        return new LoginResponse();
    }

    public void ping() throws IOException, InterruptedException {
        globalLock.acquire();
        // Send login command
        llio.writeInts(Constants.HEADER, Constants.PING_COMMAND);
        llio.flush();
        globalLock.release();
    }

    public void setid() throws IOException, InterruptedException {
        // Send login command
        llio.writeCommandHeader(Constants.SETID_COMMAND);
        llio.writeBytes(0x16, 0x00, 0x0e, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0c, 0x00, 0x06, 0x00,
                0x12, 0x03, 0x11, 0x07, 0x38, 0x16, 0xff, 0x9b);
        llio.flush();

        // Expect login response
        llio.readAndAssert(Constants.HEADER, Constants.SETID_RESPONSE);
    }

    public void setUnknown1() throws IOException, InterruptedException {
        llio.writeCommandHeader(Constants.UNKNOWN1_COMMAND);

        // WTF is this command?
        llio.writeBytes(0x11, 0x00, 0x15, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x60, 0x02, 0x01, 0x00,
                0x30, 0xff, 0xa9);
        llio.flush();
        llio.readAndAssertHeader(Constants.UNKNOWN1_RESPONSE1);
        llio.readAndAssert(0x16, 0x00, 0x0f, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0c, 0x00, 0x06,
                0x00, 0x12, 0x03, 0x11, 0x07, 0x38, 0x16, 0xff, 0x9d);
        llio.readAndAssert(Constants.HEADER, Constants.UNKNOWN1_RESPONSE2);
    }

    public void adjustPosition(Roller roller, int closedPercentage) throws IOException, InterruptedException {
        globalLock.acquire();
        llio.writeCommandHeader(Constants.ADJUST_POSITION_COMMAND);

        // Length
        llio.writeBytes(0x22, 0x00);

        // Start calculating checksum
        llio.resetChecksum();

        // Subtype?
        llio.writeBytes(0x22, 0x01);
        llio.writeSequenceNumber();

        llio.writeBytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01);

        // Length of address + roller address
        llio.writeBytes(0x06, 0x00);
        llio.writeLSBNumber(roller.getId(), 6);

        // Position??
        llio.writeBytes(0x03, 0x01, 0x01, 0x00, 0x19);
        llio.writeBytes(0x04, 0x01, 0x03, 0x00, 0x01);
        llio.writeBytes((byte) closedPercentage, 0x00);

        // Checksum
        llio.writeBytes(0xff);
        llio.writeChecksum();
        llio.flush();
        globalLock.release();
    }

    /**
     * Infinite loop that updates the state of the Controller Information Structure
     * (including rooms / rollers)
     *
     * @throws Exception when something goes extremely wrong
     */
    public void registerHeaderParsers() throws Exception {
        // Register header parsers
        // headerParser.registerMessageParser(new NullResponseParser(), Constants.HEADER, Constants.SETID_RESPONSE);

        headerParser.registerMessageParser(new HubInfoResponseParser(hub));
        headerParser.registerMessageParser(new AuthInfoResponseParser(hub));
        headerParser.registerMessageParser(new AuthInfoResponseParser(hub));
        headerParser.registerMessageParser(new RoomListParser(hub));
        headerParser.registerMessageParser(new RoomListParser(hub));
        headerParser.registerMessageParser(new RollerListParser(hub));
        headerParser.registerMessageParser(new HubInfoEndParser(hub));
        headerParser.registerMessageParser(new AdjustPositionResponse1Parser(hub));
        headerParser.registerMessageParser(new AdjustPositionResponse2Parser(hub));
        headerParser.setDefaultParser(new UnknownMessageParser());
    }

    private void getHubInfo(boolean isInitializing) throws IOException, InterruptedException {
        if (!isInitializing) {
            globalLock.acquire();
        }
        llio.writeCommandHeader(Constants.GET_HUB_INFO_COMMAND);

        llio.writeBytes(0x0c, 0x00);
        llio.resetChecksum();

        llio.writeBytes(0xf0, 0x00);
        llio.writeSequenceNumber();
        llio.writeBytes(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xff);
        llio.writeChecksum();
        llio.flush();
        if (!isInitializing) {
            globalLock.release();
        }
    }

    public void getHubInfo() throws IOException, InterruptedException {
        this.getHubInfo(false);
    }

    private void getInitialHubInfo() throws IOException, InterruptedException {
        this.getHubInfo(true);
    }

    /**
     * Main method. It keeps the state of the state machine.
     *
     * @throws Exception
     */
    public void mainLoop() {
        MessageParser messageParser;
        Socket socket = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        boolean reconnecting = false;
        globalLock = new Semaphore(1);

        while (true) {
            try {
                //
                // If we are reconnecting, clean up possible previous mess
                boolean globalLockAcquired = globalLock.tryAcquire();
                if (!globalLockAcquired && !reconnecting) {
                    logger.error("Re-entrant connection attempt while not reconnecting? **BUG**");
                    throw new Exception("Tried to reconnect wile reconnecting == false");
                }

                // Wait for a few milliseconds in case the global lock is released
                if (!globalLockAcquired) {
                    int retryCount = 0;
                    while (!globalLockAcquired && retryCount < 10) {
                        Thread.sleep(100);
                        globalLockAcquired = globalLock.tryAcquire();
                        retryCount++;
                    }
                    if (retryCount == 10 && !globalLockAcquired) {
                        // We timed out. Log the condition.
                        logger.error("Could not re-aquire global lock after 10 attempts.");
                    }
                }

                // Connect
                hub.setCurrentStatus(HubStatus.CONNECTING);
                try {
                    socket = new Socket(host, port);
                    socket.setSoTimeout(120000);
                    socket.setSoLinger(false, 0);
                    outputStream = socket.getOutputStream();
                    inputStream = socket.getInputStream();
                } catch (Exception e) {
                    logger.error("Could not connect to Rollease controller.", e);
                    throw new Exception(e);
                }

                llio = new LowLevelIO(inputStream, outputStream);
                headerParser = new HeaderParser(llio);
                registerHeaderParsers();

                // Initialize
                logger.debug("Sending connect controller...");
                ConnectResponse connectResponse = connect();
                logger.debug("Device id is: " + connectResponse.getIdentifier());

                logger.debug("Sending Login Request...");
                login(connectResponse);

                logger.debug("Sending SetID controller...");
                setid();

                logger.debug("Sending Unknown Controller 1");
                setUnknown1();

                llio.setSequenceNumber(4);

                logger.debug("Querying hub configuration...");
                getInitialHubInfo();

                logger.debug("Initializing asynchronous pinger thread");
                this.pingerThread = new Thread(new Pinger(this));
                this.pingerThread.setName("Rollease Pinger");
                this.pingerThread.start();

                logger.info("Controller started");

                // Let calls come in
                globalLock.release();
                hub.setCurrentStatus(HubStatus.ONLINE);

                // Run
                do {
                    headerParser.parseNext();
                } while (true);
            } catch (Exception e) {
                hub.setCurrentStatus(HubStatus.ERROR);
                logger.error(String.format("Caught exception %s. Attempting to reconnect...", e.getClass()), e);
                // Try to clean up if possible
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e2) {
                        logger.error("Error closing leftover socket: " + e2.getMessage(), e2);
                    }
                }
                if (this.pingerThread != null) {
                    this.pingerThread.interrupt();
                    this.pingerThread = null;
                }
                reconnecting = true;
                hub.setCurrentStatus(HubStatus.OFFLINE);
            }
        }
    }

    public void connectAndRun() {
        this.controllerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mainLoop();
            }
        });
        this.controllerThread.setName("Rollease Controller");
        this.controllerThread.start();
    }
}
