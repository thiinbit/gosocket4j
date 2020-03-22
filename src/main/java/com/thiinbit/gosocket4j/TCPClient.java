/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j;

import com.thiinbit.gosocket4j.enums.ClientStatusEnum;
import com.thiinbit.gosocket4j.enums.RunModeEnum;
import com.thiinbit.gosocket4j.exception.IllegalClientStatusException;
import com.thiinbit.gosocket4j.logs.Log;
import com.thiinbit.gosocket4j.logs.Slf4jLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.thiinbit.gosocket4j.common.Const.DEFAULT_HEARTBEAT_TIME;
import static com.thiinbit.gosocket4j.common.Const.DEFAULT_MAX_BODY_LENGTH;
import static com.thiinbit.gosocket4j.common.Const.DEFAULT_READ_TIMEOUT;
import static java.lang.String.format;

/**
 * TCPClient
 *
 * @author thiinbit
 * @version : TCPClient.java, v 0.1 20200321 11:13 AM thiinbit Exp $
 */
public class TCPClient<T> {
    /**
     * TcpClient run mode
     */
    public static RunModeEnum RUN_MODE = RunModeEnum.RELEASE;

    /**
     * Client name
     */
    private String                  name;
    /**
     * Remote host
     */
    private String                  host;
    /**
     * Remote port
     */
    private int                     port;
    /**
     * Client status
     */
    private ClientStatusEnum        status;
    private int                     readTimeout;
    private long                    heartbeatTime;
    private long                    maxPacketBodyLen;
    private Socket                  socket;
    private ConnectHandler          connectHandler;
    private PacketHandler           packetHandler;
    private DefaultHeartbeatHandler heartbeatHandler;
    private Codec<T>                codec;
    private MessageListener<T>      messageListener;
    private BlockingQueue<T>        messageQueue = new LinkedBlockingDeque<>();
    /**
     * Debug log
     */
    private Log                     dLog;
    /**
     * Client last active time
     * - sent message
     * - received message
     * - dialed
     * - hangup
     */
    private Date                    lastActive;
    /**
     * Client lock
     */
    private ReentrantLock           lock         = new ReentrantLock();

    /**
     * Do not use no arguments constructor
     */
    private TCPClient() {}

    /**
     * Constructor
     *
     * @param host Remote server host
     * @param port Remote server port
     */
    public TCPClient(String host, int port,
                     Codec<T> codec, MessageListener<T> messageListener) {
        this.name = UUID.randomUUID().toString();
        this.host = host;
        this.port = port;
        this.status = ClientStatusEnum.PREPARING;
        this.readTimeout = DEFAULT_READ_TIMEOUT;
        this.heartbeatTime = DEFAULT_HEARTBEAT_TIME;
        this.maxPacketBodyLen = DEFAULT_MAX_BODY_LENGTH;
        this.dLog = new Slf4jLog();
        this.connectHandler = new ConnectHandler();
        this.packetHandler = new DefaultPacketHandler();
        this.heartbeatHandler = new DefaultHeartbeatHandler();
        this.codec = codec;
        this.messageListener = messageListener;
    }

    /**
     * Constructor
     *
     * @param host Remote server host
     * @param port Remote server port
     * @param name Specify client name
     */
    public TCPClient(String name, String host, int port,
                     Codec<T> codec, MessageListener<T> messageListener) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.status = ClientStatusEnum.PREPARING;
        this.readTimeout = DEFAULT_READ_TIMEOUT;
        this.heartbeatTime = DEFAULT_HEARTBEAT_TIME;
        this.maxPacketBodyLen = DEFAULT_MAX_BODY_LENGTH;
        this.dLog = new Slf4jLog();
        this.connectHandler = new ConnectHandler();
        this.packetHandler = new DefaultPacketHandler();
        this.heartbeatHandler = new DefaultHeartbeatHandler();
        this.codec = codec;
        this.messageListener = messageListener;
    }

    /**
     *
     */
    public TCPClient(String name, String host, int port, Log dLog,
                     Codec<T> codec, MessageListener<T> messageListener) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.status = ClientStatusEnum.PREPARING;
        this.readTimeout = DEFAULT_READ_TIMEOUT;
        this.heartbeatTime = DEFAULT_HEARTBEAT_TIME;
        this.maxPacketBodyLen = DEFAULT_MAX_BODY_LENGTH;
        this.dLog = dLog;
        this.connectHandler = new ConnectHandler();
        this.packetHandler = new DefaultPacketHandler();
        this.heartbeatHandler = new DefaultHeartbeatHandler();
        this.codec = codec;
        this.messageListener = messageListener;
    }

    /**
     * Full field constructor
     */
    public TCPClient(String name, String host, int port,
                     int readTimeout, long maxPacketBodyLen, Log dLog,
                     Codec<T> codec, MessageListener<T> messageListener) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.status = ClientStatusEnum.PREPARING;
        this.readTimeout = readTimeout;
        this.heartbeatTime = DEFAULT_HEARTBEAT_TIME;
        this.maxPacketBodyLen = maxPacketBodyLen;
        this.dLog = dLog;
        this.connectHandler = new ConnectHandler();
        this.packetHandler = new DefaultPacketHandler();
        this.heartbeatHandler = new DefaultHeartbeatHandler();
        this.codec = codec;
        this.messageListener = messageListener;
    }

    /**
     * Set run mode as debug
     */
    public TCPClient<T> setRunModeAsDebug(boolean on) {
        if (on) {
            RUN_MODE = RunModeEnum.DEBUG;
        } else {
            RUN_MODE = RunModeEnum.RELEASE;
        }

        return this;
    }

    /**
     * Dial to remote server
     *
     * @return Self
     * @throws IOException IO exception
     */
    public TCPClient<T> dial() throws IOException {
        lock.lock();
        try {
            this.socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), readTimeout);

            // Switch client status to running
            this.status = ClientStatusEnum.RUNNING;

            this.connectHandler.handleConnect(this);

            this.dLog.debug(format("Connect to %s:%s", host, port));
        } finally {
            lock.unlock();
        }

        return this;
    }

    /**
     * Hangup from server
     *
     * @param e Pass exception if client error hangup.
     */
    public void hangup(Exception e) {
        lock.lock();
        try {
            this.status = ClientStatusEnum.STOP;

            // wait 1 sec if has message not sent in queue.
            for (int i = 0; i < 5; i++) {
                if (messageQueue.size() > 0) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
            }

            this.socket.close();
            this.updateLastActive();

            if (e != null) {
                this.dLog.debug(e.getMessage());
            }
        } catch (InterruptedException | IOException ex) {
            this.dLog.error("Client hangup error", ex);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Send message
     *
     * @param message The message
     * @throws IllegalClientStatusException t
     */
    public void sendMessage(T message) throws IllegalClientStatusException {
        lock.lock();
        try {
            if (status != ClientStatusEnum.RUNNING) {
                throw new IllegalClientStatusException("Client on " + status.name());
            }

            messageQueue.offer(message);
            dLog.debug(format("Cli %s send msg: %s", name, message.toString()));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get client name
     */
    public String name() {
        return this.name;
    }

    /**
     * Use debug log
     */
    public Log dLog() {
        return dLog;
    }

    /**
     * Update client last active time
     * - dial
     * - hangup
     * - send message
     * - received message
     */
    public void updateLastActive() {
        this.lastActive = new Date();
    }

    /**
     * Poll a message from queue
     *
     * @param timeout How long to wait before giving up
     * @return The message
     * @throws InterruptedException throws
     */
    T pollMessage(long timeout) throws InterruptedException {
        return this.messageQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    // === === === ===
    //  Package private method
    // === === === ===

    /**
     * Get client status
     */
    ClientStatusEnum status() {
        return this.status;
    }

    /**
     * Get max body limit
     */
    long maxPacketBodyLen() {
        return this.maxPacketBodyLen;
    }

    /**
     * Get codec
     */
    Codec<T> codec() {
        return this.codec;
    }

    /**
     * Get heartbeat handler
     */
    DefaultHeartbeatHandler heartbeatHandler() {
        return this.heartbeatHandler;
    }

    /**
     * Get packet handler
     */
    PacketHandler packetHandler() {
        return this.packetHandler;
    }

    /**
     * Get message listener
     */
    MessageListener<T> messageListener() {
        return this.messageListener;
    }

    /**
     * Get socket
     */
    Socket socket() {
        return this.socket;
    }

    /**
     * Calc millis interval since client last active
     */
    long millisIntervalSinceLastActive() {
        return System.currentTimeMillis() - this.lastActive.getTime();
    }

    /**
     * Get heartbeat time
     */
    long heartbeatTime() {
        return this.heartbeatTime;
    }

}
