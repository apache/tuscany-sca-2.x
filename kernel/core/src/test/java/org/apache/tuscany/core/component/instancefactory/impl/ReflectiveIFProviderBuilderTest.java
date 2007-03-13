package org.apache.tuscany.core.component.instancefactory.impl;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.core.component.ReflectiveInstanceFactoryProvider;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSite;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteType;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;

public class ReflectiveIFProviderBuilderTest extends TestCase {
    
    public void testBuild() throws Exception {
        
        ReflectiveIFProviderDefinition def = new ReflectiveIFProviderDefinition();
        
        def.setImplementationClass("org.apache.tuscany.core.component.instancefactory.impl.Foo");
        def.setDestroyMethod("destroy");
        def.setInitMethod("init");
        
        def.addConstructorArgument("java.lang.String");
        def.addConstructorArgument("java.lang.Long");
        
        def.addConstructorNames(new URI("a#b"));
        def.addConstructorNames(new URI("c#d"));
        
        InjectionSite injectionSite = new InjectionSite();
        injectionSite.setName("xyz");
        injectionSite.setUri(new URI("k#m"));
        injectionSite.setInjectionClass("org.apache.tuscany.core.component.instancefactory.impl.Bar");
        injectionSite.setType(InjectionSiteType.FIELD);
        
        def.addInjectionSite(injectionSite);
        
        injectionSite = new InjectionSite();
        injectionSite.setName("abc");
        injectionSite.setUri(new URI("x#y"));
        injectionSite.setInjectionClass("org.apache.tuscany.core.component.instancefactory.impl.Bar");
        injectionSite.setType(InjectionSiteType.METHOD);
        
        def.addInjectionSite(injectionSite);
        
        ReflectiveIFProviderBuilder builder = new ReflectiveIFProviderBuilder();
        ClassLoader cl = getClass().getClassLoader();
        
        
        ReflectiveInstanceFactoryProvider provider = builder.build(def, cl);
        assertNotNull(provider);
    }

}
