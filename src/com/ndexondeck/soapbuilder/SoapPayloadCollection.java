package com.ndexondeck.soapbuilder;


import com.ndexondeck.soapbuilder.interfaces.ISoapPayload;

import java.util.*;

public class SoapPayloadCollection implements ISoapPayload
{

    private List<ISoapPayload> attributes = new ArrayList<>();

    private Map<String,String> element_attributes = new LinkedHashMap<>();

    private String child;

    private Map<String,String> child_element_attributes = new LinkedHashMap<>();

    private String namespace = "";

    public SoapPayloadCollection(String child)
    {
        this.child = child;
    }

    public SoapPayloadCollection(String child, Map<String,String> element_attributes)
    {
        this.child = child;
        this.element_attributes = element_attributes;
    }

    public SoapPayloadCollection(String child, String namespace)
    {
        this.child = child;
        this.namespace = namespace;
    }

    public SoapPayloadCollection(String child, Map<String,String> element_attributes,String namespace)
    {
        this.child = child;
        this.element_attributes = element_attributes;
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void set(String name, ISoapPayload value) throws Exception
    {
        if(!this.namespace.isEmpty() && !name.equals(this.namespace+"__"+this.child)) throw new Exception("Cannot override soap payload collection child name from "+this.namespace+"__"+this.child+" to "+name);
        else if(!this.child.equals(name)) throw new Exception("Cannot override soap payload collection child name from "+this.child+" to "+name);

        this.pushValue(value);
    }

    private void pushValue(ISoapPayload value){
        this.attributes.add(value);
    }

    public void append(Object value){

        this.pushValue(new SoapPayload(value));
    }

    public void append(Object value, Map<String,String> element_attribute){

        this.pushValue(new SoapPayload(value,element_attribute));
    }

    public void append(List<Object> value){

        for (Object v : value) this.pushValue(new SoapPayload(v));
    }

    public void append(List<Object> value, Map<String,String> element_attribute){

        for (Object v : value) this.pushValue(new SoapPayload(v,element_attribute));
    }

    public void append(Map<String,Object> value){

        this.setItem(value);
    }

    public void append(Map<String,Object> value, Map<String,String> element_attribute){

        this.setItem(value, element_attribute);
    }

    public void append(Map<String,Object> value, Map<String,String> element_attribute, Boolean camelCase){

        this.setItem(value, element_attribute, camelCase);
    }


//    public void item(Object index, Object value, Map<String,String> element_attribute){
//
//        if(index == null) this.pushValue(new SoapPayload(value, element_attribute));
//
//        attributes = this.attributes;
//
//        if(index == null) index = attributes.size() - 1;
//        else if(!isset($attributes[$index])) throw new Exception("Undefined index $index in soap payload collection");
//
//        return this.attributes[index];
//    }

//    public void item(Integer index, Object value, Map<String,String> element_attribute){
//
//        if(index == null) this.pushValue(new SoapPayload(value,element_attribute));
//
//        Map<String,ISoapPayload> attributes = this.getAttributes();
//
//        if(is_null($index)) $index = count($attributes) - 1;
//        elseif(!isset($attributes[$index])) throw new Exception("Undefined index $index in soap payload collection");
//
//        return attributes[index];
//    }



    public void setItem(Map<String,Object> value){

        this.doSetItem(value, new HashMap<>(), true);
    }

    public void setItem(Map<String,Object> value, Map<String,String> element_attribute){

        this.doSetItem(value, element_attribute, true);
    }

    public void setItem(Map<String,Object> value, Map<String,String> element_attribute, Boolean camelCase){

        this.doSetItem(value, element_attribute, camelCase);
    }

    private void doSetItem(Map<String,Object> value, Map<String,String> element_attribute, Boolean camelCase){

        SoapPayload payload = new SoapPayload("",element_attribute);

        for (Map.Entry<String,Object> entry : value.entrySet()) {
            String key = entry.getKey();

            payload.set(key, new SoapPayload(entry.getValue()),camelCase);
        }

        this.pushValue(payload);
    }

    public String getChild()
    {
        return (!this.namespace.equalsIgnoreCase( "")) ? this.namespace + ":" + this.child : this.child;
    }

    public void setElementAttributes(Map<String,String> element_attributes)
    {
        this.element_attributes = element_attributes;
    }

    public Map<String, String> getElementAttributes()
    {
        return this.element_attributes;
    }

    public Map<String, String> getChildElementAttributes()
    {
        return this.child_element_attributes;
    }

    protected String getValue()
    {
        return "";
    }

    public void setAttributes(List<ISoapPayload> attributes) {
        this.attributes = attributes;
    }

    protected List<ISoapPayload> getAttributes()
    {
        return this.attributes;
    }

    @Override
    public String toString() {
        return "SoapPayloadCollection{" +
                "attributes=" + attributes +
                ", element_attributes=" + element_attributes +
                ", child='" + child + '\'' +
                ", child_element_attributes=" + child_element_attributes +
                ", namespace='" + namespace + '\'' +
                '}';
    }
}