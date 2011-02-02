package at.ac.tuwien.service;

import java.util.List;

import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.domain.Profile;

public interface DBService {

    public void addProfile(String prename, String surname, String password, String email,
            List<KeyValueEntry> additionalValues);

    public List<Profile> getProfiles();
}
