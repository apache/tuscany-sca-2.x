package org.apache.tuscany.binding.celtix.handler.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import org.apache.tuscany.sdo.helper.XMLHelperImpl;
import org.objectweb.celtix.bindings.DataReader;
import org.objectweb.celtix.context.ObjectMessageContext;

public class NodeDataReader implements DataReader<Node> {

    SCADataBindingCallback callback;

    public NodeDataReader(SCADataBindingCallback cb) {
        callback = cb;
    }

    public Object read(int idx, Node input) {
        return read(null, idx, input);
    }

    public Object read(QName name, int idx, Node input) {
        //REVISIT - doc/lit and rpc/lit support

        return null;
    }

    public void readWrapper(ObjectMessageContext objCtx, boolean isOutBound, Node input) {
        try {
            QName wrapperName;
            if (isOutBound) {
                wrapperName = callback.getOperationInfo().getResponseWrapperQName();
            } else {
                wrapperName = callback.getOperationInfo().getRequestWrapperQName();
            }

            Node nd = input.getFirstChild();
            while (nd != null
                    && !wrapperName.getNamespaceURI().equals(nd.getNamespaceURI())
                    && !wrapperName.getLocalPart().equals(nd.getLocalName())) {
                nd = nd.getNextSibling();
            }

            //REVISIT - This is SUCH a HACK.  This needs to be done with StAX or something
            //a bit better than streaming and reparsing
            InputStream in = getNodeStream(nd);
            XMLDocument document = new XMLHelperImpl(callback.getTypeHelper()).load(in);
            DataObject object = document.getRootObject();

            List ips = object.getInstanceProperties();
            Object[] os = new Object[object.getInstanceProperties().size()];
            for (int i = 0; i < os.length; i++) {
                os[i] = object.get((Property)ips.get(i));
            }

            if (callback.hasInOut()) {
                //REVISIT - inOuts
            } else {
                objCtx.setReturn(os[0]);
            }
        } catch (IOException e) {
            throw new WebServiceException(e);
        } catch (ClassCastException e) {
            throw new WebServiceException(e);
        } catch (ClassNotFoundException e) {
            throw new WebServiceException(e);
        } catch (InstantiationException e) {
            throw new WebServiceException(e);
        } catch (IllegalAccessException e) {
            throw new WebServiceException(e);
        }
    }

    private InputStream getNodeStream(Node node)
        throws ClassCastException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {

        //This is also a hack, the JDK should already have this set, but it doesn't
        /*
                */
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        if (registry == null) {
            System.setProperty(DOMImplementationRegistry.PROPERTY,
                "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();
        }
        DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
        if (impl == null) {
            System.setProperty(DOMImplementationRegistry.PROPERTY,
                "com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
            registry = DOMImplementationRegistry.newInstance();            
            impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
        }
        LSOutput output = impl.createLSOutput();
        RawByteArrayOutputStream bout = new RawByteArrayOutputStream();
        output.setByteStream(bout);
        LSSerializer writer = impl.createLSSerializer();
        writer.write(node, output);

        return new ByteArrayInputStream(bout.getBytes(), 0, bout.size());
    }

}
