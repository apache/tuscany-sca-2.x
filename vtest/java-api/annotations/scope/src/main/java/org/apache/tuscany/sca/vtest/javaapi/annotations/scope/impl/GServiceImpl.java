package org.apache.tuscany.sca.vtest.javaapi.annotations.scope.impl;

import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.AService;
import org.apache.tuscany.sca.vtest.javaapi.annotations.scope.GService;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.EagerInit;

@Scope("COMPOSITE")
@EagerInit
public class GServiceImpl implements GService {

	public static int initCalledCounter = 0;

	public static int destroyCalledCounter = 0;
	
	public AService a1;
	
	public String p1;
	
	@Init
    public void initGService() throws Exception {
    	initCalledCounter++;
    	System.out.println("GService->initGService");
    }

    @Destroy
    public void destroyGService() {
    	destroyCalledCounter++;
    	System.out.println("GService->destroyGService");
    }
    
	@Reference
	public void setA1(AService a1) {
		this.a1 = a1;
	}

	@Property
	public void setP1(String p1) {
		this.p1 = p1;
	}
	
    public String getName() {
        return "GService";
    }
    
	public int getInitCalledCounter() {
		return initCalledCounter;
	}

	public int getDestroyCalledCounter() {
		return destroyCalledCounter;
	}
}
