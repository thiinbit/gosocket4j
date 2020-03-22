/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j;

/**
 * MessageListener
 *
 * @author thiinbit
 * @version : MessageListener.java, v 0.1 20200321 8:17 PM thiinbit Exp $
 */
public interface MessageListener<T> {
    /**
     * On message received
     *
     * @param message The received message
     */
    void onMessage(T message, TCPClient<T> cli);
}