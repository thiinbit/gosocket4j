/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j.logs;

/**
 * Log
 *
 * @author thiinbit
 * @version : Log.java, v 0.1 20200321 12:17 PM thiinbit Exp $
 */
public interface Log {

    /**
     * DEBUG priority log message
     *
     * @param msg The message you would like logged.
     */
    void debug(String msg);

    ///**
    // * INFO priority log message
    // *
    // * @param tag Identify the source of a log message.
    // * @param msg The message you would like logged.
    // */
    //void info(String tag, String msg);

    /**
     * ERROR priority log message
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    void error(String msg, Throwable tr);
}