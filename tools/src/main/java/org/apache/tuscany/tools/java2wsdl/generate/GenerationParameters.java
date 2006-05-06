package org.apache.tuscany.tools.java2wsdl.generate; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;

import org.apache.ws.java2wsdl.Java2WSDLConstants;
import org.apache.ws.java2wsdl.Java2WSDLUtils;
import org.apache.ws.java2wsdl.utils.Java2WSDLCommandLineOption;



/**
 * This class encapsulates the parameters that effect the generation of the 
 * WSDL.  For example they contain all user settings such as source class,
 * target location etc.
 *
 */
public class GenerationParameters 
{
	public static final String WSDL_FILENAME_SUFFIX = ".wsdl";
	
	private Map cmdLineOptions = null; 
	private FileOutputStream outputFileStream = null;
	private String sourceClassName = null;
	
	private String targetNamespace = null;
	private String targetNamespacePrefix = null;
	private String schemaTargetNamespace = null;
	private String schemaTargetNamespacePrefix = null;
	private ClassLoader classLoader = null;
	private String serviceName = null;
	private String style = null;
	private String use = null;
	private String locationUri = null;
	
	public GenerationParameters(Map cmdLineOptions) throws Exception
	{
		this.cmdLineOptions = cmdLineOptions;
		loadParameters();
	}
	
	protected void loadParameters() throws Exception
	{
		initializeSourceClassName();
		resolveFileOutputStream();
		resolveClassLoader4InputClasspath();
		initializeOtherParams();
	}
	
	private Java2WSDLCommandLineOption loadOption(String shortOption, String longOption) {
        //short option gets precedence
        Java2WSDLCommandLineOption option = null;
        if (longOption != null) {
            option = (Java2WSDLCommandLineOption) cmdLineOptions.get(longOption);
            if (option != null) {
                return option;
            }
        }
        if (shortOption != null) {
            option = (Java2WSDLCommandLineOption) cmdLineOptions.get(shortOption);
        }

        return option;
    }
	
	protected void initializeSourceClassName() throws Exception
	{
		Java2WSDLCommandLineOption option = 
			loadOption(Java2WSDLConstants.CLASSNAME_OPTION, Java2WSDLConstants.CLASSNAME_OPTION_LONG);
        sourceClassName = option == null ? null : option.getOptionValue();

        if (sourceClassName == null || sourceClassName.equals("")) {
            throw new Exception("class name must be present!");
        }
	}
	
	/**
	 * @throws Exception
	 */
	protected void resolveFileOutputStream() throws Exception
	{
		File outputFolder;
		Java2WSDLCommandLineOption  option = loadOption(Java2WSDLConstants.OUTPUT_LOCATION_OPTION,
	            Java2WSDLConstants.OUTPUT_LOCATION_OPTION_LONG);
	    String outputFolderName = option == null ? System.getProperty("user.dir") : option.getOptionValue();

	    outputFolder = new File(outputFolderName);
	    if (!outputFolder.exists()) {
	        outputFolder.mkdirs();
	    } else if (!outputFolder.isDirectory()) {
	        throw new Exception("The specified location " + outputFolderName + "is not a folder");
	    }
	    
	    option = loadOption(Java2WSDLConstants.OUTPUT_FILENAME_OPTION,
                Java2WSDLConstants.OUTPUT_FILENAME_OPTION_LONG);
        String outputFileName = option == null ? null : option.getOptionValue();
        //derive a file name from the class name if the filename is not specified
        if (outputFileName == null) 
        {
            outputFileName = Java2WSDLUtils.getSimpleClassName(sourceClassName) + WSDL_FILENAME_SUFFIX;
        }

        //first create a file in the given location
        File outputFile = new File(outputFolder, outputFileName);
        try 
        {
            if (!outputFile.exists()) 
            {
                outputFile.createNewFile();
            }
            outputFileStream = new FileOutputStream(outputFile);
        } 
        catch (IOException e) 
        {
            throw new Exception(e);
        }
	}
	
	protected void resolveClassLoader4InputClasspath() throws Exception 
	{
		Java2WSDLCommandLineOption option = null;
		
		if (option != null) {
            ArrayList optionValues = option.getOptionValues();
            URL[] urls = new URL[optionValues.size()];
            String[] classPathEntries = (String[]) optionValues.toArray(new String[optionValues.size()]);

            try {
                for (int i = 0; i < classPathEntries.length; i++) {
                    String classPathEntry = classPathEntries[i];
                    //this should be a file(or a URL)
                    if (Java2WSDLUtils.isURL(classPathEntry)) {
                        urls[i] = new URL(classPathEntry);
                    } else {
                        urls[i] = new File(classPathEntry).toURL();
                    }
                }
            } catch (MalformedURLException e) {
                throw new Exception(e);
            }

            classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());

        } else {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
	}
	
	protected void initializeOtherParams()
	{
//		set the other parameters to the builder
		Java2WSDLCommandLineOption option = loadOption(Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_OPTION,
                Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_OPTION_LONG);
   		schemaTargetNamespace = (option == null) ? null : option.getOptionValue();
		         
		option = loadOption(Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_PREFIX_OPTION,
                Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_PREFIX_OPTION_LONG);
		schemaTargetNamespacePrefix = (option == null) ? null : option.getOptionValue();

		option = loadOption(Java2WSDLConstants.TARGET_NAMESPACE_OPTION,
                Java2WSDLConstants.TARGET_NAMESPACE_OPTION_LONG);
		targetNamespace = (option == null) ? null : option.getOptionValue();

		option  = loadOption(Java2WSDLConstants.TARGET_NAMESPACE_PREFIX_OPTION,
                Java2WSDLConstants.TARGET_NAMESPACE_PREFIX_OPTION_LONG);
		targetNamespacePrefix = (option == null) ? null : option.getOptionValue();
		
		option = loadOption(Java2WSDLConstants.SERVICE_NAME_OPTION,
                Java2WSDLConstants.SERVICE_NAME_OPTION_LONG);
		serviceName = (option == null) ? Java2WSDLUtils.getSimpleClassName(sourceClassName) : option.getOptionValue();

		option = loadOption(Java2WSDLConstants.STYLE_OPTION,Java2WSDLConstants.STYLE_OPTION);
		style = (option == null) ? null : option.getOptionValue();
        
        
        option  = loadOption(Java2WSDLConstants.LOCATION_OPTION,
        		Java2WSDLConstants.LOCATION_OPTION);
        locationUri = (option == null) ? null : option.getOptionValue();
        
        option = loadOption(Java2WSDLConstants.USE_OPTION,Java2WSDLConstants.USE_OPTION);
        use = (option == null) ? null : option.getOptionValue();
	}
	
	public ClassLoader getClassLoader()
	{
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader)
	{
		this.classLoader = classLoader;
	}

	public String getLocationUri()
	{
		return locationUri;
	}

	public void setLocationUri(String locationUri)
	{
		this.locationUri = locationUri;
	}

	public FileOutputStream getOutputFileStream()
	{
		return outputFileStream;
	}

	public void setOutputFileStream(FileOutputStream outputFileStream)
	{
		this.outputFileStream = outputFileStream;
	}

	public String getSchemaTargetNamespace()
	{
		return schemaTargetNamespace;
	}

	public void setSchemaTargetNamespace(String schemaTargetNamespace)
	{
		this.schemaTargetNamespace = schemaTargetNamespace;
	}

	public String getSchemaTargetNamespacePrefix()
	{
		return schemaTargetNamespacePrefix;
	}

	public void setSchemaTargetNamespacePrefix(String schemaTargetNamespacePrefix)
	{
		this.schemaTargetNamespacePrefix = schemaTargetNamespacePrefix;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	public String getSourceClassName()
	{
		return sourceClassName;
	}

	public void setSourceClassName(String sourceClassName)
	{
		this.sourceClassName = sourceClassName;
	}

	public String getStyle()
	{
		return style;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}

	public String getTargetNamespace()
	{
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace)
	{
		this.targetNamespace = targetNamespace;
	}

	public String getTargetNamespacePrefix()
	{
		return targetNamespacePrefix;
	}

	public void setTargetNamespacePrefix(String targetNamespacePrefix)
	{
		this.targetNamespacePrefix = targetNamespacePrefix;
	}

	public String getUse()
	{
		return use;
	}

	public void setUse(String use)
	{
		this.use = use;
	}
}
	
