package at.ac.tuwien.view.profiledata;

import java.io.File;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import at.ac.tuwien.BasePage;
import at.ac.tuwien.components.FileUploadForm;

public class UploadImage extends BasePage {

    private static final long serialVersionUID = 7734710518708389158L;

    private static final String UPLOAD_FOLDER = "appdata/images/";

    public UploadImage(PageParameters parameters) {
        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        if (!new File(UPLOAD_FOLDER).exists()) {
            new File(UPLOAD_FOLDER).mkdirs();
        }

        final StringValue uuid = parameters.get("id");

        final Form<Void> uploadForm = new FileUploadForm("uploadForm", UPLOAD_FOLDER) {
            private static final long serialVersionUID = -4499523543968916298L;

            @Override
            public String additionalAction() {
                File file = new File(UPLOAD_FOLDER + uuid + ".jpg");

                newFile.renameTo(file);

                return "ok";
            }
        };

        body.add(new UploadProgressBar("progress", uploadForm));
        body.add(uploadForm);
        body.add(new FeedbackPanel("feedback"));

    }
}
