package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;

public class Homepage extends BasePage {

    private static final long serialVersionUID = 7734810518718389158L;

    private static final String UPLOAD_FOLDER = "appdata/templates/";

    public Homepage() {
        body.add(new AttributeModifier("id", true, new Model<String>("home")));
        body.add(new BookmarkablePageLink<String>("batchImport", BatchImport.class));
        body.add(new BookmarkablePageLink<String>("importTemplate", ImportTemplates.class));

    }
}