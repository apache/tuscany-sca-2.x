package org.apache.tuscany.sca.contribution.processor;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;

public class DefaultUnknownElementProcessor{
	
	private Monitor monitor;
	private static final Logger logger = Logger.getLogger(DefaultUnknownElementProcessor.class.getName());
	
	public DefaultUnknownElementProcessor(Monitor monitor){
		this.monitor = monitor;
	}
	private DocumentBuilderFactory documentBuilderFactory;
	private Document document;
	
	/**
	 * Reads the contetns of the unknown elements and generates the DOM	
	 * @param reader
	 * @param name
	 * @return
	 * @throws XMLStreamException
	 */
	public Object read(XMLStreamReader reader, QName name) throws XMLStreamException{
		
		int event = reader.getEventType();
		int level = 0;
		ArrayList<String> elementList = new ArrayList<String>();
		document = createDocument();
		while(reader.hasNext()){
	    	switch(event){
		    	case START_ELEMENT:
		    		elementList.add(reader.getName().getLocalPart());
		    		if(level == 0){
		    			generateDOM(reader,null);
		    			level++;
		    		}
		    		else{
		    			generateDOM(reader,elementList.get(elementList.size()-2).toString());
		    		}
		    		
		    		break;
		    	case END_ELEMENT:
		    		elementList.remove(reader.getName().getLocalPart());
	    	}
	    	if(reader.hasNext()){
	    		event = reader.next();
	    	}
	      	
	      	if(event == START_ELEMENT || event == END_ELEMENT){
	      		if(reader.getName().equals(name)){
	      			break;
	      		}
	      	}
	    }
		return document;
	}

	/**
	 * Writes unknown portions back to the writer
	 * @param model
	 * @param writer
	 */
	public void write(Object model, XMLStreamWriter writer) {
		
		if( ! (model instanceof Document)) {
			return;
		}
		
		Document doc = (Document)model;
		try{
			DocumentTraversal traversal = (DocumentTraversal)doc;
			TreeWalker walker = traversal.createTreeWalker(doc.getDocumentElement(),NodeFilter.SHOW_ALL, null, true);
			writeDOM(walker,writer);
		}
		catch(Exception e){
			if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Document not created ");
            }
			error("Document not created",document,e);
		}
	}

	/**
	 * Method to generate the DOM
	 * @param reader
	 * @param parent
	 * @throws Exception 
	 */
	//private void generateDOM(String elementText, String parent) {
	private void generateDOM(XMLStreamReader reader, String parent) {
		try{
			String elePrefix = reader.getPrefix();
			String eleQName = reader.getLocalName();
			if (elePrefix != null && elePrefix.length() != 0) {
                eleQName = elePrefix + ":" + eleQName;
            }
			
			Element element = document.createElementNS(reader.getNamespaceURI(), eleQName);
				
			int attributeCount = reader.getAttributeCount();
			for(int i = 0;i < attributeCount;i++){
				String ns = reader.getAttributeNamespace(i);
                String prefix = reader.getAttributePrefix(i);
                String qname = reader.getAttributeLocalName(i);
                String value = reader.getAttributeValue(i);
                if (prefix != null && prefix.length() != 0) {
                    qname = prefix + ":" + qname;
                }
                element.setAttributeNS(ns,qname,value);
			}
			if(parent == null){
				if(document != null){
					document.appendChild(element);
				}
				else{
					if (logger.isLoggable(Level.SEVERE)) {
	                    logger.log(Level.SEVERE, "Document not created ");
	                }
					error("Document not created",document,element);
				}
			}
			else{
				Node parentNode = getParentNode(document,parent);
				if(parentNode != null){
					parentNode.appendChild(element);
				}
				else{
					if (logger.isLoggable(Level.SEVERE)) {
						logger.log(Level.SEVERE, "Parent node not found");
					}
					error("Parent node not found",document,parentNode.getNodeName());
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, "Document not created ");
            }
			error("Document not created",document,e);
		}
	}

	/**
	 * Method to create an empty document
	 * @return
	 */
	private Document createDocument() {
		try {
	        if (documentBuilderFactory == null) {
	            documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        }
	        document = documentBuilderFactory.newDocumentBuilder().newDocument();
	        return document;
	    } catch (ParserConfigurationException e) {
	    	e.printStackTrace();
	    }
	    return null;
	}

	/**
	 * Method to traverse the DOM structure and write the elements 
	 * @param walker
	 * @param writer
	 * @throws XMLStreamException
	 */
	private void writeDOM(TreeWalker walker,XMLStreamWriter writer) throws XMLStreamException {
	  
	    Node parent = walker.getCurrentNode();
	    
	    writer.writeStartElement(parent.getPrefix(), parent.getLocalName(), parent.getNamespaceURI());
	    
	    NamedNodeMap attributes = parent.getAttributes();
	   
	    for(int i = 0;i<attributes.getLength();i++){
		   writer.writeAttribute(attributes.item(i).getPrefix(), attributes.item(i).getNamespaceURI(), attributes.item(i).getLocalName(), attributes.item(i).getNodeValue());
	    }
	    	   
	    for (Node n = walker.firstChild();n != null;n = walker.nextSibling()) {
	      writeDOM(walker,writer);
	    }
	    writer.writeEndElement();
	    
	    walker.setCurrentNode(parent);
	}

	/**
	 * Method to get the Parent node out of the DOM structure
	 * @param doc
	 * @param parent
	 * @return
	 */
	private Node getParentNode(Node doc,String parent) {
		Node parentNode = null;
		try{
			DocumentTraversal traversal = (DocumentTraversal)doc;
			
			CharSequence prefixChar = ":";
			NodeIterator iterator = traversal.createNodeIterator(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);
			for (Node n = iterator.nextNode(); n != null; n = iterator.nextNode()) {
				String nodeName = n.getNodeName();
				String[] str = null;
				if(n.getNodeName().contains(prefixChar)){
					str = nodeName.split(":");
					nodeName = str[str.length-1];
				}
				if(parent.equalsIgnoreCase(nodeName)){
			    	  parentNode = n;
			    	}
			    }
			return parentNode;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return parentNode;
	}
	
	 /**
     * Marshals exceptions into the monitor
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Exception ex) {
    	if (monitor != null) {
	        Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-validation-messages", Severity.ERROR, model, message, ex);
	        monitor.problem(problem);
    	}
    }
    
    /**
     * Report a error.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Object... messageParameters) {
    	if (monitor != null) {
	        Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-validation-messages", Severity.ERROR, model, message, (Object[])messageParameters);
	        monitor.problem(problem);
    	}
    }
	
}
