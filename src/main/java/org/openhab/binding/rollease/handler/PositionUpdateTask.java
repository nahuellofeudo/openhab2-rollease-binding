package org.openhab.binding.rollease.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a recurring task that tries to update the position of a roller as many times as necessary,
 * Because the people at Rollease could not be bothered with implementing a reliable transport protocol.
 *
 * @author nahuel
 *
 */
class PositionUpdateTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PositionUpdateTask.class);

    // How much must be the difference between current and new position before we order the roller to move
    // If this is too small, we risk that a roller that doesn't land exactly in the spot moves forever
    protected static final int WIGGLE_ROOM = 2;

    // How much time to give rollers before retrying (milliseconds)
    private static final int RETRY_DELAY = 30000;

    String id;
    HubHandler hubHandler;
    DecimalType position;
    int delay = 30000;
    int retries = 0;
    CancelableScheduledExecutorService executor;

    public PositionUpdateTask(HubHandler hubHandler, String id, DecimalType position,
            CancelableScheduledExecutorService executor) {
        super();
        this.id = id;
        this.hubHandler = hubHandler;
        this.position = position;
        this.executor = executor;
    }

    @Override
    public void run() {
        logger.debug("Running position updater task...");
        int currentPosition = this.hubHandler.getRollerFromId(id).getPercentClosed();
        int displacement = Math.abs(currentPosition - position.intValue());

        // Run a maximum of 5 times.
        if (this.retries > 4) {
            logger.error(
                    String.format("Failed to update the position of roller %s %d times. Giving up.", id, this.retries));
            return;
        }

        if (displacement > WIGGLE_ROOM) {
            logger.info(String.format("Updating roller %s to position %d", id, position.intValue()));
            this.hubHandler.setPosition(id, position);

            // Schedule update based on amount of displacement requested + 1 second
            int actualDelay = ((this.delay * displacement) / 100) + 1000;
            executor.reschedule(id, this, actualDelay);
            this.retries++;
        } else {
            logger.info(String.format("Not updating roller %s. Current position %d is close to requested %d", id,
                    currentPosition, position.intValue()));
        }

    }
}
