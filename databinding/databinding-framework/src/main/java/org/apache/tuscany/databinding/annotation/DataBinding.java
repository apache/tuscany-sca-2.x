package org.apache.tuscany.databinding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DataBinding annotation is used to annotate java fields, parameters and return value of a method
 * 
 */
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataBinding {
    /**
     * @return Name of the data binding
     */
    String name();
    DataContext[] context() default {};

}
