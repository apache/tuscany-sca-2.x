package org.apache.tuscany.core.loader;

import java.util.logging.LogRecord;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.loader.LoaderException;

import org.apache.tuscany.host.MonitorFactory;
import org.apache.tuscany.host.monitor.ExceptionFormatter;
import org.apache.tuscany.host.monitor.FormatterRegistry;

/**
 * Formats {@link org.apache.tuscany.spi.loader.LoaderException} events
 *
 * @version $Rev$ $Date$
 */
public class LoaderExceptionFormatter implements ExceptionFormatter {
    private MonitorFactory factory;

    public LoaderExceptionFormatter() {
    }

    public boolean canFormat(Class<?> type) {
        return LoaderException.class.isAssignableFrom(type);
    }

    @Autowire(required = false)
    public void setRegistry(MonitorFactory factory) {
        this.factory = factory;
    }

    @Init(eager = true)
    public void init() {
        if (factory instanceof FormatterRegistry) {
            ((FormatterRegistry) factory).register(this);
        }
    }

    @Destroy
    public void destroy() {
        if (factory instanceof FormatterRegistry) {
            ((FormatterRegistry) factory).unregister(this);
        }
    }

    public LogRecord write(LogRecord record, Throwable exception) {
        assert exception instanceof LoaderException;
        LoaderException e = (LoaderException) exception;
        StringBuilder b = new StringBuilder(256);
        e.appendBaseMessage(b);
        if (e.getLine() != LoaderException.UNDEFINED) {
            b.append("\n").append("Line: ").append(e.getLine()).append("\n");
            b.append("Column: ").append(e.getColumn()).append("\n");
        } else {
            b.append("\n");
        }
        e.appendContextStack(b);
        if (b.length() >= 1) {
            record.setMessage(b.toString());
        }
        record.setThrown(exception);
        return record;
    }
}
