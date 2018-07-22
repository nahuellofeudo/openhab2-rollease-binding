/**
 * Copyright (c) 2014,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.rollease;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link Constants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Nahuel Lofeudo - Initial contribution
 */
@NonNullByDefault
public class Constants {

    public static final String BINDING_ID = "rollease";
    public static final String ROLLER = "rollease-roller";
    public static final String HUB = "rollease-hub";

    // List of all Channel ids
    public static final String PERCENTAGE_CLOSED = "percentage-closed";
    public static final String BATTERY_LEVEL = "battery-level";

    // Keys to store model objects as metadata
    public static final String ROLLER_OBJECT = "roller";

}
