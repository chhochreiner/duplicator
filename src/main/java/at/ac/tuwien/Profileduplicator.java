package at.ac.tuwien;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

public class Profileduplicator extends WebApplication {
    public Profileduplicator() {
        super();
    }

    @Override
    public Class<BasePage> getHomePage() {
        return BasePage.class;
    }

    @Override
    public void init() {
        super.init();
        super.getComponentInstantiationListeners().add(new SpringComponentInjector(this));
    }

}
