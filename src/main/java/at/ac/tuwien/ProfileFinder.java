package at.ac.tuwien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import at.ac.tuwien.components.FormTemplate;
import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.domain.Profile;
import at.ac.tuwien.service.APIService;
import at.ac.tuwien.service.DBService;

public class ProfileFinder extends BasePage {

    private static final long serialVersionUID = 7734810518718389158L;

    @SpringBean(name = "APIService")
    private APIService apiService;

    @SpringBean(name = "DBService")
    private DBService dbService;

    private Form<Void> activationForm;
    private Form<Void> queryForm;

    private Model<String> linkedInCode = new Model<String>();
    private ListView<String[]> linkedInUsers;
    private Model<String> twitterCode = new Model<String>();
    private ListView<String[]> twitterUsers;
    private Component XingUser;
    private Component FacebookUser;
    private ExternalLink linkedInLink;
    private ExternalLink twitterLink;

    public ProfileFinder() {
        body.add(new AttributeModifier("id", true, new Model<String>("profileFinder")));

        linkedInLink = new ExternalLink("linkedInLink", apiService.getLinkedInRequestURL(),
            "LinkedIn Verification");
        linkedInLink.setOutputMarkupId(true);

        twitterLink = new ExternalLink("twitterLink", apiService.getTwitterRequestURL(),
            "Twitter Verification");
        twitterLink.setOutputMarkupId(true);

        activationForm = new FormTemplate("activationForm") {
            private static final long serialVersionUID = 4775409828987880486L;

            @Override
            public void saveAction() {
                apiService.verifyLinkedIn(linkedInCode.getObject());
                apiService.verifyTwitter(twitterCode.getObject());

                queryForm.setVisible(true);
                activationForm.setVisible(false);
            }

            @Override
            public void setupForm() {
                RequiredTextField<String> linkedIn = new RequiredTextField<String>("linkedIn", linkedInCode);
                RequiredTextField<String> twitter = new RequiredTextField<String>("twitter", twitterCode);

                add(linkedIn, twitter);
                add(new ComponentFeedbackPanel("linkedInErrors", linkedIn));
                add(new ComponentFeedbackPanel("twitterErrors", twitter));
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

        twitterUsers = new ListView<String[]>("twitterUsers", dummyList) {
            private static final long serialVersionUID = 2265469743009974359L;

            @Override
            protected void populateItem(ListItem<String[]> item) {
                String[] profile = item.getModelObject();
                item.add(new Label("name", profile[1]));
                item.add(new ProfileImage("profile-image", new Model<String>(profile[2])));
            }
        };

        final List<KeyValueEntry> possibleFriends = new ArrayList<KeyValueEntry>();

        for (Profile profile : dbService.getProfiles()) {
            possibleFriends.add(new KeyValueEntry(profile.getValue("UUID"), (profile.getPrename() + " " + profile
                .getSurname())));
        }

        final AutoCompleteTextField<KeyValueEntry> userautocomplete = new AutoCompleteTextField<KeyValueEntry>(
            "user", new Model<KeyValueEntry>()) {
            private static final long serialVersionUID = -8859788147650177034L;

            @Override
            protected Iterator<KeyValueEntry> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    return Collections.EMPTY_LIST.iterator();
                }

                List<KeyValueEntry> choices = new ArrayList<KeyValueEntry>(10);

                for (final KeyValueEntry item : possibleFriends) {

                    if (item.getValue().toUpperCase().contains(input.toUpperCase())) {
                        choices.add(item);
                        if (choices.size() > 9) {
                            break;
                        }
                    }
                }
                return choices.iterator();
            }
        };

        final Button submit = new AjaxButton("submit", queryForm) {
            private static final long serialVersionUID = -5414315645065495551L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> queryForm) {

                String uuid = "";

                for (KeyValueEntry entry : possibleFriends) {
                    if (entry.getValue().equals(userautocomplete.getModelObject())) {
                        uuid = entry.getKey();
                        break;
                    }
                }

                if (uuid.equals("")) {
                    return;
                }

                twitterUsers.setList(apiService.executeTwitterQuery(uuid));
                linkedInUsers.setList(apiService.executeLinkedInQuery(uuid));

                XingUser = new WebMarkupContainer("XINGuser").add(new SimpleAttributeModifier("src",
                    apiService.excecuteXingQuery(uuid)));
                XingUser.setOutputMarkupId(true);
                queryForm.addOrReplace(XingUser);
                XingUser.setVisible(true);

                FacebookUser = new WebMarkupContainer("FacebookUser").add(new SimpleAttributeModifier("src",
                    apiService.executeFacebookQuery(uuid)));
                FacebookUser.setOutputMarkupId(true);
                queryForm.addOrReplace(FacebookUser);
                FacebookUser.setVisible(true);

                activationForm.setVisible(false);
                target.add(queryForm);
                target.add(activationForm);
            }

            @Override
            protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
            }
        };

        queryForm = new FormTemplate("queryForm") {
            private static final long serialVersionUID = -998287126496140172L;

            @Override
            public void setupForm() {
                add(submit);
                add(userautocomplete);
                setOutputMarkupId(true);
            }

            @Override
            public void saveAction() {
            }
        };

        linkedInUsers.setOutputMarkupId(true);
        queryForm.add(linkedInUsers);

        twitterUsers.setOutputMarkupId(true);
        queryForm.add(twitterUsers);

        XingUser = new WebMarkupContainer("XINGuser").add(new SimpleAttributeModifier("src",
            ""));
        XingUser.setVisible(false);
        XingUser.setOutputMarkupId(true);
        queryForm.add(XingUser);

        FacebookUser = new WebMarkupContainer("FacebookUser").add(new SimpleAttributeModifier("src",
            ""));
        FacebookUser.setVisible(false);
        FacebookUser.setOutputMarkupId(true);
        queryForm.add(FacebookUser);

        queryForm.setOutputMarkupId(true);
        queryForm.setVisible(false);

        if (apiService.alreadySet()) {
            activationForm.setVisible(false);
            linkedInLink.setVisible(false);
            twitterLink.setVisible(false);
            queryForm.setVisible(true);
        }
        body.add(queryForm);
        body.add(linkedInLink);
        body.add(twitterLink);
        body.add(activationForm);

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
