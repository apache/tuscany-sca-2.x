package org.apache.tuscany.test.binding;

import org.apache.tuscany.spi.model.BindingDefinition;

/**
 * A simple socket-based binding. Service operations may onyl take one parameter that is <code>Serializable</code>
 *
 * @version $$Rev$$ $$Date$$
 */
public class TestSocketBindingDefinition extends BindingDefinition {
    private String host;
    private int port;

    public TestSocketBindingDefinition(String host, int port) {
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
