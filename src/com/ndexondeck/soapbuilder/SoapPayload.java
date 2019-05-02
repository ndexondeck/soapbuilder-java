package com.ndexondeck.soapbuilder;


import com.ndexondeck.soapbuilder.interfaces.ISoapPayload;
import com.ndexondeck.soapbuilder.utils.HtmlUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class SoapPayload extends SoapBuilder implements ISoapPayload
{

    private String value = "";

    private Map<String,String> element_attributes = new LinkedHashMap<>();

    public SoapPayload() {}

    public SoapPayload(Object value)
    {
        this.value = HtmlUtils.htmlEscape(value.toString());
    }

    public SoapPayload(Object value, Map<String,String> element_attributes)
    {
        this.value = HtmlUtils.htmlEscape(value.toString());
        this.element_attributes = element_attributes;
    }

    public SoapPayload(Object value, Map<String,String> element_attributes, Boolean should_escape_value)
    {
        this.value = should_escape_value ? HtmlUtils.htmlEscape(value.toString()) : value.toString();
        this.element_attributes = element_attributes;
    }

    public String getValue()
    {
        return this.value;
    }

    /**
     * @return array
     */
    public Map<String,String> getElementAttributes()
    {
        return this.element_attributes;
    }

    @Override
    public String toString() {
        return "SoapPayload{" +
                "value='" + value + '\'' +
                ", element_attributes=" + element_attributes +
                ", attributes=" + this.getAttributes() +
                '}';
    }
}