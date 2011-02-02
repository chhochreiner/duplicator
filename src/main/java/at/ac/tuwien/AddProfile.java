package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import at.ac.tuwien.components.AdditionalInputForm;
import at.ac.tuwien.components.FormTemplate;
import at.ac.tuwien.service.DBService;

public class AddProfile extends BasePage {

    private static final long serialVersionUID = 7734710518718389158L;

    @SpringBean(name = "DBService")
    private DBService dbService;

    private Form<Void> profileDataForm;
    private AdditionalInputForm additionalFields;

    private Model<String> prenameModel = new Model<String>("asd");
    private Model<String> surnameModel = new Model<String>("asd");
    private Model<String> emailModel = new Model<String>("asd@asd.at");
    private Model<String> passwordModel = new Model<String>("asd");

    public AddProfile() {
        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        final FormComponent<String> prenameField = new RequiredTextField<String>("prename", prenameModel);
        final FormComponent<String> surnameField = new RequiredTextField<String>("surname", surnameModel);
        final FormComponent<String> emailField = new RequiredTextField<String>("email", emailModel);
        final FormComponent<String> passwordField = new RequiredTextField<String>("password", passwordModel);

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
                dbService.addProfile(prenameModel.getObject(), surnameModel.getObject(), passwordModel.getObject(),
                        emailModel.getObject(), additionalFields.getAdditionalValues());
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
