package at.ac.tuwien.view.templateduplication;

import java.io.File;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import at.ac.tuwien.BasePage;
import at.ac.tuwien.components.FileUploadForm;

public class ImportTemplates extends BasePage {

    private static final long serialVersionUID = 7734810518718389158L;

    private static final String UPLOAD_FOLDER = "appdata/templates/";

    public ImportTemplates() {
        body.add(new AttributeModifier("id", true, new Model<String>("templategenerator")));

        if (!new File(UPLOAD_FOLDER).exists()) {
            new File(UPLOAD_FOLDER).mkdirs();
        }

        final Form<Void> uploadForm = new FileUploadForm("uploadForm", UPLOAD_FOLDER) {
            private static final long serialVersionUID = -4499523543968716298L;

            @Override
            public String additionalAction() {
                return "The template " + getFileName() + " was uploaded.";
            }
        };

        body.add(new UploadProgressBar("progress", uploadForm));
        body.add(uploadForm);
        body.add(new FeedbackPanel("feedback"));

    }
}