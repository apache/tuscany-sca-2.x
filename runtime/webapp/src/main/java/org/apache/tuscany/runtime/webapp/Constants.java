package org.apache.tuscany.runtime.webapp;

/**
 * Constants used by the web application booter
 *
 * @version $Rev$ $Date$
 */
public final class Constants {

    /**
     * Context attribute to which the Tuscany runtime for this servlet context is stored.
     */
    static final String RUNTIME_ATTRIBUTE = "Tuscany.Runtime";

    /**
     * Servlet context-param name for the path to the composite to set as the webb app composite
     */
    static final String CURRENT_COMPOSITE_PATH_PARAM = "currentCompositePath";

    /**
     * Servlet context-param name for user-specified system SCDL path.
     */
    static final String SYSTEM_SCDL_PATH_PARAM = "systemScdlPath";

    /**
     * Default webapp system SCDL path.
     */
    static final String WEBAPP_SYSTEM_SCDL_PATH = "META-INF/tuscany/webapp.scdl";

    /**
     * Servlet context-param name for user-specified system SCDL path.
     */
    static final String EXTENSION_SCDL_PATH_PARAM = "extensionScdlPath";

    /**
     * Servlet context-param name for user-specified application SCDL path.
     */
    static final String APPLICATION_SCDL_PATH_PARAM = "applicationScdlPath";

    /**
     * Default application SCDL path used if no "applicationScdlPath" param is specified
     */
    static final String DEFAULT_APPLICATION_SCDL_PATH_PARAM = "/WEB-INF/default.scdl";

    /**
     * Default path for extensions if no "extensionScdlPath param is specified
     */
    static final String DEFAULT_EXTENSION_PATH_PARAM = "/META-INF/tuscany.extensions";

    /**
     * Servlet context-param name for system monitoring level. Supported values are the names of statics defined in
     * java.util.logging.Level. If absent, no monitoring will take place.
     */
    static final String SYSTEM_MONITORING_PARAM = "tuscanyMonitoringLevel";

    /**
     * Name of the context parameter that defines the directory containing bootstrap jars.
     */
    static final String BOOTDIR_PARAM = "tuscany.bootDir";

    /**
     * Name of the class to load to launch the runtime.
     */
    static final String LAUNCHER_PARAM = "tuscany.launcherClass";

    private Constants() {
    }
}
