package org.apache.tuscany.test.binding;

import org.apache.tuscany.spi.model.Binding;

/**
 * A simple socket-based binding. Service operations may onyl take one parameter that is <code>Serializable</code>
 *
 * @version $$Rev$$ $$Date$$
 */
public class TestSocketBinding extends Binding {
    private String host;
    private int port;

    public TestSocketBinding(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
