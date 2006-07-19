package org.apache.tuscany.binding.rmi.util;

import java.lang.reflect.Method;

import org.apache.tuscany.binding.rmi.entrypoint.RMIEntryPointClassLoader;

import net.sf.cglib.asm.ClassWriter;
import net.sf.cglib.asm.CodeVisitor;
import net.sf.cglib.asm.Constants;
import net.sf.cglib.asm.Type;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.proxy.InterfaceMaker;

public class RemoteInterfaceGenerator implements Constants 
{
    public static final char PERIOD = '.';
    public static final char FWD_SLASH = '/';
    public static final String REMOTE_EXCEPTION = "java.rmi.RemoteException";
    
    public static Class generateRemoteInterface(Class srcIfc, ClassLoader cl)
    {
        InterfaceMaker ifcmaker = new InterfaceMaker();
        ifcmaker.setNamingPolicy(new RMINamingPolicy(srcIfc.getName()));
        ifcmaker.setClassLoader(cl);
        
        Method[] methods = srcIfc.getMethods();
        
        for ( int count = 0 ; count < methods.length ; ++count )
        {
            ifcmaker.add(ReflectUtils.getSignature(methods[count]), getExceptionTypes(methods[count]));
        }
        
        return ifcmaker.create();
    }
    
    public static Type[] getExceptionTypes(Method method)
    {
        Type[] eTypes = ReflectUtils.getExceptionTypes(method);
        Type[] exceptionTypes = new Type[eTypes.length + 1];
        int count = 0;
        for ( ; count < eTypes.length ; ++count )
        {
            exceptionTypes[count] = eTypes[count];
        }
        exceptionTypes[count] = TypeUtils.getType(REMOTE_EXCEPTION);
        return exceptionTypes;
    }
}
