package at.ac.tuwien.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import at.ac.tuwien.GeneralConstants;
import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.domain.Profile;
import at.ac.tuwien.domain.impl.ProfileImpl;
import at.ac.tuwien.service.DBService;
import au.com.bytecode.opencsv.CSVReader;

public class DBServiceImpl implements DBService {

	enum MyRelationshipTypes implements RelationshipType {
		KNOWS
	}

	@SpringBean(name = "graphDbService")
	public GraphDatabaseService graphDbService;

	private Index<Node> profiles = null;

	private static final String INDEX = "UUID";

	@Override
	public List<Profile> getProfiles() {
		List<Profile> profiles = new ArrayList<Profile>();

		for (Node node : graphDbService.getAllNodes()) {
			if (node.hasProperty("UUID")) {
				profiles.add(new ProfileImpl(node));
			}
		}

		return profiles;
	}

	@Override
	public Map<String, String> fetchProfileData(String uuid) {
		Map<String, String> result = new HashMap<String, String>();

		Node profile = fetchNode(uuid);
		if (profile == null) {
			String[] name = uuid.split("\\s");
			result.put("prename", " ");
			result.put("surname", " ");
			if (name.length > 0) {
				result.put("prename", name[0]);
			}
			if (name.length > 1) {
				result.put("surname", name[1]);
			}

			return result;
		}

		for (String key : profile.getPropertyKeys()) {
			result.put(key, profile.getProperty(key).toString());
		}

		return result;
	}

	private Node fetchNode(String uuid) {
		setIndex();
		Node profile = profiles.get(INDEX, uuid).getSingle();
		if (profile == null) {
			return null;
		}
		return profile;
	}

	@Override
	public void editProfile(String uuid, List<KeyValueEntry> data) {
		Transaction tx = graphDbService.beginTx();
		try {
			Node node = fetchNode(uuid);
			Profile profile = new ProfileImpl(node);
			List<String> existing = new ArrayList<String>();

			for (String key : node.getPropertyKeys()) {
				existing.add(key);
			}

			for (KeyValueEntry entry : data) {
				profile.setValue(entry.getKey(), entry.getValue());
				splitBirthday(profile, entry);
				existing.remove(entry.getKey());
			}

			List<String> alreadyListet = GeneralConstants.getBlacklistedKeys();

			for (String key : existing) {
				if (!alreadyListet.contains(key)) {
					node.removeProperty(key);
				}
			}

			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public String addProfile(List<KeyValueEntry> data) {
		setIndex();
		Transaction tx = graphDbService.beginTx();
		String uuid = UUID.randomUUID().toString();
		try {
			Node node = graphDbService.createNode();
			Profile profile = new ProfileImpl(node);
			profile.setValue("UUID", uuid);
			profiles.add(node, INDEX, profile.getValue(INDEX));

			for (KeyValueEntry entry : data) {
				profile.setValue(entry.getKey(), entry.getValue());
				splitBirthday(profile, entry);
			}

			tx.success();

		} finally {
			tx.finish();
		}
		return uuid;
	}

	private void splitBirthday(Profile profile, KeyValueEntry entry) {
		if (entry.getKey().equals("birthday")) {
			String[] birthday = entry.getValue().split("\\.");
			profile.setValue("birthday_date", birthday[0]);
			profile.setValue("birthday_month", birthday[1]);
			profile.setValue("birthday_year", birthday[2]);
			DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			DateFormat formatterSingleDate = new SimpleDateFormat("d");
			DateFormat formatterSingleMonth = new SimpleDateFormat("M");
			DateFormat formatterMonth = new SimpleDateFormat("MMM");
			try {
				Date birthdayDate = formatter.parse(entry.getValue());
				profile.setValue("birthday_date_without_null", formatterSingleDate.format(birthdayDate));
				profile.setValue("birthday_month_without_null", formatterSingleMonth.format(birthdayDate));
				profile.setValue("birthday_month_alpha", formatterMonth.format(birthdayDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String addProfile(File newFile) {
		String[] nextLine;
		Map<Integer, String> header = null;
		CSVReader reader;

		try {
			reader = new CSVReader(new FileReader(newFile));

			nextLine = reader.readNext();
			if (nextLine.length < 2) {
				return "The profiles could no be added, because the file is empty";
			} else {

				header = new HashMap<Integer, String>();
				for (int i = 0; i < nextLine.length; i++) {
					header.put(i, nextLine[i]);
				}

				if (!header.containsValue("prename") || !header.containsValue("surname")
						|| !header.containsValue("email") || !header.containsValue("password")
						|| !header.containsValue("birthday")) {
					return "There is at least one of the required fields missing.";
				} else {

					List<KeyValueEntry> newData;
					Integer counter = 0;

					while ((nextLine = reader.readNext()) != null) {
						newData = new ArrayList<KeyValueEntry>();
						for (int i = 0; i < nextLine.length; i++) {
							newData.add(new KeyValueEntry(header.get(i), nextLine[i]));
						}
						addProfile(newData);
						counter++;
					}
					return (counter + " new entries were made");
				}
			}

		} catch (FileNotFoundException e) {
			return "The file was not found";
		} catch (IOException e) {
			return "The file could not be uploaded due to an internal error.";
		}
	}

	public void setGraphDbService(GraphDatabaseService graphDbService) {
		this.graphDbService = graphDbService;
	}

	@Override
	public boolean addRelation(String uuid1, String uuid2) {
		if (uuid1.equals(uuid2)) {
			return false;
		}
		Node node1 = fetchNode(uuid1);
		Node node2 = fetchNode(uuid2);

		for (Relationship rel : node1.getRelationships(MyRelationshipTypes.KNOWS, Direction.BOTH)) {
			if (rel.getOtherNode(node1).getProperty("UUID").equals(node2.getProperty("UUID"))) {
				return false;
			}
		}

		Transaction tx = graphDbService.beginTx();
		try {

			node1.createRelationshipTo(node2, MyRelationshipTypes.KNOWS);
			tx.success();
		} finally {
			tx.finish();
		}
		return true;
	}

	@Override
	public List<Profile> getRelatedProfiles(String uuid) {
		Node node = fetchNode(uuid);
		List<Profile> profiles = new ArrayList<Profile>();

		for (Relationship rel : node.getRelationships(MyRelationshipTypes.KNOWS, Direction.BOTH)) {
			profiles.add(new ProfileImpl(rel.getOtherNode(node)));
		}

		return profiles;
	}

	@Override
	public boolean removeRelation(String uuid1, String uuid2) {

		Node node1 = fetchNode(uuid1);

		for (Relationship rel : node1.getRelationships(MyRelationshipTypes.KNOWS, Direction.BOTH)) {
			if (rel.getOtherNode(node1).getProperty("UUID").equals(uuid2)) {
				Transaction tx = graphDbService.beginTx();
				try {
					rel.delete();
					tx.success();
				} finally {
					tx.finish();
				}
				return true;
			}
		}
		return false;
	}

	private void setIndex() {
		if (profiles == null) {
			profiles = graphDbService.index().forNodes(INDEX);
		}
	}
}
