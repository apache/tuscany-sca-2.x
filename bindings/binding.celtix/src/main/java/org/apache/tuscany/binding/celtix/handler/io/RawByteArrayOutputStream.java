package org.apache.tuscany.binding.celtix.handler.io;

import java.io.ByteArrayOutputStream;

/**
 * Just to allow raw access to the byte[] to avoid a copy
 */
class RawByteArrayOutputStream extends ByteArrayOutputStream {
    public byte[] getBytes() {
        return buf;
    }
}