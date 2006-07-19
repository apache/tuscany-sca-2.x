package org.apache.tuscany.binding.rmi.entrypoint;

public class RMIEntryPointClassLoader extends ClassLoader 
{
    public RMIEntryPointClassLoader()
    {
        super(Thread.currentThread().getContextClassLoader());
    }
}
