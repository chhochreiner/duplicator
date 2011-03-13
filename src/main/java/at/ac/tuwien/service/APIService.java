package at.ac.tuwien.service;

import java.util.List;

public interface APIService {

    public String getLinkedInRequestURL();

    public void verifyLinkedIn(String code);

    public List<String[]> executeLinkedInQuery(String uuid);

    public String getTwitterRequestURL();

    public void verifyTwitter(String code);

    public List<String[]> executeTwitterQuery(String uuid);

    public List<String[]> executeFacebookQuery(String uuid);

    public String getFacebookQuery(String uuid);

    public String getLinkedinQuery(String uuid);

    public boolean alreadySet();

    public String excecuteXingQuery(String uuid);

    public void setFacebookToken(String token);

    public boolean checkNetworkId(String network, String uuid);
}
