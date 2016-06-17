package com.androidsample.applevantamientoinventario;

/**
 * Created by Nestor on 6/16/2016.
 */
public class CodeValue {

    private Object Code;
    private String Value;

    public CodeValue(Object code, String value) {
        Code = code;
        Value = value;
    }

    public Object getCode() {
        return Code;
    }

    public String getValue() {
        return Value;
    }

    @Override
    public String toString() {
        return Value;
    }
}
