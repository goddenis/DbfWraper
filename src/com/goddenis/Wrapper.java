package com.goddenis;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: goddenis
 * Date: 04.04.12
 */
public class Wrapper {
    DBF dbf;

    public static void main(String args[]) throws TransformerException, ParserConfigurationException {

        Wrapper wrapper = new Wrapper();

        ArrayList<String> strings = new ArrayList<String>();

        try {
            wrapper.dbf = new DBF("D:\\Projects\\DbfWrapper\\DbfWraper\\TestData\\test.dbf");
        } catch (xBaseJException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            wrapper.composeXML();
        } catch (xBaseJException e) {
            e.printStackTrace();
        }
        int i = 1;

    }

    public void composeXML() throws TransformerException, ParserConfigurationException, xBaseJException {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = fact.newDocumentBuilder();
        Document doc = parser.newDocument();

        Node root = doc.createElement("conf");
        doc.appendChild(root);

        Node connectionConf = doc.createElement("connection_conf");
        root.appendChild(connectionConf);

        Node host = doc.createElement("host");
        connectionConf.appendChild(host);
        host.appendChild(doc.createTextNode("127.0.0.1"));

        Node base = doc.createElement("base");
        connectionConf.appendChild(base);
        base.appendChild(doc.createTextNode("test_base"));

        Node user = doc.createElement("user");
        connectionConf.appendChild(user);
        user.appendChild(doc.createTextNode("goddenis"));

        Node fildsConf = doc.createElement("filds_conf");
        root.appendChild(fildsConf);

        for (int i = 1; i <= dbf.getFieldCount(); i++) {
            Node node = doc.createElement("field");
            fildsConf.appendChild(node);
            setAttribute(node, "old_name", dbf.getField(i).getName());
            setAttribute(node, "new_name", "stub");
        }

        DOMSource domSource = new DOMSource(doc);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        Result dest = new StreamResult(new File("conf.xml"));
        transformer.transform(domSource, dest);
    }

    public static void setAttribute(Node node, String attName, String val) {
        NamedNodeMap attributes = node.getAttributes();
        Node attNode = node.getOwnerDocument().createAttribute(attName);
        attNode.setNodeValue(val);
        attributes.setNamedItem(attNode);
    }
}
