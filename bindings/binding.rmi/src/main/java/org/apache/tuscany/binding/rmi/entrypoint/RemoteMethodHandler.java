package org.apache.tuscany.binding.rmi.entrypoint;

import java.lang.reflect.Method;

import org.apache.tuscany.core.context.EntryPointContext;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class RemoteMethodHandler implements MethodInterceptor 
{
    public static final String FINALIZE_METHOD = "finalize";
    EntryPointContext entryPointContext = null;

    public RemoteMethodHandler(EntryPointContext ectx )
    {
        this.entryPointContext = ectx;
    }
    
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable 
    {
        //TO BE FIXED: don't know why it hangs for the finalize method... so blocking it for now
        if ( !method.getName().equals(FINALIZE_METHOD) )
        {
            return method.invoke(entryPointContext.getInstance(null), args);
        }
        return methodProxy.invoke(object, args);
        //return null;
    }

}
