/**
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j.test.client;

import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 * PacketTest
 *
 * @author thiinbit
 * @version : PacketTest.java, v 0.1 20200322 1:44 PM thiinbit Exp $
 */
public class PacketTest {
    public static void main(String[] args) throws Exception {
        Checksum adler32Checksum = new Adler32();
        String s = "Hello Gosocket!";
        adler32Checksum.update(s.getBytes(), 0, s.getBytes().length);
        System.out.println(adler32Checksum.getValue());
    }
}