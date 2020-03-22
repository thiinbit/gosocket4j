/**
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * ByteUtils
 *
 * @author thiinbit
 * @version : ByteUtils.java, v 0.1 20200322 8:16 AM thiinbit Exp $
 */
public class ByteUtils {

    /**
     * Unsigned int32 convert to byte[]
     * - use long to save unsigned int32
     */
    public static byte[] uint32ToBytes(long uint32Value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(uint32Value);

        return Arrays.copyOfRange(bytes, 4, 8);
    }

    /**
     * bytes convert to unsigned int32
     * - use long to save unsigned int32
     */
    public static long bytesToUint32(byte[] uint32Bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8)
                .order(ByteOrder.BIG_ENDIAN)
                .put(new byte[] {0, 0, 0, 0})
                .put(uint32Bytes);

        buffer.position(0);

        return buffer.getLong();
    }

    /**
     * Unsigned int8 convert to byte[]
     * - use short to save unsigned int8
     */
    public static byte[] uint8ToBytes(short uint8Value) {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes)
                .order(ByteOrder.BIG_ENDIAN)
                .putShort(uint8Value);

        return Arrays.copyOfRange(bytes, 1, 2);
    }

    /**
     * bytes convert to unsigned int8
     * - use short to save unsigned int32
     */
    public static short byteToUint8(byte uint8Byte) {
        ByteBuffer buffer = ByteBuffer.allocate(2).put(new byte[] {0,}).put(uint8Byte);
        buffer.position(0);

        return buffer
                .order(ByteOrder.BIG_ENDIAN)
                .getShort();
    }
}