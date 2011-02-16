package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;

public class BasePage extends WebPage {

    WebMarkupContainer body;

    public BasePage() {
        body = new WebMarkupContainer("body");
        body.add(new AttributeModifier("id", true, new Model<String>("home")));

        body.add(new BookmarkablePageLink("home", Homepage.class));
        body.add(new BookmarkablePageLink("profiledata", ProfileData.class));
        body.add(new BookmarkablePageLink("templategenerator", TemplateGenerator.class));

        add(body);
    }
}
