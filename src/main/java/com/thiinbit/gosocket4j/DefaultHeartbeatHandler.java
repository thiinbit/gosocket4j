/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j;

import com.thiinbit.gosocket4j.common.Const;

import java.io.IOException;

import static java.lang.String.format;

/**
 * DefaultHeartbeatHandler
 *
 * @author thiinbit
 * @version : DefaultHeartbeatHandler.java, v 0.1 20200321 10:55 PM thiinbit Exp $
 */
class DefaultHeartbeatHandler {

    <T> void sendHeartbeatPacket(TCPClient<T> cli) throws IOException {

        Packet pingPac = new Packet(Const.PACKET_HEARTBEAT_VERSION, Packet.PINT_CMD, Packet.PING_CHECKSUM);

        cli.packetHandler().sendPacket(pingPac, cli);

        cli.dLog().debug(format("Cli %s heartbeat. ping sent", cli.name()));
    }

    <T> void receivedHeartbeat(Packet pac, TCPClient<T> cli) throws IOException {

        if (pac.body[0] == Packet.PINT_CMD[0]) {
            Packet pongPac = new Packet(Const.PACKET_HEARTBEAT_VERSION, Packet.PONG_CMD, Packet.PONG_CHECKSUM);

            cli.packetHandler().sendPacket(pongPac, cli);

            cli.dLog().debug(format("Cli %s heartbeat. pong reply", cli.name()));
        }

        if (pac.body[0] == Packet.PONG_CMD[0]) {
            cli.dLog().debug(format("Cli %s heartbeat. pong received", cli.name()));
        }
    }
}