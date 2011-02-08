package at.ac.tuwien;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import at.ac.tuwien.components.FormTemplate;
import at.ac.tuwien.domain.Profile;
import at.ac.tuwien.service.DBService;
import at.ac.tuwien.service.TemplateService;

public class TemplateGenerator extends BasePage {

    private static final long serialVersionUID = -3746316599572680411L;

    @SpringBean(name = "TemplateService")
    private TemplateService templateService;

    @SpringBean(name = "DBService")
    private DBService dbService;

    private List<HelperContainer> userdata;
    private List<HelperContainer> templatedata;
    private Palette<HelperContainer> templates;
    private Form<Void> templateDataForm;
    private ListChoice<HelperContainer> user;
    private IChoiceRenderer<HelperContainer> renderer;
    private Model<HelperContainer> selectedUser;
    private Label log;
    private DownloadLink download;

    public TemplateGenerator() {
        body.add(new AttributeModifier("id", true, new Model<String>("templategenerator")));
        body.add(new BookmarkablePageLink<String>("importTemplate", ImportTemplates.class));

        generateUserData();
        generateTemplateData();

        selectedUser = new Model<HelperContainer>();
        user = new ListChoice<HelperContainer>("user", selectedUser, userdata);

        renderer = new ChoiceRenderer<HelperContainer>("name", "name");
        templates = new Palette<HelperContainer>("templates", new ListModel<HelperContainer>(
                new ArrayList<HelperContainer>()), new CollectionModel<HelperContainer>(templatedata), renderer, 10,
                true);

        templateDataForm = new FormTemplate("templateDataForm") {
            private static final long serialVersionUID = -3481033707162528991L;
            private boolean generated = false;

            @Override
            public void setupForm() {
                user.setMaxRows(7);
                add(user, templates);
                add(new ComponentFeedbackPanel("userErrors", user));
                add(new ComponentFeedbackPanel("templateErrors", templates));
            }

            @Override
            public void saveAction() {
                final List<File> generatedFiles = new ArrayList<File>();
                String logText = "";
                Iterator<HelperContainer> selected = templates.getSelectedChoices();

                while (selected.hasNext()) {
                    generated = true;
                    File test = templateService.generateTest(selected.next().name, selectedUser.getObject().UUID);
                    generatedFiles.add(test);
                    logText += test.getName() + " -- " + templateService.checkGeneratedTest(test) + "<br />";
                }

                if (!generated) {
                    templates.error("You have to select at least one template.");
                }

                download = new DownloadLink("template", new LoadableDetachableModel<File>() {
                    private static final long serialVersionUID = -1486899377541253504L;

                    @Override
                    protected File load() {
                        try {
                            return generateReturnFile(generatedFiles);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });

                download.setVisibilityAllowed(true);
                body.addOrReplace(download);

                log = new Label("log", logText);
                log.setEscapeModelStrings(false);
                body.addOrReplace(log);
            }

            private File generateReturnFile(final List<File> generatedFiles) {
                final File returnfile;
                if (generatedFiles.size() > 1) {
                    returnfile = templateService.createTestSuiteZip(generatedFiles);
                } else {
                    returnfile = generatedFiles.get(0);
                }
                return returnfile;
            }

            @Override
            protected void onSubmit() {
                saveAction();
                successMessage();

                success.setVisible(true);
                error.setVisible(false);
                resetModel();
                if (!generated) {
                    triggerError();
                }
            }

            private void triggerError() {
                success.setVisible(false);
                error.setVisible(true);
            }

            @Override
            public void successMessage() {
                success.setDefaultModelObject(getLocalizer().getString("success", this));
            }

            @Override
            public void setupValidator() {
                user.setRequired(true);
            }

            @Override
            public void resetModel() {
            }
        };

        body.add(templateDataForm);
        log = new Label("log", "");
        download = new DownloadLink("template", new File("dummy"));
        download.setVisibilityAllowed(false);
        body.add(download);
        body.add(log);
    }

    private void generateTemplateData() {
        File dir = new File("appdata/templates/");
        templatedata = new ArrayList<HelperContainer>();

        for (String file : dir.list()) {
            templatedata.add(new HelperContainer(file, file));
        }
    }

    private void generateUserData() {
        userdata = new ArrayList<HelperContainer>();

        for (Profile user : dbService.getProfiles()) {
            userdata.add(new HelperContainer(user.toString(), user.getValue("UUID")));
        }
    }

    private class HelperContainer implements Serializable {

        private static final long serialVersionUID = -3746316599572680421L;
        private String name;
        private String UUID;

        public HelperContainer(String name, String UUID) {
            this.name = name;
            this.UUID = UUID;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}