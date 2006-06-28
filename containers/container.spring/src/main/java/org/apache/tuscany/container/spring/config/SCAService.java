package org.apache.tuscany.container.spring.config;

/**
 * Bean impl for the Spring sca:service XML configuration element
 * @version $$Rev: $$ $$Date: $$
 */

public class SCAService {
    private String name;
    private String type;
    private String target;

    public SCAService( String name, String type, String target ) {
        this.name = name;
        this.type = type;
        this.target = target;
    }

    public String getName()   { return name; }
    public String getType()   { return type; }
    public String getTarget() { return target; }
}
