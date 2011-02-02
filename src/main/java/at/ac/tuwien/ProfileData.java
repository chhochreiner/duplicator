package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import at.ac.tuwien.domain.Profile;
import at.ac.tuwien.service.DBService;

public class ProfileData extends BasePage {

    @SpringBean(name = "DBService")
    private DBService dbService;

    public ProfileData() {

        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        body.add(new BookmarkablePageLink("addProfile", AddProfile.class));

        body.add(new ListView("profilelist", dbService.getProfiles()) {

            private static final long serialVersionUID = -8028393018074885955L;

            @Override
            protected void populateItem(ListItem item) {
                Profile profile = (Profile) item.getModelObject();
                item.add(new Label("name", profile.getPrename() + " " + profile.getSurname()));
                item.add(new Label("email", profile.getEmail()));

                PageParameters parameters = new PageParameters();

                parameters.add("id", profile.getValue("UUID"));

                item.add(new BookmarkablePageLink("action", ProfileData.class, parameters));
            }
        });

    }
}
