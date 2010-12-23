package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;

public class ProfileData extends BasePage {

    public ProfileData() {

        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        body.add(new BookmarkablePageLink("addProfile", AddProfile.class));

    }

}
