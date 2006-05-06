package org.apache.tuscany.container.js.sample.helloworld;

import org.apache.tuscany.core.client.TuscanyRuntime;
import org.osoa.sca.CurrentModuleContext;
import org.osoa.sca.ModuleContext;

public class Main {

    public static final void main(String[] args) throws Exception {

        TuscanyRuntime tuscany = new TuscanyRuntime("jsHello", null);
        tuscany.start();
        ModuleContext moduleContext = CurrentModuleContext.getContext();

        HelloWorld s = 
            (HelloWorld) moduleContext.locateService("HelloWorldJSComponent");

        String value = s.getGreeting("Petra");

        System.out.println(value);

        tuscany.stop();
    }
    
}
