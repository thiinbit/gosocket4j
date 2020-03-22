/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j.exception;

/**
 * CodecException
 *
 * @author thiinbit
 * @version : CodecException.java, v 0.1 20200322 11:49 AM thiinbit Exp $
 */
public class CodecException extends Exception {
    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}