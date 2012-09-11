package com.georges.grape.data;

public class Contact {

    public enum Type
    {
        PHONE,
        EMAIL,
        GRAPE
    }
    private Type type;
    private String contact;
    
    //used by JSON
    @SuppressWarnings("unused")
    private Contact()
    { }
    
    public Contact(Type type, String contact)
    {
        this.type=type;
        this.contact=contact;
    }
    public Type getType() {
        return type;
    }
    public String getContact() {
        return contact;
    }
    @Override
    public String toString() {
        return "Contact [type=" + type + ", contact=" + contact + "]";
    }
    
}
