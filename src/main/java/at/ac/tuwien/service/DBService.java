package at.ac.tuwien.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.domain.Profile;

public interface DBService {

	public String addProfile(List<KeyValueEntry> data);

	public String addProfile(File newFile);

	public List<Profile> getProfiles();

	public Map<String, String> fetchProfileData(String uuid);

	public void editProfile(String uuid, List<KeyValueEntry> data);

	public boolean addRelation(String uuid1, String uuid2);

	public boolean removeRelation(String uuid1, String uuid2);

	public List<Profile> getRelatedProfiles(String uuid);
}
