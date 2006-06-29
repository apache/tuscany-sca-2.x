package org.osoa.sca;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;



public class CompositeContextImpl implements CompositeContext {
    final protected CompositeComponent<?> composite;
    public CompositeContextImpl(final CompositeComponent<?> composite, final ClassLoader appclass) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, SecurityException, NoSuchMethodException{
       this.composite= composite; 
//       LauncherCurrentCompositeContext.setContext(this);
       Class<?> compClass = appclass.loadClass("org.osoa.sca.LauncherCurrentCompositeContext");
       
       Method method = compClass.getDeclaredMethod("setContext", new Class[]{org.osoa.sca.CompositeContext.class} );
       method.invoke(null, new Object[]{this});
//       Method[] methods = compClass.getDeclaredMethods();
//       for(Method method : methods){
//           String name= method.getName();
//           System.err.println("methods{" +name +"}");
//           if("setContext".equals(name) ){
//               method.invoke(null, new Object[]{composite});
//               break;
//           }
//           
//       }
//       
       
       
    }

    public ServiceReference createServiceReferenceForSession(Object arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceReference createServiceReferenceForSession(Object arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCompositeName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCompositeURI() {
        // TODO Auto-generated method stub
        return null;
    }

    public RequestContext getRequestContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> T locateService(Class<T> arg0, String arg1) {
        // TODO Auto-generated method stub
        return (T) composite.getChild(arg1).getServiceInstance();
    }

    public ServiceReference newSession(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public ServiceReference newSession(String arg0, Object arg1) {
        // TODO Auto-generated method stub
        return null;
    }

}
