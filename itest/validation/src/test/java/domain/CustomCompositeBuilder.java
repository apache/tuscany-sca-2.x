package domain;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.impl.CompositeBuilderImpl;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultValidatingXMLInputFactory;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ValidationSchemaExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ExtensibleModelResolver;
import org.apache.tuscany.sca.contribution.resolver.ModelResolverExtensionPoint;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.core.ModuleActivatorExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.policy.IntentAttachPointTypeFactory;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.builder.ContributionDependencyBuilder;
import org.apache.tuscany.sca.workspace.builder.impl.ContributionDependencyBuilderImpl;
import org.apache.tuscany.sca.definitions.SCADefinitions;

public class CustomCompositeBuilder {
    private URLArtifactProcessor<Contribution> contributionProcessor;
    private ModelResolverExtensionPoint modelResolvers;
    private ModelFactoryExtensionPoint modelFactories;
    private WorkspaceFactory workspaceFactory;
    private AssemblyFactory assemblyFactory;
    private XMLOutputFactory outputFactory;
    private StAXArtifactProcessor<Object> xmlProcessor; 
    private ContributionDependencyBuilder contributionDependencyBuilder;
    private CompositeBuilder domainCompositeBuilder;
    private CompositeBuilder nodeCompositeBuilder;
    private NodeImplementationFactory nodeFactory;
    //private AtomBindingFactory atomBindingFactory;
    private static Workspace workspace;
    private List<SCADefinitions> policyDefinitions;
    private Monitor monitor;
        
    private CustomCompositeBuilder() {
        // no code req'd
    }

    public static CustomCompositeBuilder getInstance()
    {
      if (ref == null)
          ref = new CustomCompositeBuilder();
      return ref;
    }

    private static CustomCompositeBuilder ref;
    
    private void init() {       
        // Create extension point registry 
        ExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        
        // Initialize the Tuscany module activators
        ModuleActivatorExtensionPoint moduleActivators = extensionPoints.getExtensionPoint(ModuleActivatorExtensionPoint.class);
        for (ModuleActivator activator: moduleActivators.getModuleActivators()) {
            //activator.start(extensionPoints);
        }
        
        // Create a monitor
        UtilityExtensionPoint utilities = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();

        // Get XML input/output factories
        modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        
        // Create a validation XML schema extension point
        ValidationSchemaExtensionPoint schemas = extensionPoints.getExtensionPoint(ValidationSchemaExtensionPoint.class);
        // Create a validating XML input factory
        XMLInputFactory validatingInputFactory = new DefaultValidatingXMLInputFactory(inputFactory, schemas, monitor);
        //modelFactories.addFactory(validatingInputFactory);
        
        // Get contribution workspace and assembly model factories
        workspaceFactory = modelFactories.getFactory(WorkspaceFactory.class); 
        assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        nodeFactory = modelFactories.getFactory(NodeImplementationFactory.class);
        //atomBindingFactory = modelFactories.getFactory(AtomBindingFactory.class);
        
        // Create XML artifact processors
        StAXArtifactProcessorExtensionPoint xmlProcessorExtensions = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        xmlProcessor = new ExtensibleStAXArtifactProcessor(xmlProcessorExtensions, inputFactory, outputFactory, monitor);
        
        // Create contribution content processor
        URLArtifactProcessorExtensionPoint docProcessorExtensions = extensionPoints.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);
        contributionProcessor = docProcessorExtensions.getProcessor(Contribution.class);
        //policyDefinitions = new ArrayList<SCADefinitions>();
        //docProcessorExtensions.addArtifactProcessor(new CompositeDocumentProcessor(xmlProcessor, validatingInputFactory, policyDefinitions, monitor));
        
        // Get the model resolvers
        modelResolvers = extensionPoints.getExtensionPoint(ModelResolverExtensionPoint.class);
        
        // Create a contribution dependency builder
        contributionDependencyBuilder = new ContributionDependencyBuilderImpl(monitor);
        
        // Create a composite builder
        SCABindingFactory scaBindingFactory = modelFactories.getFactory(SCABindingFactory.class);
        IntentAttachPointTypeFactory attachPointTypeFactory = modelFactories.getFactory(IntentAttachPointTypeFactory.class);
        InterfaceContractMapper contractMapper = utilities.getUtility(InterfaceContractMapper.class);
        domainCompositeBuilder = new CompositeBuilderImpl(assemblyFactory, scaBindingFactory, attachPointTypeFactory, contractMapper, monitor);
    }
    
    public void loadContribution(String compositeURL, String sourceURI, String sourceURL) throws Exception {
    	init();

        // Create workspace model
        workspace = workspaceFactory.createWorkspace();
        workspace.setModelResolver(new ExtensibleModelResolver(workspace, modelResolvers, modelFactories));

        // Read the sample store contribution
        URI artifactURI = URI.create(sourceURI);
        //URL contributionURL = FileHelper.toFile(new URL(sourceURL)).toURI().toURL();
        URL artifactURL = new File(sourceURL).toURI().toURL();
        URL contributionURL = new File(compositeURL).toURI().toURL();
        Contribution storeContribution = contributionProcessor.read(contributionURL, artifactURI, artifactURL);              	
        workspace.getContributions().add(storeContribution);
        
        // Build the contribution dependencies
        Map<Contribution, List<Contribution>> contributionDependencies = new HashMap<Contribution, List<Contribution>>();
        Set<Contribution> resolved = new HashSet<Contribution>();
        for (Contribution contribution: workspace.getContributions()) {
            List<Contribution> dependencies = contributionDependencyBuilder.buildContributionDependencies(contribution, workspace);
            
            // Resolve contributions
            for (Contribution dependency: dependencies) {
                if (!resolved.contains(dependency)) {
                    resolved.add(dependency);
                   	contributionProcessor.resolve(dependency, workspace.getModelResolver());                                       
                }
            }
            
            contributionDependencies.put(contribution, dependencies);
        }
        
        // Create a composite model for the domain
        /*Composite domainComposite = assemblyFactory.createComposite();
        domainComposite.setName(new QName(Constants.SCA10_NS, "domain"));
        
        // Add all deployables to it, normally the domain administrator would select
        // the deployables to include
        domainComposite.getIncludes().addAll(workspace.getDeployables());
        
        // Build the domain composite and wire the components included in it
        domainCompositeBuilder.build(domainComposite);*/
    }
    
    public Monitor getMonitorInstance() {
    	return monitor;
    }
    
    public void readContribution(String compositeURL, String sourceURI, String sourceURL) throws Exception {
    	init();

        // Create workspace model
        workspace = workspaceFactory.createWorkspace();
        workspace.setModelResolver(new ExtensibleModelResolver(workspace, modelResolvers, modelFactories));

        // Read the sample store contribution
        URI artifactURI = URI.create(sourceURI);
        //URL contributionURL = FileHelper.toFile(new URL(sourceURL)).toURI().toURL();
        URL artifactURL = new File(sourceURL).toURI().toURL();
        URL contributionURL = new File(compositeURL).toURI().toURL();
        Contribution storeContribution = contributionProcessor.read(contributionURL, artifactURI, artifactURL);              	
        workspace.getContributions().add(storeContribution);    	
    }
}
