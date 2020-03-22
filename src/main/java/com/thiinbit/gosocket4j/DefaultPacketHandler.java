/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j;

import com.thiinbit.gosocket4j.exception.CodecException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.lang.String.format;

/**
 * DefaultPacketHandler
 *
 * @author thiinbit
 * @version : DefaultPacketHandler.java, v 0.1 20200322 10:50 AM thiinbit Exp $
 */
class DefaultPacketHandler implements PacketHandler {

    @Override
    public <T> void sendPacket(Packet packet, TCPClient<T> cli) throws IOException {

        ByteBuffer buf = ByteBuffer.allocate(Packet.HEADER_LEN + packet.body.length + Packet.CHECKSUM_LEN)
                .order(ByteOrder.BIG_ENDIAN)
                .put(packet.head)
                .put(packet.body)
                .put(packet.checksum);

        cli.socket().getOutputStream().write(buf.array());

        cli.dLog().debug(format("Cli %s packet sent. ver: %s, len: %s, checksum: %s",
                cli.name(), packet.getVer(), packet.getLen(), packet.getChecksum()));
        cli.updateLastActive();
    }

    @Override
    public <T> void receivedPacket(Packet packet, TCPClient<T> cli) throws CodecException {
        T message = cli.codec().decode(packet.body);

        cli.dLog().debug(format("Cli %s packet received. ver: %s, len: %s, checksum: %s",
                cli.name(), packet.getVer(), packet.getLen(), packet.getChecksum()));

        cli.updateLastActive();
        cli.messageListener().onMessage(message, cli);
    }
}