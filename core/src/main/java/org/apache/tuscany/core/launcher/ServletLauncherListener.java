package org.apache.tuscany.core.launcher;

import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.bootstrap.ComponentNames;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

/**
 * Launcher for runtime environment that loads info from servlet context params.
 * This listener manages one top-level CompositeContext per servlet context; the
 * lifecycle of that CompositeContext corresponds to the the lifecycle of the
 * associated servlet context.
 *
 * @version $$Rev: $$ $$Date: $$
 */

public class ServletLauncherListener implements ServletContextListener {
    /**
     * Servlet context-param name for user-specified system SCDL path.
     */
    public static final String SYSTEM_SCDL_PATH_PARAM = "systemScdlPath";
    /**
     * Servlet context-param name for user-specified application SCDL path.
     */
    public static final String APPLICATION_SCDL_PATH_PARAM = "applicationScdlPath";

    /**
     * Default application SCDL path used if no "applicationScdlPath" param is specified
     *
     * REVIEW: this doesn't work as expected right now because we are using the webapp classloader
     * directly, which doesn't include the root of the webapp.
     */
    public static final String DEFAULT_APPLICATION_SCDL_PATH = "WEB-INF/default.scdl";

    /**
     * Context attribute to which application root component (of type CompositeComponent<?>)
     * will be bound to on successful application initialization.  May be null on failure.
     */
    public static final String APPLICATION_ROOT_COMPONENT_ATTRIBUTE = ComponentNames.TUSCANY_ROOT;

    /**
     * Context attribute to which application root context (of type CompositeContext)
     * will be bound to on successful application initialization.  May be null on failure.
     */
    public static final String APPLICATION_ROOT_CONTEXT_ATTRIBUTE = APPLICATION_ROOT_COMPONENT_ATTRIBUTE + ".context";

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        // Read optional path to system SCDL from context-param
        String systemScdlPath = servletContext.getInitParameter(SYSTEM_SCDL_PATH_PARAM);
        if (systemScdlPath == null) {
            systemScdlPath = Launcher.METAINF_SYSTEM_SCDL_PATH;
        }

        // Read optional path to application SCDL from context-param
        String applicationScdlPath = servletContext.getInitParameter(APPLICATION_SCDL_PATH_PARAM);
        if (applicationScdlPath == null) {
            applicationScdlPath = DEFAULT_APPLICATION_SCDL_PATH;
        }

        Launcher launcher = new Launcher();

        // REVIEW: Not sure how reliable it is to rely on the thread context classloader as having
        // reasonable semantics across a variety of servlet containers.. if "not very", the thread
        // context loader works for Tomcat, so perhaps this class needs to become container-specific.
        launcher.setApplicationLoader(Thread.currentThread().getContextClassLoader());

        CompositeComponent<?> component = null;
        CompositeContextImpl context = null;

        try {
            launcher.bootRuntime(systemScdlPath);
            component = launcher.bootApplication(applicationScdlPath);
            component.start();
            context = new CompositeContextImpl(component);
            context.start();
        } catch (LoaderException le) {
            // TODO: Need proper logging infrastructure here
            // TODO: stash exception info in attributes?
            le.printStackTrace();
        }

        servletContext.setAttribute(APPLICATION_ROOT_COMPONENT_ATTRIBUTE, component);
        servletContext.setAttribute(APPLICATION_ROOT_COMPONENT_ATTRIBUTE, context);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();

        CompositeComponent<?> component =
            (CompositeComponent<?>) servletContext.getAttribute(APPLICATION_ROOT_COMPONENT_ATTRIBUTE);

        // REVIEW: may be ok to use CurrentCompositeContext.getContext(), but this feels safer.
        CompositeContextImpl context =
            (CompositeContextImpl) servletContext.getAttribute(APPLICATION_ROOT_CONTEXT_ATTRIBUTE);

        if (component != null) {
            component.stop();
        }

        if (context != null) {
            context.stop();
        }
    }
}
