package at.ac.tuwien;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import at.ac.tuwien.components.FormTemplate;
import at.ac.tuwien.service.APIService;

public class ProfileFinder extends BasePage {

    private static final long serialVersionUID = 7734810518718389158L;

    @SpringBean(name = "APIService")
    private APIService apiService;

    private Form<Void> activationForm;

    private Model<String> linkedInCode = new Model<String>();
    private ListView<String[]> linkedInUsers;

    public ProfileFinder() {

        body.add(new AttributeModifier("id", true, new Model<String>("profileFinder")));

        body.add(new ExternalLink("linkedInLink", apiService.getLinkedInRequestURL(),
            "LinkedIn Verification"));

        activationForm = new FormTemplate("activationForm") {
            private static final long serialVersionUID = 4775409828987880486L;

            @Override
            public void saveAction() {
                apiService.verifyLinkedIn(linkedInCode.getObject());
                linkedInUsers.setList(apiService.executeLinkedInQuery("uuid"));
            }

            @Override
            public void setupForm() {
                RequiredTextField<String> linkedIn = new RequiredTextField<String>("linkedIn", linkedInCode);

                add(linkedIn);
                add(new ComponentFeedbackPanel("linkedInErrors", linkedIn));
            }
        };

        List<String[]> dummyList = new ArrayList<String[]>();

        linkedInUsers = new ListView<String[]>("linkedInUsers", dummyList) {
            private static final long serialVersionUID = 2265469743009974359L;

            @Override
            protected void populateItem(ListItem<String[]> item) {
                String[] profile = item.getModelObject();
                item.add(new Label("name", profile[1] + " " + profile[2]));
                item.add(new ProfileImage("profile-image", new Model<String>(profile[3])));
            }
        };

        if (apiService.alreadySet() != null) {
            activationForm.setVisible(false);
        }
        body.add(activationForm);

        linkedInUsers.setOutputMarkupId(true);
        body.add(linkedInUsers);
    }

    private class ProfileImage extends WebComponent {
        private static final long serialVersionUID = -2765370962595118048L;

        public ProfileImage(String id, IModel<String> model) {
            super(id, model);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            checkComponentTag(tag, "img");
            tag.put("src", getDefaultModelObjectAsString());
        }
    }
}
