package at.ac.tuwien.service.impl;

import java.util.ArrayList;
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

public class DBServiceImpl implements DBService {

    @SpringBean(name = "graphDbService")
    public GraphDatabaseService graphDbService;

    @SpringBean(name = "indexService")
    private IndexService indexService;

    private static final String INDEX = "UUID";

    @Override
    public void addProfile(String prename, String surname, String password, String email,
            List<KeyValueEntry> additionalValues) {
        Transaction tx = graphDbService.beginTx();
        try {
            Node node = graphDbService.createNode();

            Profile profile = new ProfileImpl(node);
            profile.setEmail(email);
            profile.setPassword(password);
            profile.setPrename(prename);
            profile.setSurname(surname);

            profile.setValue("UUID", UUID.randomUUID().toString());

            List<KeyValueEntry> additional = additionalValues;

            for (KeyValueEntry entry : additional) {
                profile.setValue(entry.getKey(), entry.getValue());
            }

            indexService.index(node, INDEX, profile.getValue("UUID"));

            tx.success();

        } finally {
            tx.finish();
        }
    }

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
    public Map<String, Object> fetchProfileData(String uuid) {
        Node profile = indexService.getSingleNode(INDEX, uuid);
        if (profile == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<String, Object>();

        for (String key : profile.getPropertyKeys()) {
            result.put(key, profile.getProperty(key));
        }

        return result;
    }

    public void setGraphDbService(GraphDatabaseService graphDbService) {
        this.graphDbService = graphDbService;
    }

    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

}
