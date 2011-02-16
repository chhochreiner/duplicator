package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;

import at.ac.tuwien.view.profiledata.ProfileData;
import at.ac.tuwien.view.templateduplication.TemplateGenerator;

public class BasePage extends WebPage {
    private static final long serialVersionUID = -1953236807233668105L;
    protected WebMarkupContainer body;

    public BasePage() {
        body = new WebMarkupContainer("body");
        body.add(new AttributeModifier("id", true, new Model<String>("home")));

        body.add(new BookmarkablePageLink("home", Homepage.class));
        body.add(new BookmarkablePageLink("profiledata", ProfileData.class));
        body.add(new BookmarkablePageLink("templategenerator", TemplateGenerator.class));

        add(body);
    }
}
