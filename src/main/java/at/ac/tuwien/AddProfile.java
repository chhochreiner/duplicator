package at.ac.tuwien;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import at.ac.tuwien.components.AdditionalInputForm;
import at.ac.tuwien.components.FormTemplate;
import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.service.DBService;

public class AddProfile extends BasePage {

    private static final long serialVersionUID = 7734710518718389158L;

    @SpringBean(name = "DBService")
    private DBService dbService;

    private Form<Void> profileDataForm;
    private AdditionalInputForm additionalFields;

    private Model<String> prenameModel = new Model<String>();
    private Model<String> surnameModel = new Model<String>();
    private Model<String> emailModel = new Model<String>();
    private Model<String> passwordModel = new Model<String>();
    private Model<Date> birthdayModel = new Model<Date>();

    public AddProfile() {
        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        final FormComponent<String> prenameField = new RequiredTextField<String>("prename", prenameModel);
        final FormComponent<String> surnameField = new RequiredTextField<String>("surname", surnameModel);
        final FormComponent<String> emailField = new RequiredTextField<String>("email", emailModel);
        final FormComponent<String> passwordField = new RequiredTextField<String>("password", passwordModel);
        final DateField birthday = new DateField("birthday", birthdayModel);

        profileDataForm = new FormTemplate("profileDataForm") {
            private static final long serialVersionUID = -3481033707062528941L;

            @Override
            public void setupForm() {

                additionalFields = new AdditionalInputForm("additionalForm", null);
                add(additionalFields);

                add(prenameField, surnameField, emailField, passwordField, birthday);
                add(new ComponentFeedbackPanel("prenameErrors", prenameField));
                add(new ComponentFeedbackPanel("surnameErrors", surnameField));
                add(new ComponentFeedbackPanel("emailErrors", emailField));
                add(new ComponentFeedbackPanel("passwordErrors", passwordField));
                add(new ComponentFeedbackPanel("birthdayErrors", birthday));
            }

            @Override
            public void saveAction() {

                List<KeyValueEntry> values = additionalFields.getAdditionalValues();

                Format formatter = new SimpleDateFormat("dd.MM.yyyy");
                values.add(new KeyValueEntry("birthday", formatter.format(birthday.getDate())));

                values.add(new KeyValueEntry("prename", prenameModel.getObject()));
                values.add(new KeyValueEntry("surname", surnameModel.getObject()));
                values.add(new KeyValueEntry("email", emailModel.getObject()));
                values.add(new KeyValueEntry("password", passwordModel.getObject()));

                dbService.addProfile(values);
            }

            @Override
            public void successMessage() {
                success.setDefaultModelObject(getLocalizer().getString("success", this));
            }

            @Override
            public void setupValidator() {
                birthday.setRequired(true);
                emailField.add(EmailAddressValidator.getInstance());
            }

            @Override
            public void resetModel() {
                prenameModel = new Model<String>();
                surnameModel = new Model<String>();
                passwordModel = new Model<String>();
                emailModel = new Model<String>();
                birthdayModel = new Model<Date>();

                prenameField.setModel(prenameModel);
                surnameField.setModel(surnameModel);
                passwordField.setModel(passwordModel);
                emailField.setModel(emailModel);
                birthday.setModel(birthdayModel);
            }

        };

        body.add(profileDataForm);
    }
}
