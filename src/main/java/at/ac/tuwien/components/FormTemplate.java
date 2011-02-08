package at.ac.tuwien.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;

public abstract class FormTemplate extends Form<Void> {

    private static final long serialVersionUID = 7459456400029741598L;
    protected Label error;
    protected Label success;

    public FormTemplate(String id) {
        super(id);
        setupForm();
        setupValidator();

        error = new Label("error", this.getLocalizer().getString("global.form.error", this));
        error.setVisible(false);
        add(error);
        success = new Label("success", "");
        success.setVisible(false);
        add(success);
    }

    @Override
    protected void onError() {
        success.setVisible(false);
        error.setVisible(true);
    }

    @Override
    protected void onSubmit() {
        saveAction();
        successMessage();

        success.setVisible(true);
        error.setVisible(false);
        resetModel();
    }

    abstract public void saveAction();

    public void successMessage() {
        success.setDefaultModelObject(getLocalizer().getString("success", this));
    }

    abstract public void setupForm();

    public void setupValidator() {

    }

    public void resetModel() {

    }

}
