package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public class UserData extends BasePage {

    public UserData() {

        body.add(new AttributeModifier("id", true, new Model<String>("userdata")));

        body.add(new Label("label1", "This is in the subclass Page1"));
    }

}
