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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;

import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.domain.Profile;
import at.ac.tuwien.domain.impl.ProfileImpl;
import at.ac.tuwien.service.DBService;
import au.com.bytecode.opencsv.CSVReader;

public class DBServiceImpl implements DBService {

    @SpringBean(name = "graphDbService")
    public GraphDatabaseService graphDbService;

    @SpringBean(name = "indexService")
    private IndexService indexService;

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
        Node profile = fetchNode(uuid);
        if (profile == null) {
            return null;
        }

        Map<String, String> result = new HashMap<String, String>();

        for (String key : profile.getPropertyKeys()) {
            result.put(key, profile.getProperty(key).toString());
        }

        return result;
    }

    private Node fetchNode(String uuid) {
        Node profile = indexService.getSingleNode(INDEX, uuid);
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

            List<String> alreadyListet = new ArrayList<String>();
            alreadyListet.add("UUID");
            alreadyListet.add("birthday_year");
            alreadyListet.add("birthday_month_alpha");
            alreadyListet.add("birthday_month_without_null");
            alreadyListet.add("birthday_date_without_null");
            alreadyListet.add("birthday_date");
            alreadyListet.add("birthday_month");

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
    public void addProfile(List<KeyValueEntry> data) {
        Transaction tx = graphDbService.beginTx();
        try {
            Node node = graphDbService.createNode();
            Profile profile = new ProfileImpl(node);
            profile.setValue("UUID", UUID.randomUUID().toString());
            indexService.index(node, INDEX, profile.getValue("UUID"));

            for (KeyValueEntry entry : data) {
                profile.setValue(entry.getKey(), entry.getValue());
                splitBirthday(profile, entry);
            }

            tx.success();

        } finally {
            tx.finish();
        }
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

    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

}
