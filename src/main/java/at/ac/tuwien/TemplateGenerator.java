package at.ac.tuwien;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

public class TemplateGenerator extends BasePage {

    public TemplateGenerator() {

        body.add(new AttributeModifier("id", true, new Model<String>("templategenerator")));

        body.add(new Label("label1", "This is in the subclass templategenerator"));

    }

}