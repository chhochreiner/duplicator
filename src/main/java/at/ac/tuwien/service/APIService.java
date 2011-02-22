package at.ac.tuwien.service;

import java.util.List;

import org.scribe.oauth.OAuthService;

public interface APIService {

    public String getLinkedInRequestURL();

    public void verifyLinkedIn(String code);

    public List<String[]> executeLinkedInQuery(String uuid);

    public OAuthService alreadySet();
}
