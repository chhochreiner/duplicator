package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.neo4j.graphdb.GraphDatabaseService;

public class ProfileData extends BasePage {

    private GraphDatabaseService database;

    public ProfileData() {

        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        body.add(new BookmarkablePageLink("addProfile", AddProfile.class));

        // database = GraphDB.getDatabase();
        //
        // Index<Node> persons = database.index().forNodes("persons");
        //
        // for (Node person : persons.query("prename", "Franz")) {
        //
        // System.out.println(person.getProperty("lastname"));
        //
        // }

    }
}
