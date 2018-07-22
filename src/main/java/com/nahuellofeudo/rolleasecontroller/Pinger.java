package com.nahuellofeudo.rolleasecontroller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object pings the controller every 30 seconds or so, to prevent it from closing the connection
 */
class Pinger implements Runnable {
    Logger logger = LoggerFactory.getLogger(Pinger.class);
    Controller controller;

    public Pinger(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        int counter = 0;
        while (true) {
            try {
                Thread.sleep(20000);
                if (counter == 5) {
                    // Every minute pull the whole hub status instead of just pinging
                    logger.info("Refreshing hub state...");
                    controller.getHubInfo();
                    counter = 0;
                } else {
                    logger.info("Pinging controller...");
                    controller.ping();
                    counter++;
                }
            } catch (InterruptedException e) {
                logger.error("Pinger thread interrupted. Exiting.");
                return;
            } catch (IOException e) {
                logger.error("The Controller reported an error when pinging the controller.", e);
            }
        }
    }
}
