package org.apache.tuscany.binding.rmi.util;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;

public class RMINamingPolicy extends DefaultNamingPolicy 
{
    private String classname = null;
    RMINamingPolicy(String classname)
    {
        this.classname = classname;
    }
    
    public String getClassName(String prefix, String source, Object key, Predicate names)
    {
        return classname + "extends java.rmi.Remote";
    }

}
