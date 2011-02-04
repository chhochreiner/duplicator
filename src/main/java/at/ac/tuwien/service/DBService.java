package at.ac.tuwien.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.domain.Profile;

public interface DBService {

    public void addProfile(List<KeyValueEntry> data);

    public String addProfile(File newFile);

    public List<Profile> getProfiles();

    public Map<String, Object> fetchProfileData(String uuid);
}
