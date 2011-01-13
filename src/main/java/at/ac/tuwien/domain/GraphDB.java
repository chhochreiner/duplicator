package at.ac.tuwien.domain;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class GraphDB {

    static GraphDatabaseService graphDB = null;

    private GraphDB() {

    }

    public static GraphDatabaseService getDatabase() {
        if (graphDB == null) {
            graphDB = new EmbeddedGraphDatabase("neo4j-1.2/data/graph.db");
        }
        return graphDB;
    }

}
