package com.sietecerouno.atlantetransportador.utils;

/**
 * Created by gio on 14/9/16.
 */
public class CustomObj
{
    public String value;
    public String value2;
    public int valueInt;
    public int valueInt2;
    public int valueInt3;
    public String name;
    public String img;
    public Boolean selected;

    public CustomObj(String _name, String _value, String _img, Boolean _selected, String _value2, int _valueInt, int _valueInt2, int _valueInt3)
    {
        this.name = _name;
        this.value = _value;
        this.value2 = _value2;
        this.valueInt = _valueInt;
        this.valueInt2 = _valueInt2;
        this.valueInt3 = _valueInt3;
        this.img = _img;
        this.selected = _selected;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.name.toString();
    }
}

