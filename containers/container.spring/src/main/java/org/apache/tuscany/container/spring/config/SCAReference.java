package org.apache.tuscany.container.spring.config;

/**
 * Bean impl for the Spring sca:reference XML configuration element
 * @version $$Rev: $$ $$Date: $$
 */

public class SCAReference {
    private String name;
    private String type;
    private String defaultService;  // optional

    public SCAReference(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName()    { return name; }
    public String getType()    { return type; }

    public String getDefault()         { return defaultService; }
    public void   setDefault(String s) { this.defaultService = s; }
}
