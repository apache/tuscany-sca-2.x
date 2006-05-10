package org.apache.tuscany.databinding.sdo.system;

import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.helper.DataFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface SDOService {

    /**
     * Returns a Type helper
     * @return
     */
    public TypeHelper getTypeHelper();

    /**
     * Returns an XML helper
     * @return
     */
    public XMLHelper getXMLHelper();

    /**
     * Returns an XSD helper
     * @return
     */
    public XSDHelper getXSDHelper();

    /**
     * Returns a DataFactory
     * @return
     */
    public DataFactory getDataFactory();
    
}
