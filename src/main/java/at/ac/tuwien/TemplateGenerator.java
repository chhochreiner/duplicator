package at.ac.tuwien;

import java.io.File;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import at.ac.tuwien.service.TemplateService;

public class TemplateGenerator extends BasePage {

    private static final long serialVersionUID = -3746316599572680411L;

    @SpringBean(name = "TemplateService")
    private TemplateService templateService;
    File test;

    public TemplateGenerator() {

        body.add(new AttributeModifier("id", true, new Model<String>("templategenerator")));

        test = templateService.generateTest("muh", "uuid");

        body.add(new DownloadLink("template", new LoadableDetachableModel<File>() {
            private static final long serialVersionUID = -1486899377541253504L;

            @Override
            protected File load() {
                try {
                    return test;
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, "template.xml"));

        body.add(new Label("log", templateService.checkGeneratedTest(test)));
    }
}