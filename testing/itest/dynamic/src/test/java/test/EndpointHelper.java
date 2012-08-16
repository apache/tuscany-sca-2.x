package test;

import java.net.URI;
import java.net.URL;

import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.binding.ws.WebServiceBinding;
import org.apache.tuscany.sca.binding.ws.WebServiceBindingFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.processor.ContributionResolveException;
import org.apache.tuscany.sca.contribution.processor.ExtensibleURLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.ProcessorContext;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.impl.NodeImpl;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLDefinition;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLFactory;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterface;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLInterfaceContract;
import org.apache.tuscany.sca.interfacedef.wsdl.WSDLObject;

public class EndpointHelper {

    // TODO: change this to be a method on Node that takes a configured Binding to add a new endpoint
    public static void addWSEndpoint(Node node, String endpointName, URL wsdlURL, QName portTypeQN, String targetURL) {
        ExtensionPointRegistry extensionPoints = ((NodeImpl)node).getExtensionPointRegistry();
        FactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);

        Component component = assemblyFactory.createComponent();
        component.setName("foo");        
        
        WebServiceBindingFactory webServiceBindingFactory = modelFactories.getFactory(WebServiceBindingFactory.class);
        WebServiceBinding wsBinding = webServiceBindingFactory.createWebServiceBinding();
        wsBinding.setURI(targetURL);
        
        WSDLInterfaceContract wsdlIC = getWSDLInterfaceContract(wsdlURL, portTypeQN, modelFactories, extensionPoints);
        wsBinding.setBindingInterfaceContract(wsdlIC);

        wsBinding.setGeneratedWSDLDocument(((WSDLInterface)wsdlIC.getInterface()).getWsdlDefinition().getDefinition());
        wsBinding.setService((Service)wsBinding.getGeneratedWSDLDocument().getServices().values().iterator().next());
        
        Endpoint newEndpoint = assemblyFactory.createEndpoint();
        newEndpoint.setComponent(component);
        ComponentService cs = assemblyFactory.createComponentService();
        cs.setName("baa");
        newEndpoint.setService(cs);
        newEndpoint.setBinding(wsBinding);
        newEndpoint.setURI(endpointName);
        newEndpoint.setRemote(true);
        
        ((NodeImpl)node).getEndpointRegistry().addEndpoint(newEndpoint);
    }
     
     private static WSDLInterfaceContract getWSDLInterfaceContract(URL wsdlURL, QName portTypeQN, FactoryExtensionPoint modelFactories, ExtensionPointRegistry extensionPoints) {
         try {

             ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
             Contribution contribution = contributionFactory.createContribution();
             ModelResolverExtensionPoint modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
             ModelResolver modelResolver = new ExtensibleModelResolver(contribution, modelResolvers, modelFactories);
             contribution.setModelResolver(modelResolver);

             ExtensibleURLArtifactProcessor aproc = new ExtensibleURLArtifactProcessor(extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class));

             ProcessorContext ctx = new ProcessorContext();
             WSDLDefinition wd = aproc.read(null, new URI("anything.wsdl"), wsdlURL, ctx, WSDLDefinition.class);
             modelResolver.addModel(wd, ctx);
             modelResolver.resolveModel(WSDLDefinition.class, wd, ctx);
             final WSDLObject<PortType> pt = wd.getWSDLObject(PortType.class, portTypeQN);
             if(pt == null)
                 throw new ContributionResolveException("Couldn't find " + portTypeQN);
             
             WSDLFactory wif = modelFactories.getFactory(WSDLFactory.class);
             final WSDLInterface nwi = wif.createWSDLInterface(pt.getElement(), wd, modelResolver, null);
             nwi.setWsdlDefinition(wd);

             WSDLInterfaceContract wsdlIC = wif.createWSDLInterfaceContract();
             wsdlIC.setInterface(nwi);

             return wsdlIC;

         } catch(Exception e) {
             throw new RuntimeException(e);
         }
     }
     
}
