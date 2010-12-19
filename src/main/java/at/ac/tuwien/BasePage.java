package at.ac.tuwien;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class BasePage extends WebPage {

    WebMarkupContainer body;

    public BasePage() {
        body = new WebMarkupContainer("body");

        body.add(new BookmarkablePageLink("userdata", UserData.class));
        body.add(new BookmarkablePageLink("templategenerator", TemplateGenerator.class));

        add(body);
    }
}
