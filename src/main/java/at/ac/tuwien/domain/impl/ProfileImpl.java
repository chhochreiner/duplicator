package at.ac.tuwien.domain.impl;

import org.neo4j.graphdb.Node;

import at.ac.tuwien.domain.Profile;

public class ProfileImpl implements Profile {

    private final Node underlyingNode;
    private static final String KEY_PRENAME = "prename";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";

    public ProfileImpl(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    @Override
    public String getPrename() {
        return (String) underlyingNode.getProperty(KEY_PRENAME);
    }

    @Override
    public void setPrename(String prename) {
        underlyingNode.setProperty(KEY_PRENAME, prename);
    }

    @Override
    public String getSurname() {
        return (String) underlyingNode.getProperty(KEY_SURNAME);
    }

    @Override
    public void setSurname(String surname) {
        underlyingNode.setProperty(KEY_SURNAME, surname);
    }

    @Override
    public String getPassword() {
        return (String) underlyingNode.getProperty(KEY_PASSWORD);
    }

    @Override
    public void setPassword(String password) {
        underlyingNode.setProperty(KEY_PASSWORD, password);
    }

    @Override
    public String getEmail() {
        return (String) underlyingNode.getProperty(KEY_EMAIL);
    }

    @Override
    public void setEmail(String email) {
        underlyingNode.setProperty(KEY_EMAIL, email);
    }

    @Override
    public void setValue(String key, String value) {
        underlyingNode.setProperty(key, value);
    }

    @Override
    public String getValue(String key) {
        return (String) underlyingNode.getProperty(key);
    }

    @Override
    public String toString() {
        return (getPrename() + " " + getSurname());
    }

}
