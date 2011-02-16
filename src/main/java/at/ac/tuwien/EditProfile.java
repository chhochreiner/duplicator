package at.ac.tuwien;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import at.ac.tuwien.components.AdditionalInputForm;
import at.ac.tuwien.components.FormTemplate;
import at.ac.tuwien.domain.KeyValueEntry;
import at.ac.tuwien.service.DBService;

public class EditProfile extends BasePage {

    private static final long serialVersionUID = 7734710518718389158L;

    @SpringBean(name = "DBService")
    private DBService dbService;

    private Form<Void> profileDataForm;
    private AdditionalInputForm additionalFields;

    private Model<String> prenameModel;
    private Model<String> surnameModel;
    private Model<String> emailModel;
    private Model<String> passwordModel;
    private Model<Date> birthdayModel;
    private Map<String, String> data;

    public EditProfile(PageParameters parameters) {
        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        StringValue uuid = parameters.get("id");

        data = dbService.fetchProfileData(uuid.toString());
        final List<KeyValueEntry> additionalvalues = new ArrayList<KeyValueEntry>();

        List<String> alreadyListet = new ArrayList<String>();
        alreadyListet.add("prename");
        alreadyListet.add("surname");
        alreadyListet.add("email");
        alreadyListet.add("password");
        alreadyListet.add("birthday");
        alreadyListet.add("UUID");
        alreadyListet.add("birthday_year");
        alreadyListet.add("birthday_month_alpha");
        alreadyListet.add("birthday_month_without_null");
        alreadyListet.add("birthday_date_without_null");
        alreadyListet.add("birthday_date");
        alreadyListet.add("birthday_month");

        if (data == null) {
            PageParameters parameter = new PageParameters();

            parameter.add("error", "Could not find a profile with UUID " + uuid.toString());
            throw new RestartResponseException(ErrorPage.class, parameter);
        }

        prenameModel = new Model<String>(data.get("prename"));
        surnameModel = new Model<String>(data.get("surname"));
        emailModel = new Model<String>(data.get("email"));
        passwordModel = new Model<String>(data.get("password"));

        try {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            birthdayModel = new Model<Date>(formatter.parse(data.get("birthday")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (String key : data.keySet()) {
            if (alreadyListet.contains(key)) {
                continue;
            }
            additionalvalues.add(new KeyValueEntry(key.toString(), data.get(key).toString()));
        }

        final FormComponent<String> prenameField = new RequiredTextField<String>("prename", prenameModel);
        final FormComponent<String> surnameField = new RequiredTextField<String>("surname", surnameModel);
        final FormComponent<String> emailField = new RequiredTextField<String>("email", emailModel);
        final FormComponent<String> passwordField = new RequiredTextField<String>("password", passwordModel);
        final DateField birthday = new DateField("birthday", birthdayModel);
        birthday.setDate(birthdayModel.getObject());

        profileDataForm = new FormTemplate("profileDataForm") {
            private static final long serialVersionUID = -3481033707062528941L;

            @Override
            public void setupForm() {

                additionalFields = new AdditionalInputForm("additionalForm", additionalvalues);
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

                // TODO update Profile

                List<KeyValueEntry> values = additionalFields.getAdditionalValues();

                Format formatter = new SimpleDateFormat("dd.MM.yyyy");
                values.add(new KeyValueEntry("birthday", formatter.format(birthday.getDate())));

                values.add(new KeyValueEntry("prename", prenameModel.getObject()));
                values.add(new KeyValueEntry("surname", surnameModel.getObject()));
                values.add(new KeyValueEntry("email", emailModel.getObject()));
                values.add(new KeyValueEntry("password", passwordModel.getObject()));

                dbService.editProfile(data.get("UUID").toString(), values);
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
            }

        };

        body.add(profileDataForm);
    }
}
