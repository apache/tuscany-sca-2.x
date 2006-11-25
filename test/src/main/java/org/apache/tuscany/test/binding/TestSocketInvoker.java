package org.apache.tuscany.test.binding;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Responsible for serializing an operation parameter flowing the invocation through the socket
 *
 * @version $Rev$ $Date$
 */
public class TestSocketInvoker implements TargetInvoker {
    private String host;
    private int port;
    private String operation;

    private boolean cacheable;

    public TestSocketInvoker(String host, int port, String operation) {
        this.host = host;
        this.port = port;
        this.operation = operation;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return isCacheable();
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody(), TargetInvoker.NONE);
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }

    /**
     * Sends the payload over a socket
     */
    public Object invokeTarget(final Object object, final short sequence) throws InvocationTargetException {
        int argn;
        if (object == null) {
            argn = 0;
        } else if (!object.getClass().isArray()) {
            argn = 1;
        } else {
            argn = ((Object[])object).length;
        }

        Socket socket = null;
        ObjectOutputStream os = null;
        ObjectInputStream is = null;
        try {
            socket = new Socket(host, port);
            os = new ObjectOutputStream(socket.getOutputStream());
            os.writeUTF(operation);
            os.writeInt(argn);
            for (int i=0; i<argn; i++) {
                if (!object.getClass().isArray()) {
                    os.writeObject(object);
                }
                else {
                    os.writeObject(((Object[])object)[i]);
                }
            }
            os.flush();
            is = new ObjectInputStream(socket.getInputStream());
            return is.readObject();
        } catch (ClassNotFoundException e) {
            throw new InvocationTargetException(e);
        } catch (UnknownHostException e) {
            throw new InvocationTargetException(e);
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
