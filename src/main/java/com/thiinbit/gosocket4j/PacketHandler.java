/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j;

import com.thiinbit.gosocket4j.exception.CodecException;

import java.io.IOException;

/**
 * thiinbit
 *
 * @author thiinbit
 * @version : PacketHandler.java, v 0.1 20200321 10:41 PM thiinbit Exp $
 */
interface PacketHandler {
    <T> void sendPacket(Packet packet, TCPClient<T> cli) throws IOException;

    <T> void receivedPacket(Packet packet, TCPClient<T> cli) throws CodecException;
}