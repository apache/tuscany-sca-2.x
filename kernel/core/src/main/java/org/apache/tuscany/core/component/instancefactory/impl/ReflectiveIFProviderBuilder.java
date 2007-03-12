package org.apache.tuscany.core.component.instancefactory.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.ReflectiveInstanceFactoryProvider;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderException;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSite;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteType;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;

/**
 * IF provider builder for reflective IF provider.
 */
public class ReflectiveIFProviderBuilder extends
    AbstractIFProviderBuilder<ReflectiveInstanceFactoryProvider, ReflectiveIFProviderDefinition> {

    @Override
    protected Class<ReflectiveIFProviderDefinition> getIfpdClass() {
        return ReflectiveIFProviderDefinition.class;
    }

    @SuppressWarnings("unchecked")
    public ReflectiveInstanceFactoryProvider build(ReflectiveIFProviderDefinition ifpd, ClassLoader cl)
        throws IFProviderBuilderException {

        try {

            Class implClass = cl.loadClass(ifpd.getImplementationClass());

            Constructor ctr = getConstructor(ifpd, cl, implClass);

            Method initMethod = getInitMethod(ifpd, implClass);

            Method destroyMethod = getDestroyMethod(ifpd, implClass);

            List<URI> ctrInjectSites = ifpd.getConstructorNames();

            Map<URI, Member> injectionSites = getInjectionSites(ifpd, implClass);

            return new ReflectiveInstanceFactoryProvider(ctr, ctrInjectSites, injectionSites, initMethod, destroyMethod);

        } catch (ClassNotFoundException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (NoSuchMethodException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (NoSuchFieldException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (IntrospectionException ex) {
            throw new IFProviderBuilderException(ex);
        }
    }

    /*
     * Get injection sites.
     */
    private Map<URI, Member> getInjectionSites(ReflectiveIFProviderDefinition ifpd, Class implClass)
        throws NoSuchFieldException, IntrospectionException, IFProviderBuilderException {
        Map<URI, Member> injectionSites = new HashMap<URI, Member>();
        for (InjectionSite injectionSite : ifpd.getInjectionSites()) {
            String name = injectionSite.getName();
            Member member = null;
            if (injectionSite.getType() == InjectionSiteType.FIELD) {
                member = implClass.getDeclaredField(name);
            } else if (injectionSite.getType() == InjectionSiteType.METHOD) {
                for (PropertyDescriptor pd : Introspector.getBeanInfo(implClass).getPropertyDescriptors()) {
                    if (name.equals(pd.getName())) {
                        member = pd.getWriteMethod();
                    }
                }
            }
            if (member == null) {
                throw new IFProviderBuilderException("Unknown injection site " + name);
            }
            injectionSites.put(injectionSite.getUri(), member);
        }
        return injectionSites;
    }

    /*
     * Get destroy method.
     */
    private Method getDestroyMethod(ReflectiveIFProviderDefinition ifpd, Class implClass) throws NoSuchMethodException {
        Method destroyMethod = null;
        String destroyMethodName = ifpd.getDestroyMethod();
        if (destroyMethod != null) {
            destroyMethod = implClass.getDeclaredMethod(destroyMethodName);
        }
        return destroyMethod;
    }

    /*
     * Get init method.
     */
    private Method getInitMethod(ReflectiveIFProviderDefinition ifpd, Class implClass) throws NoSuchMethodException {
        Method initMethod = null;
        String initMethodName = ifpd.getInitMethod();
        if (initMethodName != null) {
            initMethod = implClass.getDeclaredMethod(initMethodName);
        }
        return initMethod;
    }

    /*
     * Gets the matching constructor.
     */
    private Constructor getConstructor(ReflectiveIFProviderDefinition ifpd, ClassLoader cl, Class implClass)
        throws ClassNotFoundException, NoSuchMethodException {
        Class[] ctrArgs = new Class[ifpd.getConstructorArguments().size()];
        int i = 0;
        for (String ctrArgClass : ifpd.getConstructorArguments()) {
            ctrArgs[i++] = cl.loadClass(ctrArgClass);
        }
        Constructor ctr = implClass.getDeclaredConstructor(ctrArgs);
        return ctr;
    }

}
