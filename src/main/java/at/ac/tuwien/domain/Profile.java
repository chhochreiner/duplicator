package at.ac.tuwien.domain;

public interface Profile {

    public String getPrename();

    public void setPrename(String prename);

    public String getSurname();

    public void setSurname(String surname);

    public String getPassword();

    public void setPassword(String password);

    public String getEmail();

    public void setEmail(String email);

    public void setValue(String key, String value);

    public String getValue(String key);

}
