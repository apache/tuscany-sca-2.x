package org.apache.tuscany.databinding.sdo.system;

import commonj.sdo.helper.XSDHelper;
import commonj.sdo.helper.DataFactory;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface SDOService {

    public XSDHelper getHelper();

    public DataFactory getDataFactory();
    
}
