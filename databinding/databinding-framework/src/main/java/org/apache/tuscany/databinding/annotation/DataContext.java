package org.apache.tuscany.databinding.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Key/Value pair to represent the data context
 * 
 */
@Target( {})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataContext {
    String key();

    String value();

}
