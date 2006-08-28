package org.apache.tuscany.tools.java2wsdl.generate;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.w3c.dom.Document;



/**
 * This class parses the Schema document and inserts sdo annotations wherever required
 *
 */
public class SDOAnnotationsDecorator extends SDOAnnotations
{
    private Map sdoAnnotations = null;
    
    
    public SDOAnnotationsDecorator()
    {
       
    }
    
    public void decorateWithAnnotations(Map annotationsMap, OMElement schemaDoc)
    {
        sdoAnnotations = annotationsMap;
        Iterator schemaElements = schemaDoc.getChildElements();
        OMElement schema = null;
        String targetNamespace = null;
        String annoMapKey = null;
        
        while ( schemaElements.hasNext() )
        {
            schema = (OMElement)schemaElements.next();
            targetNamespace = getSchemaTargetNamespace(schema);
            annoMapKey = makeAnnotationMapKey(targetNamespace, SCHEMA, "");
            if ( sdoAnnotations.get(annoMapKey) != null )
            {
                addSDOAnnotations(schema, annoMapKey);
            }
            decorateWithAnnotations(targetNamespace, schema);  
        }
    }
    
    private void decorateWithAnnotations(String targetNamespace, OMElement schemaElement)
    {
        Iterator childElements = schemaElement.getChildElements();
        OMElement childElement = null;
        String annoMapKey = null;
        while ( childElements.hasNext() )
        {
            childElement = (OMElement)childElements.next();
            annoMapKey = makeAnnotationMapKey(targetNamespace, 
                                                childElement.getLocalName(), 
                                                getTypeNameAttribute(childElement));
            if ( sdoAnnotations.get(annoMapKey) != null )
            {
                addSDOAnnotations(childElement, annoMapKey);
            }
            decorateWithAnnotations(targetNamespace, childElement);   
        }
    }
    
    private void addSDOAnnotations(OMElement childElement, String annoMapKey)
    {
        Map annotations = (Map)sdoAnnotations.get(annoMapKey);
        if ( annotations != null )
        {
            Iterator keys = annotations.keySet().iterator();
            String attrName = null;
            while ( keys.hasNext() )
            {
                attrName = (String)keys.next();
                childElement.addAttribute(attrName, 
                                            (String)annotations.get(attrName),
                                            null);
            }
        }
    }
    
    
    private String getSchemaTargetNamespace(OMElement element)
    {
        return element.getAttributeValue(new QName("","targetNamespace"));
    }
    
    
    private String getTypeNameAttribute(OMElement element)
    {
        return element.getAttributeValue(new QName("","name"));
    }
}
