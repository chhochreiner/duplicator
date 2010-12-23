package at.ac.tuwien.domain;

import java.io.Serializable;

import org.apache.wicket.model.Model;

/**
 * Simple key-value implementation required to be able at all to create specific
 * models for adding specific key and value access.
 */
public class KeyValueEntry implements Serializable {

    private static final long serialVersionUID = 6612645802752223746L;

    private Model<String> key = new Model<String>();
    private Model<String> value = new Model<String>();

    public KeyValueEntry() {
        super();
    }

    public String getKey() {
        return this.key.getObject();
    }

    public void setKey(String key) {
        this.key.setObject(key);
    }

    public String getValue() {
        return this.value.getObject();
    }

    public void setValue(String value) {
        this.value.setObject(value);
    }

    public Model<String> getKeyModel() {
        return this.key;
    }

    public Model<String> getValueModel() {
        return this.value;
    }

    @Override
    public String toString() {
        return "Key [" + getKey() + "] Value [" + getValue() + "]";
    }

}
