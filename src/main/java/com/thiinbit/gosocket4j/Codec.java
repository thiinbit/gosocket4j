/*
 * thiinbit
 * Copyright (c) 2020 All Rights Reserved.
 */
package com.thiinbit.gosocket4j;

import com.thiinbit.gosocket4j.exception.CodecException;

/**
 * Codec
 *
 * @author thiinbit
 * @version : Codec.java, v 0.1 20200322 12:20 AM thiinbit Exp $
 */
public interface Codec<T> {
    byte[] encode(T message) throws CodecException;

    T decode(byte[] body) throws CodecException;
}