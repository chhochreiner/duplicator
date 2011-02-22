package at.ac.tuwien.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import at.ac.tuwien.service.APIService;
import at.ac.tuwien.service.DBService;

public class APIServiceImpl implements APIService {

    @SpringBean(name = "DBService")
    public DBService dbService;

    private OAuthService linkedInService = null;
    private Token linkedInRequestToken;
    private Token linkedInAccessToken;

    private OAuthService twitterService;
    private Token twitterRequestToken;
    private Token twitterAccessToken;

    private OAuthService facebookService;
    private Token facebookRequestToken;
    private Token facebookAccessToken;

    public APIServiceImpl() {

        linkedInService = new ServiceBuilder()
            .provider(LinkedInApi.class)
            .apiKey("KbvaulneD9ML6w4hDfI16cx58LJx3vEudgiC_NWtLSkq6WpkhpINeZVrrKwVZKDE")
            .apiSecret("kFJXV98FOMibMfHSFk4vc_3wSA4YzXVVYLu9afXXfhsoqRe7FtUkmTqcYlY5c5hA")
            .build();
        linkedInRequestToken = linkedInService.getRequestToken();

        twitterService = new ServiceBuilder()
            .provider(TwitterApi.class)
            .apiKey("oDbrOUXFZz7Nc1MsHPtsbg")
            .apiSecret("ABWpSXT52gnVz9vagTvJhHvwJO1H2Ox6GzTRBZr0")
            .build();
        twitterRequestToken = twitterService.getRequestToken();
    }

    @Override
    public String getLinkedInRequestURL() {
        return linkedInService.getAuthorizationUrl(linkedInRequestToken);
    }

    @Override
    public void verifyLinkedIn(String code) {
        Verifier verifier = new Verifier(code);
        linkedInAccessToken = linkedInService.getAccessToken(linkedInRequestToken, verifier);
    }

    @Override
    public List<String[]> executeLinkedInQuery(String uuid) {

        // Map<String, String> data = dbService.fetchProfileData(uuid);

        Map<String, String> data = new HashMap<String, String>();

        data.put("prename", "Markus");
        data.put("surname", "Huber");
        // data.put("country-code", "Austria");

        String resource =
            "http://api.linkedin.com/v1/people-search";
        resource += ":(people:(id,first-name,last-name,picture-url))";
        resource += "?first-name=" + data.get("prename") + "&last-name="
                + data.get("surname") + "&count=10";

        if (data.containsKey("country-code")) {
            resource += "&country-code=" + data.get("country-code");
        }

        OAuthRequest request = new OAuthRequest(Verb.GET, resource);
        linkedInService.signRequest(linkedInAccessToken, request);
        Response response = request.send();

        return parseLinkedInXML(response.getBody());
    }

    public void setDbService(DBService dbService) {
        this.dbService = dbService;
    }

    private List<String[]> parseLinkedInXML(String XML) {

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Token alreadySet() {
        return linkedInAccessToken;
    }

    @Override
    public String excecuteXingQuery(String uuid) {
        return "https://www.xing.com/search/people?search%5Bq%5D=markus+huber&send=1";
    }

    @Override
    public String getTwitterRequestURL() {
        return twitterService.getAuthorizationUrl(twitterRequestToken);
    }

    @Override
    public void verifyTwitter(String code) {
        Verifier verifier = new Verifier(code);
        twitterAccessToken = twitterService.getAccessToken(twitterRequestToken, verifier);
    }

    @Override
    public List<String[]> executeTwitterQuery(String uuid) {
        // Map<String, String> data = dbService.fetchProfileData(uuid);

        Map<String, String> data = new HashMap<String, String>();

        data.put("prename", "Markus");
        data.put("surname", ".Huber");

        String resource =
            "http://api.twitter.com/1/users/search.xml?q=" + data.get("prename") + data.get("surname") + "&per_page=10";
        System.out.print(resource);
        OAuthRequest request = new OAuthRequest(Verb.GET, resource);
        twitterService.signRequest(twitterAccessToken, request);
        Response response = request.send();
        System.out.print(response.getBody());

        return parseTwitterXML(response.getBody());
    }

    private List<String[]> parseTwitterXML(String XML) {

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getFacebookRequestURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void verifyFacebook(String code) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String[]> executeFacebookQuery(String uuid) {
        // TODO Auto-generated method stub
        return null;
    }

}