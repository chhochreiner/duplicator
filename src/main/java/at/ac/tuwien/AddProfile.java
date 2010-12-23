package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import at.ac.tuwien.components.AdditionalInputForm;
import at.ac.tuwien.components.FormTemplate;

public class AddProfile extends BasePage {

    private Form<Void> profileDataForm;
    private Form<Void> additionalFields;

    private Model<String> prenameModel = new Model<String>();
    private Model<String> surnameModel = new Model<String>();
    private Model<String> emailModel = new Model<String>();
    private Model<String> passwordModel = new Model<String>();

    public AddProfile() {
        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        final RequiredTextField<String> prenameField = new RequiredTextField<String>("prename", prenameModel);
        final RequiredTextField<String> surnameField = new RequiredTextField<String>("surname", surnameModel);
        final RequiredTextField<String> emailField = new RequiredTextField<String>("email", emailModel);
        final RequiredTextField<String> passwordField = new RequiredTextField<String>("password", passwordModel);

        profileDataForm = new FormTemplate("profileDataForm") {
            private static final long serialVersionUID = -3481033707062528941L;

            @Override
            public void setupForm() {

                additionalFields = new AdditionalInputForm("additionalForm", null);
                add(additionalFields);

                add(prenameField, surnameField, emailField, passwordField);
                add(new ComponentFeedbackPanel("prenameErrors", prenameField));
                add(new ComponentFeedbackPanel("surnameErrors", surnameField));
                add(new ComponentFeedbackPanel("emailErrors", emailField));
                add(new ComponentFeedbackPanel("passwordErrors", passwordField));
            }

            @Override
            public void saveAction() {
                // generate new User object

            }

            @Override
            public void successMessage() {
                success.setDefaultModelObject(getLocalizer().getString("success", this));
            }

            @Override
            public void setupValidator() {
                emailField.add(EmailAddressValidator.getInstance());
            }

            @Override
            public void resetModel() {
                prenameModel = new Model<String>();
                surnameModel = new Model<String>();
                passwordModel = new Model<String>();
                emailModel = new Model<String>();

                prenameField.setModel(prenameModel);
                surnameField.setModel(surnameModel);
                passwordField.setModel(passwordModel);
                emailField.setModel(emailModel);
            }

        };

        body.add(profileDataForm);
    }
}
