package at.ac.tuwien.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import at.ac.tuwien.domain.KeyValueEntry;

public class AdditionalInputForm extends Form<Void> {

    private static final long serialVersionUID = 5843313862787254558L;

    private ListView<KeyValueEntry> elementView;
    private List<KeyValueEntry> entries;

    public AdditionalInputForm(final String id, List<KeyValueEntry> data) {
        super(id);

        if (data != null) {
            this.entries = data;
        } else {
            this.entries = new ArrayList<KeyValueEntry>();
        }

        setOutputMarkupId(true);

        this.elementView = new ListView<KeyValueEntry>("customFields", entries) {

            private static final long serialVersionUID = -6012822316820493831L;

            @Override
            protected void populateItem(final ListItem<KeyValueEntry> item) {
                item.add(new TextField<String>("ownField.id", item.getModelObject().getKeyModel()));
                item.add(new TextField<String>("ownField.value", item.getModelObject().getValueModel()));

                item.add(new AjaxButton("removeField") {
                    private static final long serialVersionUID = 8086971193734197987L;

                    @Override
                    protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                        entries.remove(item.getModelObject());
                        target.add(form);
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                    }
                });

            }
        };
        this.elementView.setOutputMarkupId(true);
        add(this.elementView);

        add(new AjaxButton("addField", this) {

            private static final long serialVersionUID = -6012822316820493832L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                entries.add(new KeyValueEntry());
                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });

        add(new AjaxButton("removeField", this) {
            private static final long serialVersionUID = -6012822316820493833L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                if (entries.size() > 0) {
                    entries.remove(entries.size() - 1);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }

        });
    }

    public List<KeyValueEntry> getAdditionalValues() {
        final List<KeyValueEntry> retVal = new ArrayList<KeyValueEntry>();

        for (final KeyValueEntry keyValueEntry : (this.entries)) {
            if (keyValueEntry.getKey() != null) {
                if (keyValueEntry.getValue() == null) {
                    keyValueEntry.setValue("");
                }
                retVal.add(keyValueEntry);
            }
        }

        return retVal;
    }

}
