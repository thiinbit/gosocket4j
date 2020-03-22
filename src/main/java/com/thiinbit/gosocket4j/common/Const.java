/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j.common;

/**
 * Constant
 *
 * @author thiinbit
 * @version : Constant.java, v 0.1 20200321 8:06 PM thiinbit Exp $
 */
public class Const {
    /**
     * Default read timeout
     * - 42 seconds
     */
    public static final int   DEFAULT_READ_TIMEOUT     = 6 * 1000;
    ///**
    // * Default write timeout
    // * - 5s seconds
    // */
    //public static final long  DEFAULT_WRITE_TIMEOUT    = 5 * 1000;
    /**
     * Default heartbeat time
     * - 5 seconds
     */
    public static final long  DEFAULT_HEARTBEAT_TIME   = 5 * 1000;
    /**
     * Packet version
     */
    public static final short PACKET_VERSION           = 0x2A; // 101010 -> 42
    /**
     * Packet heartbeat version
     */
    public static final short PACKET_HEARTBEAT_VERSION = 0xFF; // 11111111 -> 255
    /**
     * Packet max body length
     * - 4M
     */
    public static final long  DEFAULT_MAX_BODY_LENGTH  = 4 * 1024 * 1024;
}