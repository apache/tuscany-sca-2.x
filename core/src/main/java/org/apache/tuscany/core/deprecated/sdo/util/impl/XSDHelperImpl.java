package org.apache.tuscany.core.deprecated.sdo.util.impl;

import commonj.sdo.Property;
import commonj.sdo.Type;
import org.eclipse.emf.ecore.sdo.EProperty;
import org.eclipse.emf.ecore.sdo.EType;
import org.eclipse.emf.ecore.util.ExtendedMetaData;

import org.apache.tuscany.model.util.ConfiguredResourceSet;
import org.apache.tuscany.core.deprecated.sdo.util.XSDHelper;

public class XSDHelperImpl implements XSDHelper {
    private ConfiguredResourceSet configuredResourceSet;

    public XSDHelperImpl(ConfiguredResourceSet configuredResourceSet) {
        super();
        this.configuredResourceSet = configuredResourceSet;
    }

    public String getLocalName(Type type) {
        ExtendedMetaData metaData = configuredResourceSet.getExtendedMetaData();
        return metaData.getName(((EType) type).getEClassifier());
    }

    public String getLocalName(Property property) {
        ExtendedMetaData metaData = configuredResourceSet.getExtendedMetaData();
        return metaData.getName(((EProperty) property).getEStructuralFeature());
    }

    public String getNamespaceURI(Property property) {
        ExtendedMetaData metaData = configuredResourceSet.getExtendedMetaData();
        return metaData.getNamespace(((EProperty) property).getEStructuralFeature());
    }


}
