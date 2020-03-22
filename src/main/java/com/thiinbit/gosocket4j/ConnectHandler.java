/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j;

import com.thiinbit.gosocket4j.common.Const;
import com.thiinbit.gosocket4j.enums.ClientStatusEnum;
import com.thiinbit.gosocket4j.exception.ReadDataException;
import com.thiinbit.gosocket4j.util.ByteUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

/**
 * thiinbit
 *
 * @author thiinbit
 * @version : ConnectHandler.java, v 0.1 20200321 10:51 PM thiinbit Exp $
 */
class ConnectHandler {

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    <T> void handleConnect(final TCPClient<T> cli) {
        executor.execute(() -> {
            try {
                handleWrite(cli);
            } catch (Exception e) {
                cli.hangup(e);
            }
        });

        executor.execute(() -> {
            try {
                handleRead(cli);
            } catch (Exception e) {
                cli.hangup(e);
            }
        });
    }

    private <T> void handleWrite(TCPClient<T> cli) {
        cli.dLog().debug(format("Cli %s start handle write.", cli.name()));
        while (true) {
            try {
                if (cli.status() != ClientStatusEnum.RUNNING) {
                    cli.dLog().debug(format("Cli %s stop write.", cli.name()));
                    break;
                }

                T message = cli.pollMessage(cli.heartbeatTime());
                if (message == null) {
                    if (cli.millisIntervalSinceLastActive() > cli.heartbeatTime()) {
                        cli.heartbeatHandler().sendHeartbeatPacket(cli);
                        cli.dLog().debug(format("Cli %s healthy check. ping sent", cli.name()));
                    } else {
                        cli.dLog().debug(format("Cli %s healthy check.", cli.name()));
                    }

                    continue;
                }

                byte[] data = cli.codec().encode(message);

                Packet pac = new Packet(Const.PACKET_VERSION, data);

                cli.packetHandler().sendPacket(pac, cli);
            } catch (Exception e) {
                cli.hangup(e);
                break;
            }
        }
    }

    private <T> void handleRead(TCPClient<T> cli) {
        cli.dLog().debug(format("Cli %s start handle read.", cli.name()));
        while (true) {
            try {
                if (cli.status() != ClientStatusEnum.RUNNING) {
                    cli.dLog().debug(format("Cli %s stop read.", cli.name()));
                    break;
                }

                byte[] verByte = new byte[Packet.VER_LEN];
                int reVerLen = cli.socket().getInputStream().read(verByte);
                if (reVerLen == -1) {
                    throw new ReadDataException("Server closed. " + cli.socket().getRemoteSocketAddress().toString());
                }
                if (reVerLen < Packet.VER_LEN) {
                    throw new ReadDataException("Read wrong verLen: " + reVerLen);
                }
                short ver = ByteUtils.byteToUint8(verByte[0]);
                if (ver != Const.PACKET_VERSION && ver != Const.PACKET_HEARTBEAT_VERSION) {
                    throw new ReadDataException("Read wrong ver: " + ver);
                }

                byte[] lenBytes = new byte[Packet.LEN_LEN];
                int reLenLen = cli.socket().getInputStream().read(lenBytes);
                if (reLenLen < Packet.LEN_LEN) {
                    throw new ReadDataException("Read wrong lenLen: " + reLenLen);
                }
                long bodyLen = ByteUtils.bytesToUint32(lenBytes);

                byte[] bodyBytes = new byte[(int) bodyLen];
                int reBodyLen = cli.socket().getInputStream().read(bodyBytes);
                if (bodyLen != reBodyLen) {
                    throw new ReadDataException("Read wrong bodyLen: " + reBodyLen + ", except: " + bodyLen);
                }
                if (bodyLen > cli.maxPacketBodyLen()) {
                    throw new ReadDataException("Exceed max body len limit: " + cli.maxPacketBodyLen() + ", actually: " + bodyLen);
                }

                byte[] checksumBytes = new byte[Packet.CHECKSUM_LEN];
                int reChecksumLen = cli.socket().getInputStream().read(checksumBytes);
                if (reChecksumLen < Packet.CHECKSUM_LEN) {
                    throw new ReadDataException("Read wrong checksumLen: " + reChecksumLen);
                }
                long checksum = ByteUtils.bytesToUint32(checksumBytes);

                long exceptChecksum = Packet.checksum(bodyBytes);
                if (checksum != exceptChecksum) {
                    throw new ReadDataException("Read checksum fail: " + checksum + ", except: " + exceptChecksum);
                }

                Packet pac = new Packet(ver, bodyBytes, checksumBytes);

                if (ver == Const.PACKET_VERSION) {
                    cli.packetHandler().receivedPacket(pac, cli);
                }
                if (ver == Const.PACKET_HEARTBEAT_VERSION) {
                    cli.heartbeatHandler().receivedHeartbeat(pac, cli);
                }
            } catch (Exception e) {
                cli.hangup(e);
                break;
            }
        }
    }
}