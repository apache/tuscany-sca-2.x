package org.apache.tuscany.sca.binding.atom;

import java.util.UUID;

import junit.framework.Assert;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.tuscany.sca.binding.atom.collection.Collection;
import org.apache.tuscany.sca.node.Client;
import org.apache.tuscany.sca.node.Contribution;
import org.apache.tuscany.sca.node.ContributionLocationHelper;
import org.apache.tuscany.sca.node.Node;
import org.apache.tuscany.sca.node.NodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AtomDeleteTestCase {
    protected static Node consumerNode;
    protected static Node providerNode;
    protected static CustomerClient testService;
    protected static Abdera abdera;

    @BeforeClass
    public static void init() throws Exception {
        try{
        System.out.println(">>>AtomDeleteTestCase.init entry");
        String contribution = ContributionLocationHelper.getContributionLocation(AtomDeleteTestCase.class);
        providerNode = NodeFactory.newInstance().createNode(
                                                               "org/apache/tuscany/sca/binding/atom/Provider.composite", new Contribution("provider", contribution));
        consumerNode = NodeFactory.newInstance().createNode(
                                                               "org/apache/tuscany/sca/binding/atom/Consumer.composite", new Contribution("consumer", contribution));
        providerNode.start();
        consumerNode.start();
        testService = consumerNode.getService(CustomerClient.class, "CustomerClient");
        abdera = new Abdera();
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        // System.out.println(">>>AtomDeleteTestCase.destroy entry");
        consumerNode.stop();
        consumerNode.destroy();
        providerNode.stop();
        providerNode.destroy();
    }

    @Test
    public void testPrelim() throws Exception {
        Assert.assertNotNull(providerNode);
        Assert.assertNotNull(consumerNode);
        Assert.assertNotNull(testService);
        Assert.assertNotNull(abdera);
    }

    @Test
    public void testAtomDelete() throws Exception {
        Collection resourceCollection = testService.getCustomerCollection();
        Assert.assertNotNull(resourceCollection);

        Entry postEntry = postEntry("Sponge Bob");
        System.out.println(">>> post entry= " + postEntry.getTitle());

        Entry newEntry = resourceCollection.post(postEntry);
        System.out.println("<<< Entry posted for " + newEntry.getTitle());

        System.out.println(">>> get id=" + newEntry.getId());

        resourceCollection.delete(newEntry.getId().toString());

    }

    @Test
    public void testAtomDeleteException() throws Exception {
        Collection resourceCollection = testService.getCustomerCollection();
        Assert.assertNotNull(resourceCollection);

        try {
            // Generates custom ID
            String id = "urn:uuid:customer-" + UUID.randomUUID().toString();
            resourceCollection.delete(id);
        } catch (Exception e) {
            // ID doesn't match with the existing IDs and NotFoundException is
            // thrown
            Assert.assertEquals("NotFoundException", e.getClass().getSimpleName());
        }

    }

    private Entry postEntry(String value) {
        Entry entry = abdera.newEntry();
        entry.setTitle("customer " + value);

        Content content = abdera.getFactory().newContent();
        content.setContentType(Content.Type.TEXT);
        content.setValue(value);
        entry.setContentElement(content);

        return entry;
    }

}
