package org.apache.tuscany.databinding.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.helper.CopyHelper;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.ObjectCreationException;

/**
 * Creates new instances of an SDO
 *
 * @version $Rev: 418257 $ $Date: 2006-06-30 06:16:24 -0700 (Fri, 30 Jun 2006) $
 */
public class SDOObjectFactory implements ObjectFactory<DataObject> {

    private DataObject dataObject;

    public SDOObjectFactory(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public DataObject getInstance() throws ObjectCreationException {
        return CopyHelper.INSTANCE.copy(dataObject);
    }

    public void releaseInstance(DataObject instance) {
    }

}

