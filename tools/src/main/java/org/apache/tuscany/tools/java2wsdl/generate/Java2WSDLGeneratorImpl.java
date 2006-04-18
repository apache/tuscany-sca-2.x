/**
 * 
 */
package org.apache.tuscany.tools.java2wsdl.generate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.axis2.wsdl.Java2WSDL;
import org.apache.axis2.wsdl.builder.Java2WOMBuilder;
import org.apache.axis2.wsdl.builder.Java2WSDLBuilder;
import org.apache.axis2.wsdl.builder.SchemaGenerator;
import org.apache.axis2.wsdl.codegen.CodeGenerationException;
import org.apache.axis2.wsdl.i18n.CodegenMessages;
import org.apache.axis2.wsdl.util.CommandLineOption;
import org.apache.axis2.wsdl.util.CommandLineOptionParser;
import org.apache.axis2.wsdl.util.Java2WSDLOptionsValidator;
import org.apache.axis2.wsdl.util.CommandLineOptionConstants.Java2WSDLConstants;
import org.apache.axis2.wsdl.writer.WOMWriter;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.wsdl.WSDLDescription;
import org.apache.wsdl.impl.WSDLDescriptionImpl;

/**
 * This is an implementation of the Java2WSDLGenerator facade. This
 * implementation is a decorator around the Axis2 implementation of the
 * Java2WSDL conversion. The WSDL generation is divided into phases that are
 * stringed up as a template method. The phases are - User Input Validation -
 * WSDL Java Model Generation - Serialization of WSDL Java Model The function of
 * each phase is accomplished by delegation to the appropriate classes in Axis2.
 * At the start and end of each phase an event is published to subcribers
 * denoting the start and end of the phase.
 * 
 * Such a spliting up of the Java2WSDL conversion into phases has been designed
 * to enable interceptors to modify the model or apply transformations to the
 * output. Typically the interceptors can subscribe to the start and end events
 * of these phases and hence be able to intercept.
 * 
 * Note: This class contains substantial AXIS2 Java2WSDL code refactored into
 * it. These will be removed as and when the Axis2 code is fixed.
 * 
 */
public class Java2WSDLGeneratorImpl extends Java2WSDLBuilder implements
		Java2WSDLGenerator {
	public static final String HTTP = "http://";

	public static final String WSDL_FILENAME_SUFFIX = ".wsdl";

	public static final String DEFAULT_PREFIX = "wsdl";

	public static final char PACKAGE_CLASS_DELIMITER = '.';

	public static final String DEFAULT_TARGET_NAMESPACE_PREFIX = "tns";

	public static final String DEFAULT_SCHEMA_TARGET_NAMESPACE_PREFIX = "stns";

	private List<WSDLGenListener> genPhaseListeners = new Vector<WSDLGenListener>();

	private Map<String, CommandLineOption> commandLineOptions = null;

	private String sourceClassName = null;

	private OutputStream outputStream = null;

	private ClassLoader classLoader;

	private WSDLDescription wsdlDescription = null;

	private WSDLModel wsdlModel = null;

	public Java2WSDLGeneratorImpl() {
		super(null, null, null);

	}

	private void multicastGenPhaseCompletionEvent(int genPhase) {
		WSDLGenEvent event = new WSDLGenEvent(this, genPhase);
		Iterator iterator = genPhaseListeners.iterator();
		while (iterator.hasNext()) {
			((WSDLGenListener) iterator.next()).WSDLGenPhaseCompleted(event);
		}
	}

	protected CommandLineOption loadOption(String shortOption,
			String longOption, Map options) {
		// short option gets precedence
		CommandLineOption option = null;
		if (longOption != null) {
			option = (CommandLineOption) options.get(longOption);
			if (option != null) {
				return option;
			}
		}
		if (shortOption != null) {
			option = (CommandLineOption) options.get(shortOption);
		}

		return option;
	}

	private String getSimpleClassName(String qualifiedName) {
		int index = qualifiedName.lastIndexOf(".");
		if (index > 0) {
			return qualifiedName.substring(index + 1, qualifiedName.length());
		}
		return qualifiedName;
	}

	private void initialize() throws Exception {
		CommandLineOption option = null;

		// get the input classname
		option = loadOption(Java2WSDLConstants.CLASSNAME_OPTION,
				Java2WSDLConstants.CLASSNAME_OPTION_LONG, commandLineOptions);
		sourceClassName = option == null ? null : option.getOptionValue();
		if (sourceClassName == null || sourceClassName.equals("")) {
			throw new CodeGenerationException(CodegenMessages
					.getMessage("java2wsdl.classIsMust"));
		}

		if (getOutputStream() == null) {
			resolveOutputStream();
		}

		// create and initialize an instance of the Java2WSDLBuilder
		resolveClassLoader4InputClasspath();

		option = loadOption(Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_OPTION,
				Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_OPTION_LONG,
				commandLineOptions);
		setSchemaTargetNamespace(option == null ? namespaceFromPackageName(sourceClassName)
				+ "/schema"
				: option.getOptionValue());

		option = loadOption(
				Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_PREFIX_OPTION,
				Java2WSDLConstants.SCHEMA_TARGET_NAMESPACE_PREFIX_OPTION_LONG,
				commandLineOptions);
		setSchemaTargetNamespacePrefix(option == null ? DEFAULT_SCHEMA_TARGET_NAMESPACE_PREFIX
				: option.getOptionValue());

		option = loadOption(Java2WSDLConstants.TARGET_NAMESPACE_OPTION,
				Java2WSDLConstants.TARGET_NAMESPACE_OPTION_LONG,
				commandLineOptions);
		setTargetNamespace(option == null ? namespaceFromPackageName(sourceClassName)
				: option.getOptionValue());

		option = loadOption(Java2WSDLConstants.TARGET_NAMESPACE_PREFIX_OPTION,
				Java2WSDLConstants.TARGET_NAMESPACE_PREFIX_OPTION_LONG,
				commandLineOptions);
		setTargetNamespacePrefix(option == null ? DEFAULT_TARGET_NAMESPACE_PREFIX
				: option.getOptionValue());

		option = loadOption(Java2WSDLConstants.SERVICE_NAME_OPTION,
				Java2WSDLConstants.SERVICE_NAME_OPTION_LONG, commandLineOptions);
		setServiceName(option == null ? getSimpleClassName(sourceClassName)
				: option.getOptionValue());
	}

	protected void resolveOutputStream() throws Exception {
		// create the output stream from the input name of the folder and file
		// for the WSDL output
		CommandLineOption option = loadOption(
				Java2WSDLConstants.OUTPUT_LOCATION_OPTION,
				Java2WSDLConstants.OUTPUT_LOCATION_OPTION_LONG,
				commandLineOptions);
		String outputFolderName = option == null ? System
				.getProperty("user.dir") : option.getOptionValue();

		option = loadOption(Java2WSDLConstants.OUTPUT_FILENAME_OPTION,
				Java2WSDLConstants.OUTPUT_FILENAME_OPTION_LONG,
				commandLineOptions);

		String outputFileName = null;
		// derive a file name from the class name if the filename is not
		// specified
		if (option == null) {
			outputFileName = getSimpleClassName(sourceClassName)
					+ WSDL_FILENAME_SUFFIX;
		} else {
			outputFileName = option.getOptionValue();
		}

		File outputFolder = new File(outputFolderName);
		if (!outputFolder.exists()) {
			outputFolder.mkdir();
		} else if (!outputFolder.isDirectory()) {
			throw new CodeGenerationException(CodegenMessages.getMessage(
					"java2wsdl.notAFolder", outputFolderName));
		}

		// first create a file in the given location
		File outputFile = new File(outputFolder, outputFileName);
		try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			outputStream = new FileOutputStream(outputFile);
		} catch (IOException e) {
			throw new CodeGenerationException(e);
		}

	}

	private void resolveClassLoader4InputClasspath() throws Exception {
		// if the class path is present, create a URL class loader with those
		// class path entries present. if not just take the TCCL
		CommandLineOption option = loadOption(
				Java2WSDLConstants.CLASSPATH_OPTION,
				Java2WSDLConstants.CLASSPATH_OPTION_LONG, commandLineOptions);

		if (option != null) {
			ArrayList optionValues = option.getOptionValues();
			URL[] urls = new URL[optionValues.size()];
			String[] classPathEntries = (String[]) optionValues
					.toArray(new String[optionValues.size()]);

			try {
				for (int i = 0; i < classPathEntries.length; i++) {
					String classPathEntry = classPathEntries[i];
					// this should be a file(or a URL)
					if (classPathEntry.startsWith(HTTP)) {
						urls[i] = new URL(classPathEntry);
					} else {
						urls[i] = new File(classPathEntry).toURL();
					}
				}
			} catch (MalformedURLException e) {
				throw new CodeGenerationException(e);
			}

			classLoader = new URLClassLoader(urls, Thread.currentThread()
					.getContextClassLoader());

		} else {
			classLoader = Thread.currentThread().getContextClassLoader();
		}

	}

	protected boolean validateInputArgs(String[] args) {
		boolean isValid = true;
		CommandLineOptionParser parser = new CommandLineOptionParser(args);
		if (parser.getAllOptions().size() == 0) {
			Java2WSDL.printUsage();
			isValid = false;
		} else if (parser.getInvalidOptions(new Java2WSDLOptionsValidator())
				.size() > 0) {
			Java2WSDL.printUsage();
			isValid = false;
		}

		if (isValid) {
			commandLineOptions = parser.getAllOptions();
		}

		return isValid;
	}

	public boolean generateWSDLModel() throws Exception {
		boolean isComplete = true;
		initialize();

		SchemaGenerator schemaGenerator = new SchemaGenerator(getClassLoader(),
				getSourceClassName(), getSchemaTargetNamespace(),
				getSchemaTargetNamespacePrefix());

		XmlSchema schema = schemaGenerator.generateSchema();

		wsdlDescription = new Java2WOMBuilder(schemaGenerator.getTypeTable(),
				schemaGenerator.getMethods(), schema, getServiceName(),
				getTargetNamespace(), getTargetNamespacePrefix()).generateWOM();

		return isComplete;
	}

	public boolean serializeWSDLModel() throws Exception {
		boolean isComplete = true;

		// WOMWriter womWriter =
		// WOMWriterFactory.createWriter(org.apache.wsdl.WSDLConstants.WSDL_1_1);
		WOMWriter womWriter = new TuscanyWOM2WSDL11Writer();
		womWriter.setdefaultWSDLPrefix(DEFAULT_PREFIX);

		womWriter.writeWOM(wsdlDescription, getOutputStream());

		return isComplete;
	}

	/*
	 * This is the template method that splits the Java2WSDL generation cycle
	 * into phase / steps.
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#generateWSDL(java.lang.String[])
	 */
	public void generateWSDL(String[] args) {
		try {
			// if the argument input are found to be valid
			if (validateInputArgs(args)) {
				// multicast event for input args validation complete
				multicastGenPhaseCompletionEvent(WSDLGenListener.INPUT_ARGS_VALIDATION);

				// if the WSDL Model generation was successul
				if (generateWSDLModel()) {
					wsdlModel = new WSDLModel(
							(WSDLDescriptionImpl) wsdlDescription);
					// multicast event for generation of wsdl model
					multicastGenPhaseCompletionEvent(WSDLGenListener.WSDL_MODEL_CREATION);
					// if the serialization of the generated (and fixed) model
					// is successful
					if (serializeWSDLModel()) {
						// multicast event for writing of the WSDL Model to
						// supplied output stream
						multicastGenPhaseCompletionEvent(WSDLGenListener.WSDL_MODEL_WRITING);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#addWSDLGenListener(tuscany.tools.WSDLGenListener)
	 */
	public void addWSDLGenListener(WSDLGenListener l) {
		genPhaseListeners.add(l);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#removeWSDLGenListener(tuscany.tools.WSDLGenListener)
	 */
	public void removeWSDLGenListener(WSDLGenListener l) {
		genPhaseListeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#getCommandLineOptions()
	 */
	public Map getCommandLineOptions() {
		return commandLineOptions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#setCommandLineOptoins(java.util.Map)
	 */
	public void setCommandLineOptoins(Map cmdLineOpts) {
		commandLineOptions = cmdLineOpts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#getWSDLDescription()
	 */
	public WSDLModel getWSDLModel() {
		return wsdlModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#getOutputStream()
	 */
	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return outputStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuscany.tools.Java2WSDLGeneratorIfc#setOutputStream(java.io.OutputStream)
	 */
	public void setOutputStream(OutputStream outStream) {
		outputStream = outStream;
	}

	public void generateWSDL(Map commandLineOptions) {
		// TODO Auto-generated method stub

	}

	public String getSourceClassName() {
		return sourceClassName;
	}

	public void setSourceClassName(String sourceClassName) {
		this.sourceClassName = sourceClassName;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * create an xml namespace URI given a java package name. For example given
	 * org.apache.tuscany.wsdl.java2wsdl the string
	 * http://java2wsdl.wsdl.tuscany.apache.org is returned
	 * 
	 * @param packageName
	 * @return
	 */
	private String namespaceFromPackageName(String packageName) {
		StringBuffer strBuf = new StringBuffer(HTTP);
		int prevIndex = packageName.length();
		int currentIndex = packageName.lastIndexOf(PACKAGE_CLASS_DELIMITER);
		while (currentIndex != -1) {
			prevIndex = currentIndex;
			currentIndex = packageName.lastIndexOf(PACKAGE_CLASS_DELIMITER,
					prevIndex - 1);
			strBuf.append(packageName.substring(currentIndex + 1, prevIndex));
			if (currentIndex != -1) {
				strBuf.append(PACKAGE_CLASS_DELIMITER);
			}
		}
		return strBuf.toString();
	}

}
