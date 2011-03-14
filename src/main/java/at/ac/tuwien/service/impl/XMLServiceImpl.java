package at.ac.tuwien.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLServiceImpl {

    public List<String[]> parseLinkedInXML(String XML) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        List<String[]> result = new ArrayList<String[]>();

        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(XML));

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList persons = doc.getElementsByTagName("person");
            for (int i = 0; i < persons.getLength(); i++) {

                String[] buffer = new String[4];

                Element person = (Element) persons.item(i);
                NodeList id = person.getElementsByTagName("id");
                NodeList prename = person.getElementsByTagName("first-name");
                NodeList surname = person.getElementsByTagName("last-name");
                NodeList pictureURL = person.getElementsByTagName("picture-url");

                buffer[0] = id.item(0).getFirstChild().getTextContent();
                buffer[1] = prename.item(0).getFirstChild().getTextContent();
                buffer[2] = surname.item(0).getFirstChild().getTextContent();
                if (pictureURL.item(0) != null) {
                    buffer[3] = pictureURL.item(0).getFirstChild().getTextContent();
                }

                result.add(buffer);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String[]> parseTwitterXML(String XML) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        List<String[]> result = new ArrayList<String[]>();

        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(XML));

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList persons = doc.getElementsByTagName("user");
            for (int i = 0; i < persons.getLength(); i++) {

                String[] buffer = new String[4];

                Element person = (Element) persons.item(i);
                NodeList id = person.getElementsByTagName("id");
                NodeList name = person.getElementsByTagName("name");
                NodeList pictureURL = person.getElementsByTagName("profile_image_url");

                buffer[0] = id.item(0).getFirstChild().getTextContent();
                buffer[1] = name.item(0).getFirstChild().getTextContent();
                if (pictureURL.item(0) != null) {
                    buffer[2] = pictureURL.item(0).getFirstChild().getTextContent();
                }

                result.add(buffer);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
