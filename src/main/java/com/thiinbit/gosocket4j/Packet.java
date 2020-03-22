/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j;

import com.thiinbit.gosocket4j.util.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 * Packet
 *
 * @author thiinbit
 * @version : Packet.java, v 0.1 20200321 8:20 PM thiinbit Exp $
 */
class Packet {

    /**
     * Packet related const
     */
    public static transient final int    VER_LEN       = 1;
    public static transient final int    LEN_LEN       = 4;
    public static transient final int    HEADER_LEN    = VER_LEN + LEN_LEN;
    public static transient final int    CHECKSUM_LEN  = 4;
    public static transient final byte[] PINT_CMD      = {0,};
    public static transient final byte[] PONG_CMD      = {1,};
    public static transient final byte[] PING_CHECKSUM = ByteUtils.uint32ToBytes(checksum(PINT_CMD));
    public static transient final byte[] PONG_CHECKSUM = ByteUtils.uint32ToBytes(checksum(PONG_CMD));

    /**
     * 1 byte version (8bit)
     * 4 byte length (32bit)
     */
    byte[] head;
    /**
     * Body bytes
     */
    byte[] body;
    /**
     * Checksum
     * - 4 byte (32bit)
     * - adler32 checksum
     */
    byte[] checksum;

    /**
     * Constructor
     */
    Packet(short version, byte[] body) {
        ByteBuffer headBuf = ByteBuffer.allocate(HEADER_LEN);
        headBuf.order(ByteOrder.BIG_ENDIAN)
                .put(ByteUtils.uint8ToBytes(version))
                .put(ByteUtils.uint32ToBytes(body.length));

        this.head = headBuf.array();
        this.body = body;
        this.checksum = ByteUtils.uint32ToBytes(checksum(body));
    }

    /**
     *
     */
    Packet(short version, byte[] body, byte[] checksumBytes) {
        ByteBuffer headBuf = ByteBuffer.allocate(HEADER_LEN);
        headBuf.order(ByteOrder.BIG_ENDIAN)
                .put(ByteUtils.uint8ToBytes(version))
                .put(ByteUtils.uint32ToBytes(body.length));

        this.head = headBuf.array();
        this.body = body;
        this.checksum = checksumBytes;
    }

    /**
     * Calc checksum
     */
    static long checksum(byte[] data) {
        Checksum adler32Checksum = new Adler32();
        adler32Checksum.update(data, 0, data.length);
        return adler32Checksum.getValue();
    }

    /**
     * Getter method for property <tt>ver</tt>.
     *
     * @return property value of ver
     */
    short getVer() {
        return ByteUtils.byteToUint8(head[0]);
    }

    /**
     * Getter method for property <tt>length</tt>.
     *
     * @return property value of length
     */
    long getLen() {
        return ByteUtils.bytesToUint32(Arrays.copyOfRange(head, 1, 5));
    }

    /**
     * Getter method for property <tt>checksum</tt>.
     *
     * @return property value of checksum
     */
    long getChecksum() {
        return ByteUtils.bytesToUint32(checksum);
    }
}
