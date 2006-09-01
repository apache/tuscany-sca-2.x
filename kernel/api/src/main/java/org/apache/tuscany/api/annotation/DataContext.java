package org.apache.tuscany.api.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * A key/value pair to represent information pertaining to a {@link DataType}
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface DataContext {
    /**
     * @return key for the context entry
     */
    String key();

    /**
     * @return key for the context value
     */
    String value();

}
