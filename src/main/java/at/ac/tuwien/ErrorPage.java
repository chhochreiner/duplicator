package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ErrorPage extends WebPage {

    WebMarkupContainer body;

    public ErrorPage(PageParameters parameters) {
        body = new WebMarkupContainer("body");
        body.add(new AttributeModifier("id", true, new Model<String>("home")));

        body.add(new BookmarkablePageLink("home", ErrorPage.class));
        body.add(new BookmarkablePageLink("profiledata", ProfileData.class));
        body.add(new BookmarkablePageLink("templategenerator", TemplateGenerator.class));

        body.add(new Label("feedback", parameters.get("error").toString()));

        add(body);
    }
}
