package com.nahuellofeudo.rolleasecontroller;

import com.nahuellofeudo.rolleasecontroller.model.Hub;
import com.nahuellofeudo.rolleasecontroller.model.Roller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ldap.Control;

public class Main {

  public static void main(String[] argv) throws Exception {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    Logger logger = LoggerFactory.getLogger(Main.class);

    // The hub's model object
    Hub hub = new Hub();

    Controller controller = new Controller(hub, "10.1.16.191");
    controller.connectAndRun();

    Roller roller;
    do {
      System.out.println("Waiting for roller info...");
      Thread.sleep(1000);
      roller = hub.getRollerById(0x0100055f5fa7l);
    } while (roller == null);

    controller.adjustPosition(roller, 90);
    Thread.sleep(5000);

    controller.adjustPosition(roller, 100);

    System.out.println("Done.");
  }
}
