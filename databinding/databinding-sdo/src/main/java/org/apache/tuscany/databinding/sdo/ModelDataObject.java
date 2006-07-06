package org.apache.tuscany.databinding.sdo;

import org.apache.tuscany.spi.model.ModelObject;

import commonj.sdo.DataObject;

public class ModelDataObject extends ModelObject {
    private DataObject dataObject;

    public ModelDataObject(DataObject dataObject) {
        super();
        this.dataObject = dataObject;
    }

    public DataObject getDataObject() {
        return dataObject;
    }
    
    
}
