package at.ac.tuwien;

import java.io.File;
import java.util.UUID;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import at.ac.tuwien.service.DBService;

public class BatchImport extends BasePage {

    private static final long serialVersionUID = 7734710518718389158L;

    @SpringBean(name = "DBService")
    private DBService dbService;

    private static final String UPLOAD_FOLDER = ".appdata/uploads/";

    public BatchImport() {
        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        if (!new File(UPLOAD_FOLDER).exists()) {
            new File(UPLOAD_FOLDER).mkdirs();
        }

        final Form<Void> uploadForm = new FileUploadForm("uploadForm");
        body.add(new UploadProgressBar("progress", uploadForm));
        body.add(uploadForm);
        body.add(new FeedbackPanel("feedback"));

    }

    private class FileUploadForm extends Form<Void> {

        private static final long serialVersionUID = 6708616521685744826L;

        private FileUploadField fileUploadField;
        private File newFile;

        public FileUploadForm(final String name) {
            super(name);

            setMultiPart(true);
            add(this.fileUploadField = new FileUploadField("fileInput"));
        }

        @Override
        protected void onSubmit() {
            final FileUpload upload = this.fileUploadField.getFileUpload();
            if (upload != null) {
                newFile = new File(UPLOAD_FOLDER + UUID.randomUUID().toString());
                try {
                    newFile.createNewFile();
                    upload.writeTo(newFile);
                    info(dbService.addProfile(newFile));
                } catch (final Exception e) {
                    throw new IllegalStateException("Unable to write file");
                }
            } else {
                warn(getLocalizer().getString("uploadError", this));
            }
        }

    }

}
