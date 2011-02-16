package at.ac.tuwien.view.profiledata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import at.ac.tuwien.BasePage;
import at.ac.tuwien.components.FormTemplate;
import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.domain.Profile;
import at.ac.tuwien.service.DBService;
import at.ac.tuwien.service.TemplateService;

public class EditFriends extends BasePage {

    private static final long serialVersionUID = 7734710518718389158L;

    @SpringBean(name = "DBService")
    private DBService dbService;

    @SpringBean(name = "TemplateService")
    private TemplateService templateService;

    private Form<Void> friendsDataForm;

    public EditFriends(PageParameters parameters) {
        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        final String uuid = parameters.get("id").toString();

        final List<KeyValueEntry> relatedFriends = new ArrayList<KeyValueEntry>();

        for (Profile profile : dbService.getRelatedProfiles(uuid)) {
            relatedFriends.add(new KeyValueEntry(profile.getValue("UUID"), (profile.getPrename() + " " + profile
                    .getSurname())));
        }

        final ListView<KeyValueEntry> friends = new ListView<KeyValueEntry>("friends", relatedFriends) {
            private static final long serialVersionUID = 7734710518717389159L;

            @Override
            protected void populateItem(ListItem<KeyValueEntry> item) {
                final KeyValueEntry entry = item.getModelObject();
                item.add(new Label("name", entry.getValue()));
                item.add(new AjaxFallbackLink("remove") {
                    private static final long serialVersionUID = -5910821785750463054L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        dbService.removeRelation(uuid, entry.getKey());
                        relatedFriends.remove(entry);
                        target.add(friendsDataForm);
                    }
                });
            }
        };

        friends.setOutputMarkupId(true);

        final List<KeyValueEntry> possibleFriends = new ArrayList<KeyValueEntry>();

        for (Profile profile : dbService.getProfiles()) {
            possibleFriends.add(new KeyValueEntry(profile.getValue("UUID"), (profile.getPrename() + " " + profile
                    .getSurname())));
        }

        final AutoCompleteTextField<KeyValueEntry> friendautocomplete = new AutoCompleteTextField<KeyValueEntry>(
                "newfriends", new Model<KeyValueEntry>()) {
            private static final long serialVersionUID = -8859788148650177034L;

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

        final Behavior autocomplete = new AjaxFormSubmitBehavior(friendsDataForm, "onchange") {
            private static final long serialVersionUID = 4679750146936906956L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {

                for (KeyValueEntry entry : possibleFriends) {
                    if (entry.getValue().equals(friendautocomplete.getModelObject())) {
                        if (dbService.addRelation(uuid, entry.getKey())) {
                            relatedFriends.add(entry);
                        }
                    }
                }
                target.add(friendsDataForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
            }
        };

        friendsDataForm = new FormTemplate("friendsDataForm") {
            private static final long serialVersionUID = -998287136496140172L;

            @Override
            public void setupForm() {
                friendautocomplete.add(autocomplete);
                add(friendautocomplete);
                setOutputMarkupId(true);
                add(friends);
            }

            @Override
            public void saveAction() {
            }
        };

        body.add(friendsDataForm);
        body.add(new DownloadLink("vcard", templateService.generateVcardExport(dbService.getRelatedProfiles(uuid))));
    }
}