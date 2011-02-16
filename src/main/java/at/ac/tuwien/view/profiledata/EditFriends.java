package at.ac.tuwien.view.profiledata;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import at.ac.tuwien.BasePage;
import at.ac.tuwien.service.DBService;

public class EditFriends extends BasePage {

    private static final long serialVersionUID = 7734710518718389158L;

    @SpringBean(name = "DBService")
    private DBService dbService;

    public EditFriends(PageParameters parameters) {
        body.add(new AttributeModifier("id", true, new Model<String>("profiledata")));

        StringValue uuid = parameters.get("id");

    }

}