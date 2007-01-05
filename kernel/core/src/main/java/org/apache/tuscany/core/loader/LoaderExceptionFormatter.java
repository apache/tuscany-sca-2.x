package org.apache.tuscany.core.loader;

import java.io.PrintWriter;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.loader.LoaderException;

import org.apache.tuscany.host.monitor.ExceptionFormatter;
import org.apache.tuscany.host.monitor.FormatterRegistry;

/**
 * Formats {@link org.apache.tuscany.spi.loader.LoaderException} events
 *
 * @version $Rev$ $Date$
 */
@EagerInit
public class LoaderExceptionFormatter implements ExceptionFormatter {
    private FormatterRegistry factory;

    public LoaderExceptionFormatter(@Autowire FormatterRegistry factory) {
        this.factory = factory;
        factory.register(this);
    }

    public boolean canFormat(Class<?> type) {
        return LoaderException.class.isAssignableFrom(type);
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
