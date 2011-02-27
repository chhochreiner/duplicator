package at.ac.tuwien.service;

import java.util.List;

public interface APIService {

    public String getLinkedInRequestURL();

    public void verifyLinkedIn(String code);

    public List<String[]> executeLinkedInQuery(String uuid);

    public String getTwitterRequestURL();

    public void verifyTwitter(String code);

    public List<String[]> executeTwitterQuery(String uuid);

    public String getFacebookRequestURL();

    public void verifyFacebook(String code);

    public List<String[]> executeFacebookQuery(String uuid);

    public boolean alreadySet();

    public String excecuteXingQuery(String uuid);
}
