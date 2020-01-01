/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.rollease.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is pretty much an Executor, with two differences:
 * 1) Each task has an id associated.
 * 2) Only one task may be scheduled for each id.
 * Scheduling a new task for an id that already has a task scheduled cancels the old task.
 *
 * @author Nahuel Lofeudo
 *
 */
@SuppressWarnings("rawtypes")
public class CancelableScheduledExecutorService {
    private final Logger logger = LoggerFactory.getLogger(CancelableScheduledExecutorService.class);

    ScheduledExecutorService executor;
    Map<String, ScheduledFuture> futures;

    public CancelableScheduledExecutorService() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.futures = new HashMap<>();

    }

    public synchronized void reschedule(String id, PositionUpdateTask task, int delay) {
        logger.info(String.format("Rescheduling update of id %s in %d milliseconds", id, delay));
        ScheduledFuture newFuture = this.executor.schedule(task, delay, TimeUnit.MILLISECONDS);
        futures.put(id, newFuture);
    }

    public synchronized void schedule(String id, PositionUpdateTask task, int delay) {
        logger.info(String.format("Scheduling update of id %s in %d milliseconds", id, delay));
        this.cancel(id);
        this.reschedule(id, task, delay);
    }

    public synchronized void schedule(String id, PositionUpdateTask task) {
        this.schedule(id, task, 0);
    }

    /**
     * If there is a task already present for this ID, cancel it.
     *
     * @param id the id of the task to try and cancel
     */
    public synchronized void cancel(String id) {

        if (futures.containsKey(id)) {
            ScheduledFuture future = this.futures.get(id);
            if (!future.isDone()) {
                logger.info(String.format("Canceling existing update of id %s", id));
                future.cancel(false);
            }
        }
    }
}
