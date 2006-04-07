package org.apache.tuscany.core.system.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * A system annotation to inject an autowired instance
 * 
 * @version $Rev$ $Date$
 */
@Target( { METHOD, FIELD })
@Retention(RUNTIME)
public @interface Autowire {
    
    public boolean required() default true;

}
