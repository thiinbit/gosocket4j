/**
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j.test.client;

import com.thiinbit.gosocket4j.Codec;
import com.thiinbit.gosocket4j.TCPClient;
import com.thiinbit.gosocket4j.exception.CodecException;

import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

/**
 * TCPClientTest
 *
 * @author thiinbit
 * @version : TCPClientTest.java, v 0.1 20200322 12:10 PM thiinbit Exp $
 */
public class TCPClientTest {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8888;

        TCPClient<String> cli = new TCPClient<>(host, port,
                new Codec<String>() {
                    @Override
                    public byte[] encode(String message) throws CodecException {
                        return message.getBytes();
                    }

                    @Override
                    public String decode(byte[] body) throws CodecException {
                        return new String(body);
                    }
                }, (m, c) ->
                c.dLog().debug(format("Cli %s Received message: %s", c.name(), m)));

        cli.setRunModeAsDebug(true).dial();

        cli.sendMessage("Hello Gosocket!");

        TimeUnit.SECONDS.sleep(14);

        cli.hangup(null);
    }
}