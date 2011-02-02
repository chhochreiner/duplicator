package at.ac.tuwien.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.domain.Profile;
import at.ac.tuwien.domain.impl.ProfileImpl;
import at.ac.tuwien.service.DBService;

public class DBServiceImpl implements DBService {

    @SpringBean(name = "graphDbService")
    public GraphDatabaseService graphDbService;

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
            tx.success();

        } finally {
            tx.finish();

        }
    }

    public void setGraphDbService(GraphDatabaseService graphDbService) {
        this.graphDbService = graphDbService;
    }
}
