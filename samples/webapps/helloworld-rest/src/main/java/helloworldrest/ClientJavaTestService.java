package helloworldrest;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import helloworldrest.HelloWorldService;

/*
 * To test, simply run the program
 * Access the service by invoking the getName() method of HelloWorldService 
 */

public class ClientJavaTestService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SCADomain scaDomain = SCADomain.newInstance("rest.composite");
        HelloWorldService helloService = 
            scaDomain.getService(HelloWorldService.class, "HelloWorldRESTServiceComponent");
        
		//HelloWorldService helloService = new HelloWorldServiceImpl();
        System.out.println("### Message from REST service " + helloService.getName());
        
        scaDomain.close();
	}

}
