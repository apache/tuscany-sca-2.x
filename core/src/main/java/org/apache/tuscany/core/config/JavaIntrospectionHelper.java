package org.apache.tuscany.core.config;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements various reflection-related operations
 * 
 * @version $Rev$ $Date$
 */
public class JavaIntrospectionHelper {

    private static final Class[] EMPTY_CLASS_ARRY = new Class[0];

    // ----------------------------------
    // Constructors
    // ----------------------------------

    /**
     * Hide the constructor
     */
    private JavaIntrospectionHelper() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    /**
     * Returns a collection of public, private, protected, or default fields declared by a class or one of its
     * supertypes
     */
    public static Set<Field> getAllFields(Class pClass) {
        return getAllFields(pClass, new HashSet<Field>());
    }

    /**
     * Recursively evaluates the type hierachy to return all fields on a given type
     * 
     * @spec This raises an interesting issue - do we allow injection on private supertype fields in a subtype even if
     *       they are annotated?
     */
    private static Set<Field> getAllFields(Class pClass, Set<Field> fields) {
        if (pClass == null || pClass.isArray() || Object.class.equals(pClass)) {
            return fields;
        }
        fields = getAllFields(pClass.getSuperclass(), fields);
        Field[] declaredFields = pClass.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            declaredFields[i].setAccessible(true); // ignore Java accessibility
            fields.add(declaredFields[i]);
        }
        return fields;
    }

    /**
     * Returns a collection of public, private, protected, or default methods declared by a class or one of its
     * supertypes. Note that overriden methods will not be returned in the collection (i.e. only the method override
     * will be). <p/> This method can potentially be expensive as reflection information is not cached. It is assumed
     * that this method will be used during a configuration phase.
     */
    public static Set<Method> getAllUniqueMethods(Class pClass) {
        return getAllUniqueMethods(pClass, new HashSet<Method>());
    }

    /**
     * Recursively evaluates the type hierarchy to return all unique methods
     */
    private static Set<Method> getAllUniqueMethods(Class pClass, Set<Method> methods) {
        if (pClass == null || pClass.isArray() || Object.class.equals(pClass)) {
            return methods;
        }
        // we first evaluate methods of the subclass and then move to the parent
        Method[] declaredMethods = pClass.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            if (methods.size() == 0) {
                methods.add(declaredMethods[i]);
            } else {
                List temp = new ArrayList();
                boolean matched = false;
                for (Method method : methods) {
                    // only add if not already in the set from a supclass (i.e. the
                    // method is not overrided)
                    if (exactMethodMatch(declaredMethods[i], method)) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    // TODO ignore Java accessibility
                    declaredMethods[i].setAccessible(true);
                    temp.add(declaredMethods[i]);

                }
                methods.addAll(temp);
                temp.clear();
            }
        }
        // evaluate class hierarchy - this is done last to track inherited methods
        methods = getAllUniqueMethods(pClass.getSuperclass(), methods);
        return methods;
    }

    /**
     * Finds the closest matching field with the given name, that is, a field of the exact specified type or,
     * alternately, of a supertype.
     * 
     * @param name the name of the field
     * @param type the field type
     * @param fields the collection of fields to search
     * @return the matching field or null if not found
     * @throws NoSuchFieldException if no field found
     */
    public static Field findClosestMatchingField(String name, Class type, Set<Field> fields) {
        Field candidate = null;
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                if (field.getType().equals(type)) {
                    return field; // exact match
                } else if (field.getType().isAssignableFrom(type)
                        || (field.getType().isPrimitive() && primitiveAssignable(field.getType(), type))) {
                    // We could have the situation where a field parameter is a primitive and the demarshalled value is
                    // an object counterpart (e.g. Integer and int)
                    // @spec issue
                    // either an interface or super class, so keep a reference until
                    // we know there are no closer types
                    candidate = field;
                }
            }
        }
        if (candidate != null) {
            return candidate;
        } else {
            return null;
        }
    }

    /**
     * Finds the closest matching method with the given name, that is, a method taking the exact parameter types or,
     * alternately, parameter supertypes.
     * 
     * @param name the name of the method
     * @param types the method parameter types
     * @param methods the collection of methods to search
     * @return the matching method or null if not found
     * @throws NoSuchFieldException if no field found
     */
    public static Method findClosestMatchingMethod(String name, Class[] types, Set<Method> methods) {
        if (types == null) {
            types = EMPTY_CLASS_ARRY;
        }
        Method candidate = null;
        for (Method method : methods) {
            if (method.getName().equals(name) && method.getParameterTypes().length == types.length) {
                Class[] params = method.getParameterTypes();
                boolean disqualify = false;
                boolean exactMatch = true;
                for (int i = 0; i < params.length; i++) {
                    if (!params[i].equals(types[i]) && !params[i].isAssignableFrom(types[i])) {
                        // no match
                        disqualify = true;
                        exactMatch = false;
                        break;
                    } else if (!params[i].equals(types[i]) && params[i].isAssignableFrom(types[i])) {
                        // not exact match
                        exactMatch = false;
                    }
                }
                if (disqualify) {
                    continue;
                } else if (exactMatch) {
                    return method;
                } else {
                    candidate = method;
                }
            }
        }
        if (candidate != null) {
            return candidate;
        } else {
            return null;
        }
    }

    /**
     * Returns a field or method defined in the given class or its superclasses matching a literal name and parameter
     * types <p/> This method can potentially be expensive as reflection information is not cached. It is assumed that
     * this method will be used during a configuration phase.
     * 
     * @param clazz the class to introspect
     * @param propertName the literal name of the property (i.e. JavaBean conventions are not applied)
     * @param paramTypes the parameter types for a method or null for fields or methods with no parameters
     * @return the field, method or null
     */
    public static AccessibleObject getBeanProperty(Class clazz, String propertName, Class[] paramTypes) {

        Set<Method> methods = getAllUniqueMethods(clazz);
        for (Method method : methods) {
            if (method.getName().equals(propertName)) {
                Class[] types = method.getParameterTypes();
                if (types.length == 0 && paramTypes == null) {
                    return method;
                } else if (types.length != 0 && paramTypes == null) {
                    break;
                } else if (types.length == paramTypes.length) {
                    for (int n = 0; n < types.length - 1; n++) {
                        if (!types[n].equals(paramTypes[n]) || !types[n].isAssignableFrom(paramTypes[n])) {
                            break;
                        }
                    }
                    return method;
                }
            }
        }

        Set<Field> fields = getAllFields(clazz);
        for (Field field : fields) {
            if (field.getName().equals(propertName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Determines if two methods "match" - that is, they have the same method names and exact parameter types (one is
     * not a supertype of the other)
     */
    public static boolean exactMethodMatch(Method method1, Method method2) {
        if (!method1.getName().equals(method2.getName())) {
            return false;
        }
        Class[] types1 = method1.getParameterTypes();
        Class[] types2 = method2.getParameterTypes();
        if (types1.length == 0 && types2.length == 0) {
            return true;
        } else if (types1.length == types2.length) {
            for (int n = 0; n < types1.length; n++) {
                if (!types1[n].equals(types2[n])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static Constructor getDefaultConstructor(Class clazz) throws NoSuchMethodException {
        return clazz.getConstructor((Class[]) null);
    }

    /**
     * Loads a class corresponding to the class name using the current context class loader.
     * 
     * @throws ClassNotFoundException if the class was not found on the classpath
     */
    public static Class loadClass(String pName) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return Class.forName(pName, true, loader);
    }

    /**
     * Returns the simple name of a class - i.e. the class name devoid of its package qualifier
     * 
     * @param implClass
     * @return
     */
    public static String getBaseName(Class<?> implClass) {
        String baseName = implClass.getName();
        int lastDot = baseName.lastIndexOf('.');
        if (lastDot != -1) {
            baseName = baseName.substring(lastDot + 1);
        }
        return baseName;
    }

    public static boolean isImmutable(Class clazz) {
        return (String.class == clazz || clazz.isPrimitive() || Number.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz) || Byte.class
                .isAssignableFrom(clazz));
    }

    /**
     * Takes a property name and converts it to a getter method name according to JavaBean conventions. For example,
     * property <code>foo<code> is returned as <code>getFoo</code>
     */
    public static String toGetter(String name) {
        return "get" + name.toUpperCase().substring(0, 1) + name.substring(1);
    }

    /**
     * Takes a setter or getter method name and converts it to a property name according to JavaBean conventions. For
     * example, <code>setFoo(var)</code> is returned as property <code>foo<code>
     */
    public static String toPropertyName(String name) {
        return name.substring(3, 4).toLowerCase() + name.substring(4);
    }

    /**
     * Takes a property name and converts it to a setter method name according to JavaBean conventions. For example, the
     * property <code>foo<code> is returned as <code>setFoo(var)</code>
     */
    public static String toSetter(String name) {
        return "set" + name.toUpperCase().substring(0, 1) + name.substring(1);
    }

    /**
     * Compares a two types, assuming one is a primitive, to dtermine if the other is its object counterpart
     */
    private static boolean primitiveAssignable(Class memberType, Class param) {
        if (memberType == Integer.class) {
            return (param == Integer.TYPE);
        } else if (memberType == Double.class) {
            return (param == Double.TYPE);
        } else if (memberType == Float.class) {
            return (param == Float.TYPE);
        } else if (memberType == Short.class) {
            return (param == Short.TYPE);
        } else if (memberType == Character.class) {
            return (param == Character.TYPE);
        } else if (memberType == Boolean.class) {
            return (param == Boolean.TYPE);
        } else if (memberType == Byte.class) {
            return (param == Byte.TYPE);
        } else if (param == Integer.class) {
            return (memberType == Integer.TYPE);
        } else if (param == Double.class) {
            return (memberType == Double.TYPE);
        } else if (param == Float.class) {
            return (memberType == Float.TYPE);
        } else if (param == Short.class) {
            return (memberType == Short.TYPE);
        } else if (param == Character.class) {
            return (memberType == Character.TYPE);
        } else if (param == Boolean.class) {
            return (memberType == Boolean.TYPE);
        } else if (param == Byte.class) {
            return (memberType == Byte.TYPE);
        } else {
            return false;
        }
    }
}
