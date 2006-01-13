package org.apache.tuscany.core.injection;

import org.apache.tuscany.core.deprecated.sdo.util.CopyHelper;
import org.apache.tuscany.core.deprecated.sdo.util.impl.CopyHelperImpl;

import commonj.sdo.DataObject;

/**
 * Creates new instances of an SDO
 *
 * @version $Rev$ $Date$
 */
public class SDOObjectFactory implements ObjectFactory<DataObject> {

    private DataObject dataObject;

    private CopyHelper helper;

    //----------------------------------
    // Constructors
    //----------------------------------

    public SDOObjectFactory(DataObject dataObject) {
        this.dataObject = dataObject;
        helper = new CopyHelperImpl();
    }

    //----------------------------------
    // Methods
    //----------------------------------

    public DataObject getInstance() throws ObjectCreationException {
        return helper.copy(dataObject);
    }

    public void releaseInstance(DataObject instance) {
    }

}

