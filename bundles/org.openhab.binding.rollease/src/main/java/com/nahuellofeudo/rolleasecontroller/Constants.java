package com.nahuellofeudo.rolleasecontroller;

/**
 * Miscellaneous constants
 *
 * @author Nahuel Lofeudo
 *
 */
public class Constants {
    public static String ID_STRING = "Smart_Id1_y:";

    public static long HEADER = 0x00000003;
    public static long CONNECT_COMMAND = 0x03000006;
    public static long CONNECT_RESPONSE = 0x0f000007;

    public static long LOGIN_COMMAND = 0x0f000008;
    public static long LOGIN_RESPONSE = 0x04000009;

    public static long PING_COMMAND = 0x03000015;
    public static long PING_RESPONSE = 0x03000016;

    public static long SETID_COMMAND = 0x28000090;
    public static long SETID_RESPONSE = 0x03000091;

    public static long UNKNOWN1_COMMAND = 0x23000090;
    public static long UNKNOWN1_RESPONSE1 = 0x28000091;
    public static long UNKNOWN1_RESPONSE2 = 0x03000091;

    public static long GET_HUB_INFO_COMMAND = 0x1e000090;
    public static long GET_HUB_INFO_RESPONSE = 0x4a000091;
    public static long GET_HUB_INFO_AUTH = 0x55000091;

    public static long GET_ITEMS_ROOMS = 0x01000091;
    public static long GET_ITEMS_ROLLERS = 0x03000091;

    public static long GET_ITEMS_END = 0x23000091;

    public static long CLOSE_COMMAND = 0x2d000090;
    public static long ADJUST_POSITION_COMMAND = 0x34000090;
    public static long ADJUST_POSITION_RESPONSE1 = 0x44000091;
    public static long ADJUST_POSITION_RESPONSE2 = 0x34000091;
}
