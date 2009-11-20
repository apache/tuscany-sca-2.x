/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.stripes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.exception.StripesRuntimeException;
import net.sourceforge.stripes.util.Log;
import net.sourceforge.stripes.util.ReflectUtil;

import org.apache.tuscany.sca.implementation.web.runtime.utils.ContextHelper;
import org.oasisopen.sca.annotation.Reference;

/**
 * <p>Static helper class that is used to lookup SCA references and inject them into objects
 * (often ActionBeans). Is capable of injecting references through setter methods (property access)
 * and also through direct field access if the security policy allows it. Methods and fields
 * must be annotated using the SCA {@code @Reference} annotation.</p>
 *
 * <p>Methods and fields may be public, protected, package-access or private. If they are not
 * public an attempt is made to call {@link Method#setAccessible(boolean)} in order to make
 * them accessible from this class.  If the attempt fails, an exception will be thrown.</p>
 *
 * <p>Method names can take any form.  For example {@code setSomeBean(Bean b)} or
 * {@code someBean(bean b)}. In both cases, if a specific Reference name is not supplied,
 * the default name of {@code someBean} will be used.</p>
 *
 * <p>The value of the {@code @Reference} annotation should be the reference on the 
 * SCA component with an {@code <implementation.web>} componentType. 
 *
 * <p>The first time that any of the injection methods in this class is called with a specific type
 * of object, the object's class is examined for annotated fields and methods. The discovered
 * fields and methods are then cached for future usage.</p>
 *
 * Created for Tuscany from the Stripes SpringHelper written by Dan Hayes and Tim Fennell
 */
public class TuscanyHelper {
    private static final Log log = Log.getInstance(TuscanyHelper.class);

    /** Lazily filled in map of Class to methods annotated with Reference. */
    private static Map<Class<?>, Collection<Method>> methodMap =
            new ConcurrentHashMap<Class<?>, Collection<Method>>();

    /** Lazily filled in map of Class to fields annotated with Reference. */
    private static Map<Class<?>, Collection<Field>> fieldMap =
            new ConcurrentHashMap<Class<?>, Collection<Field>>();

    /**
     * Injects SCA References using the ComponentContext that is
     * derived from the ServletContext, which is in turn looked up using the
     * ActionBeanContext.
     *
     * @param bean    the object into which to inject SCA reference
     * @param context the ActionBeanContext represented by the current request
     */
    public static void injectBeans(Object bean, ActionBeanContext context) {
        injectBeans(bean, StripesFilter.getConfiguration().getServletContext());
    }

    /**
     * Looks for all methods and fields annotated with {@code @Reference} and attempts
     * to lookup and inject a managed bean into the field/property. If any annotated
     * element cannot be injected an exception is thrown.
     *
     * @param bean the bean into which to inject SCA reference
     * @param ctx the SCA ComponentContext
     */
    public static void injectBeans(Object bean, ServletContext ctx) {
        // First inject any values using annotated methods
        for (Method m : getMethods(bean.getClass())) {
            try {
                Reference scaReference = m.getAnnotation(Reference.class);
                boolean nameSupplied = !"".equals(scaReference.name());
                String name = nameSupplied ? scaReference.name() : methodToPropertyName(m);
                Class<?> beanType = m.getParameterTypes()[0];
                Object managedBean = findReference(ctx, name, beanType, !nameSupplied);
                m.invoke(bean, managedBean);
            }
            catch (Exception e) {
                throw new StripesRuntimeException("Exception while trying to lookup and inject " +
                    "an SCA Reference into a bean of type " + bean.getClass().getSimpleName() +
                    " using method " + m.toString(), e);
            }
        }

        // And then inject any properties that are annotated
        for (Field f : getFields(bean.getClass())) {
            try {
                Reference scaReference = f.getAnnotation(Reference.class);
                boolean nameSupplied = !"".equals(scaReference.name());
                String name = nameSupplied ? scaReference.name() : f.getName();
                Object managedBean = findReference(ctx, name, f.getType(), !nameSupplied);
                f.set(bean, managedBean);
            }
            catch (Exception e) {
                throw new StripesRuntimeException("Exception while trying to lookup and inject " +
                    "an SCA Referenceinto a bean of type " + bean.getClass().getSimpleName() +
                    " using field access on field " + f.toString(), e);
            }
        }
    }

    /**
     * Fetches the methods on a class that are annotated with Reference. The first time it
     * is called for a particular class it will introspect the class and cache the results.
     * All non-overridden methods are examined, including protected and private methods.
     * If a method is not public an attempt it made to make it accessible - if it fails
     * it is removed from the collection and an error is logged.
     *
     * @param clazz the class on which to look for Reference annotated methods
     * @return the collection of methods with the annotation
     */
    protected static Collection<Method> getMethods(Class<?> clazz) {
        Collection<Method> methods = methodMap.get(clazz);
        if (methods == null) {
            methods = ReflectUtil.getMethods(clazz);
            Iterator<Method> iterator = methods.iterator();

            while (iterator.hasNext()) {
                Method method = iterator.next();
                if (!method.isAnnotationPresent(Reference.class)) {
                    iterator.remove();
                }
                else {
                    // If the method isn't public, try to make it accessible
                    if (!method.isAccessible()) {
                        try {
                            method.setAccessible(true);
                        }
                        catch (SecurityException se) {
                            throw new StripesRuntimeException(
                                "Method " + clazz.getName() + "." + method.getName() + "is marked " +
                                "with @Reference and is not public. An attempt to call " +
                                "setAccessible(true) resulted in a SecurityException. Please " +
                                "either make the method public or modify your JVM security " +
                                "policy to allow Stripes to setAccessible(true).", se);
                        }
                    }

                    // Ensure the method has only the one parameter
                    if (method.getParameterTypes().length != 1) {
                        throw new StripesRuntimeException(
                            "A method marked with @Reference must have exactly one parameter: " +
                            "the bean to be injected. Method [" + method.toGenericString() + "] has " +
                            method.getParameterTypes().length + " parameters."
                        );
                    }
                }
            }

            methodMap.put(clazz, methods);
        }

        return methods;
    }

    /**
     * Fetches the fields on a class that are annotated with Refernece. The first time it
     * is called for a particular class it will introspect the class and cache the results.
     * All non-overridden fields are examined, including protected and private fields.
     * If a field is not public an attempt it made to make it accessible - if it fails
     * it is removed from the collection and an error is logged.
     *
     * @param clazz the class on which to look for Reference annotated fields
     * @return the collection of methods with the annotation
     */
    protected static Collection<Field> getFields(Class<?> clazz) {
        Collection<Field> fields = fieldMap.get(clazz);
        if (fields == null) {
            fields = ReflectUtil.getFields(clazz);
            Iterator<Field> iterator = fields.iterator();

            while (iterator.hasNext()) {
                Field field = iterator.next();
                if (!field.isAnnotationPresent(Reference.class)) {
                    iterator.remove();
                }
                else if (!field.isAccessible()) {
                    // If the field isn't public, try to make it accessible
                    try {
                        field.setAccessible(true);
                    }
                    catch (SecurityException se) {
                        throw new StripesRuntimeException(
                            "Field " + clazz.getName() + "." + field.getName() + "is marked " +
                            "with @Reference and is not public. An attempt to call " +
                            "setAccessible(true) resulted in a SecurityException. Please " +
                            "either make the field public, annotate a public setter instead " +
                            "or modify your JVM security policy to allow Stripes to " +
                            "setAccessible(true).", se);
                    }
                }
            }

            fieldMap.put(clazz, fields);
        }

        return fields;
    }

    /**
     * Looks up an SCA Reference from a ComponentContext. First looks for a bean
     * with name specified. If no such bean exists, looks for a bean by type. If there is
     * only one bean of the appropriate type, it is returned. If zero or more than one bean
     * of the correct type exists, an exception is thrown.
     *
     * @param ctx the SCA ComponentContext
     * @param name the name of the reference to look for
     * @param type the type of bean to look for
     * @param allowFindByType true to indicate that finding a bean by type is acceptable
     *        if find by name fails.
     * @exception RuntimeException various subclasses of RuntimeException are thrown if it
     *            is not possible to find a unique matching bean in the ComponentContext given
     *            the constraints supplied.
     */
    protected static Object findReference(ServletContext ctx,
                                           String name,
                                           Class<?> type,
                                           boolean allowFindByType) {
        // First try to lookup using the name provided
            Object bean =  ContextHelper.getReference(name, type, ctx);
            if (bean == null) {
                throw new StripesRuntimeException("no reference defined:" + name);
            }

            log.debug("Found sca reference with name [", name, "] and type [",
                      bean.getClass().getName(), "]");
            return bean;

// TODO: Support get by type (sca autowire?)            
            
//        // If we got here then we didn't find a bean yet, try by type
//        String[] beanNames = ctx.getBeanNamesForType(type);
//        if (beanNames.length == 0) {
//            throw new StripesRuntimeException(
//                "Unable to find SpringBean with name [" + name + "] or type [" +
//                type.getName() + "] in the Spring application context.");
//        }
//        else if (beanNames.length > 1) {
//            throw new StripesRuntimeException(
//                "Unable to find SpringBean with name [" + name + "] or unique bean with type [" +
//                type.getName() + "] in the Spring application context. Found " + beanNames.length +
//                "beans of matching type.");
//        }
//        else {
//            log.warn("Found unique SpringBean with type [" + type.getName() + "]. Matching on ",
//                     "type is a little risky so watch out!");
//            return ctx.getBean(beanNames[0], type);
//        }
    }

    /**
     * A slightly unusual, and somewhat "loose" conversion of a method name to a property
     * name. Assumes that the name is in fact a mutator for a property and will do the
     * usual {@code setFoo} to {@code foo} conversion if the method follows the normal
     * syntax, otherwise will just return the method name.
     *
     * @param m the method to determine the property name of
     * @return a String property name
     */
    protected static String methodToPropertyName(Method m) {
        String name = m.getName();
        if (name.startsWith("set") && name.length() > 3) {
            String ret = name.substring(3,4).toLowerCase();
            if (name.length() > 4) ret += name.substring(4);
            return ret;
        }
        else {
            return name;
        }
    }
}
