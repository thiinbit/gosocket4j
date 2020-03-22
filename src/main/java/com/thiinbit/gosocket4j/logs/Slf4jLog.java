/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j.logs;

import com.thiinbit.gosocket4j.TCPClient;
import com.thiinbit.gosocket4j.enums.RunModeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DebugLog
 *
 * @author thiinbit
 * @version : DebugLog.java, v 0.1 20200321 12:34 PM thiinbit Exp $
 */
public class Slf4jLog implements Log {
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(Log.class);
    /**
     * Log prefix
     */
    public static final  String PREFIX = "[Gosocket4j]";

    /**
     *
     */
    public void debug(String msg) {
        if (TCPClient.RUN_MODE == RunModeEnum.DEBUG) {
            logger.debug("{} {}", PREFIX, msg);
        }
    }

    /**
     *
     */
    public void error(String msg, Throwable tr) {
        logger.error(PREFIX + " " + msg, tr);
    }
}