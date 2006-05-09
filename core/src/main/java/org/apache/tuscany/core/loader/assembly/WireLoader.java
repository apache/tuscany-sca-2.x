package org.apache.tuscany.core.loader.assembly;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.loader.LoaderContext;
import org.apache.tuscany.model.assembly.Wire;
import org.osoa.sca.annotations.Scope;

/**
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class WireLoader extends AbstractLoader {
    private static final String XSD = "http://www.w3.org/2001/XMLSchema";

    private static final Map<QName, Class<?>> TYPE_MAP;

    static {
        // todo support more XSD types, or remove if we store the QName
        TYPE_MAP = new HashMap<QName, Class<?>>(17);
        TYPE_MAP.put(new QName(XSD, "string"), String.class);
    }

    public QName getXMLType() {
        return AssemblyConstants.WIRE;
    }

    public Wire load(XMLStreamReader reader, LoaderContext loaderContext) throws XMLStreamException, ConfigurationLoadException {
        assert AssemblyConstants.WIRE.equals(reader.getName());
        Wire wire = factory.createWire();
        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    if (AssemblyConstants.WIRE_SOURCE.equals(qname)) {
                        String uri = reader.getElementText();
                        int pos = uri.indexOf('/');
                        if (pos < 1) {
                            throw new ConfigurationLoadException("Invalid source wire");
                        }
                        String partName = uri.substring(0, pos);
                        String portName = uri.substring(pos + 1);
                        wire.setSource(factory.createServiceURI(null, partName, portName));
                    } else if (AssemblyConstants.WIRE_TARGET.equals(qname)) {
                        String uri = reader.getElementText();
                        int pos = uri.indexOf('/');
                        if (pos < 1) {
                            wire.setTarget(factory.createServiceURI(null, uri));
                        }else{
                            String partName = uri.substring(0, pos);
                            String portName = uri.substring(pos + 1);
                            wire.setTarget(factory.createServiceURI(null, partName, portName));
                        }
                    }
                    break;
                case END_ELEMENT:
                    return wire;
            }
        }
    }
}
