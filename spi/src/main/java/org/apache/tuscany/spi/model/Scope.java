package org.apache.tuscany.spi.model;

/**
 * The default implementation scopes supported by assemblies.
 */
public enum Scope {
    STATELESS,
    REQUEST,
    SESSION,
    MODULE,
    COMPOSITE,
    UNDEFINED

}
