package com.goddenis;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: goddenis
 * Date: 04.04.12
 */
public class Wrapper {
    DBF dbf;
    String tableName = "test";
    Map<Integer, Field> fields = new HashMap<Integer, Field>();
    private File outfile = new File("out.sql");

    public static void main(String args[]) throws TransformerException, ParserConfigurationException {

        Wrapper wrapper = new Wrapper();


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

        try {
            wrapper.readConfig(new File("D:\\Projects\\DbfWrapper\\DbfWraper\\TestData\\conf.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        String[] strings = null;
        try {
            strings = wrapper.readDbf();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (xBaseJException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            wrapper.writeFile(strings);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeFile(String[] strings) throws IOException {
        FileWriter writer = new FileWriter(this.outfile);
        for (int i = 1; i < strings.length; i++) {
            writer.write(strings[i] + ";\n");
        }
        writer.close();
    }

    public String[] readDbf() throws IOException, xBaseJException {
        String[] strings = new String[dbf.getRecordCount()];
        String header = "Insert into " + tableName + "(";
        String body = "";
        for (Integer i : fields.keySet()) {
            header = header + fields.get(i).getNewName() + (i != fields.size() ? ", " : " ) values (");
        }
        for (int r = 0; r < dbf.getRecordCount(); r++) {
            body = "";
            dbf.gotoRecord(r + 1);
            for (int c = 1; c <= dbf.getFieldCount(); c++) {
                if (dbf.getField(c).isCharField()) {
                    body = body + "'" + dbf.getField(c).get().trim() + "'";
                } else {
                    body = body + dbf.getField(c).get().trim();
                }
                body = body + (c == dbf.getFieldCount() ? ")" : ",");
            }
            strings[r] = header + body;
        }
        return strings;
    }

    public void readConfig(File file) throws IOException, SAXException {
        DOMParser parser = new DOMParser();
        parser.parse(file.getPath());

        Document dom = parser.getDocument();

        NodeList fieldsNodes = dom.getElementsByTagName("field");

        for (int i = 0; i < fieldsNodes.getLength(); i++) {
            Node aNode = fieldsNodes.item(i);

            NamedNodeMap attributes = aNode.getAttributes();

            Field field = new Field();
            field.setOldName(attributes.getNamedItem("old_name").getNodeValue());
            field.setNewName(attributes.getNamedItem("new_name").getNodeValue());
            fields.put(Integer.valueOf(attributes.getNamedItem("i").getNodeValue()), field);
        }

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
            setAttribute(node, "i", String.valueOf(i));
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
