package com.example.uhunt.sample1;

/**
 * Created by Mohaimen on 05-Dec-17.
 */

public class UserDetails {
    int _id;
    String _name;
    String _number;

    public UserDetails(){};
    public UserDetails(int id,String name,String number)
    {
        this._id=id;
        this._name=name;
        this._number=number;
    }
    public UserDetails(String name, String number){
        this._name = name;
        this._number = number;
    }

    public int get_id()
    {
        return this._id;
    }
    public String get_name()
    {
        return this._name;
    }
    public String get_number()
    {
        return this._number;
    }
    public void set_id(int id)
    {
        this._id=id;
    }
    public void set_name(String name)
    {
        this._name=name;
    }
    public void set_number(String number)
    {
        this._number=number;
    }

}
