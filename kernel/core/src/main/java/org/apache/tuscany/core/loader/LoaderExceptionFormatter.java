package org.apache.tuscany.core.loader;

import java.io.PrintWriter;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.loader.LoaderException;

import org.apache.tuscany.host.monitor.ExceptionFormatter;
import org.apache.tuscany.host.monitor.FormatterRegistry;

/**
 * Formats {@link org.apache.tuscany.spi.loader.LoaderException} events
 *
 * @version $Rev$ $Date$
 */
public class LoaderExceptionFormatter implements ExceptionFormatter {
    private FormatterRegistry factory;

    public LoaderExceptionFormatter() {
    }

    public boolean canFormat(Class<?> type) {
        return LoaderException.class.isAssignableFrom(type);
    }

    @Autowire
    public void setRegistry(FormatterRegistry factory) {
        this.factory = factory;
    }

    @Init(eager = true)
    public void init() {
        factory.register(this);
    }

    @Destroy
    public void destroy() {
        factory.unregister(this);
    }

    public PrintWriter write(PrintWriter writer, Throwable exception) {
        assert exception instanceof LoaderException;
        LoaderException e = (LoaderException) exception;
        e.appendBaseMessage(writer);
        if (e.getLine() != LoaderException.UNDEFINED) {
            writer.write("\nLine: " + e.getLine() + "\n");
            writer.write("Column: " + e.getColumn());
        } else {
            writer.write("\n");
        }
        e.appendContextStack(writer).append("\n");
        return writer;
    }
}
