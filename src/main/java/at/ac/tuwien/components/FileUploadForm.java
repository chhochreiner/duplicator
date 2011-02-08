package at.ac.tuwien.components;

import java.io.File;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;

public class FileUploadForm extends Form<Void> {

    private static final long serialVersionUID = 6708616521685744826L;

    private FileUploadField fileUploadField;
    protected File newFile;
    private String UPLOAD_FOLDER;

    public FileUploadForm(final String name, String UPLOAD_FOLDER) {
        super(name);
        this.UPLOAD_FOLDER = UPLOAD_FOLDER;

        setMultiPart(true);
        add(this.fileUploadField = new FileUploadField("fileInput"));
    }

    @Override
    protected void onSubmit() {
        final FileUpload upload = this.fileUploadField.getFileUpload();
        if (upload != null) {
            newFile = new File(UPLOAD_FOLDER + upload.getClientFileName());
            try {
                newFile.createNewFile();
                upload.writeTo(newFile);
                info(additionalAction());
            } catch (final Exception e) {
                throw new IllegalStateException("Unable to write file");
            }
        } else {
            warn(getLocalizer().getString("uploadError", this));
        }
    }

    public String additionalAction() {
        return "";
    }

    public String getFileName() {
        return newFile.getName();
    }

}
