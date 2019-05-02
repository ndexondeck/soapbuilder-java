package com.ndexondeck.soapbuilder;

//import com.google.common.collect.ImmutableSet;
//import ng.upperlink.constant.ExceptionKeyConstant;
//import ng.upperlink.interfaces.ISoapPayload;
//import ng.upperlink.util.CaseConverter;
//import ng.upperlink.util.CollectionUtils;

import com.ndexondeck.soapbuilder.exceptions.SoapBuilderException;
import com.ndexondeck.soapbuilder.interfaces.ISoapPayload;
import com.ndexondeck.soapbuilder.utils.CaseConverter;
import com.ndexondeck.soapbuilder.utils.CollectionUtils;

import java.util.*;

public class SoapBuilder
{
    private String key = "soap";

    private Map<String,String> namespaces = new LinkedHashMap<>();

    private Map<String,ISoapPayload> attributes = new LinkedHashMap<>();

    private Set<String> names = new HashSet<>();

    private Set<String> properties = new HashSet<>();

    private String version = "1.1";

    private Boolean xml_mode = false;

    private Boolean response = false;

    private String encoding = "utf-8";//Windows-1252

    public SoapBuilder() {}

    public SoapBuilder(String key) {
        this.key = key;
    }

    public SoapBuilder(String key, Map<String,String> namespaces)
    {
        this.key = key;
        this.namespaces = namespaces;
    }

    public SoapBuilder(String key, Map<String,String> namespaces, String version)
    {
        this.key = key;
        this.namespaces = namespaces;
        this.version = version;
    }

    /**
     * Copy Constructor
     * @param original
     */
    public SoapBuilder(SoapBuilder original){

        this.attributes = original.attributes;
        this.encoding = original.encoding;
        this.names = original.names;
        this.namespaces = original.namespaces;
        this.version = original.version;
        this.properties = original.properties;
        this.xml_mode = original.xml_mode;
        this.response = original.response;
        this.encoding = original.encoding;
    }

    public SoapBuilder setAsXml(){
        this.xml_mode = true;
        return this;
    }

    public SoapBuilder addAttribute(String namespace, String href){
        this.namespaces.put(namespace,href);
        return this;
    }

    public SoapPayload get(String name)
    {
        String newName = name.replaceAll("__",":");

        if(this.attributes.containsKey(newName)){
            return (SoapPayload) this.attributes.get(newName);
        }
        return new SoapPayload();
    }

    public SoapPayloadCollection getCollection(String name)
    {
        String newName = name.replaceAll("__",":");

        if(this.attributes.containsKey(newName)){
            return (SoapPayloadCollection) this.attributes.get(newName);
        }
        return new SoapPayloadCollection("Collection");
    }

    public void set(String name, ISoapPayload value)
    {

        String newName = name.replaceAll("__",":");

        String[] v = newName.split(":");

        Integer i = 0;
        if(v.length > 1){
            i = 1;
            this.names.add(v[0]);
        }
        this.properties.add(v[i]);

        this.attributes.put(newName,value);
    }

    public void set(String name, ISoapPayload value, Boolean camelCase)
    {

        if(camelCase){
            //reconstruct name
            String[] names = name.split("__");
            name = (names.length > 1) ? names[0] +"__"+ CaseConverter.convertToUpperCamelCase(names[1]) : CaseConverter.convertToUpperCamelCase(name);
        }

        this.set(name,value);
    }

    private void validateNamespace(Set<String> names) throws Exception{
        Set<String> namespaces = new HashSet<>();
        namespaces.add(this.key);

        Collection<String> nameDiff = CollectionUtils.difference(CollectionUtils.union(namespaces,this.namespaces.keySet()),names);

//        if(nameDiff.size() > 0) throw new Exception("Unknown XML NameSpace : "+ String.join(",",nameDiff));
    }

    private void validateStructure() throws Exception{

        if(this.properties.contains("Header")){
            if(!this.properties.contains("Body")) throw new Exception("Body was not found in xml");
        }else{
            throw new Exception("Header was not found in xml");
        }
    }

    public String getXml() throws Exception{
        String moreAttr = "";
        String schema = "";

        if(!this.xml_mode){
            this.validateStructure();
            this.validateNamespace(this.names);

            for(Map.Entry<String,String> entry : this.namespaces.entrySet()){
                moreAttr = moreAttr + " xmlns:"+entry.getKey()+"=\""+entry.getValue()+"\"";
            }

            switch (this.version){
                case "1.2":
                    schema = "http://www.w3.org/2003/05/soap-envelope";
                    break;
                default:
                    schema = "http://schemas.xmlsoap.org/soap/envelope/";
                    break;
            }
        }

        String xmlContent = this.readPayload(this.attributes);

        String prepend = "";
        if(this.isResponse()){
            prepend = "<?xml version=\""+this.version+"\" encoding=\""+this.encoding+"\" ?>";
        }

        if(!this.xml_mode){
            return prepend +
"<"+this.key+":Envelope xmlns:"+this.key+"=\""+schema+"\""+moreAttr+">"+
   xmlContent+
"</"+this.key+":Envelope>";
        }

        return prepend+
xmlContent;
    }

    public String getXmlSafe() throws SoapBuilderException{

        try {
            return getXml();
        } catch (Exception e) {
            throw new SoapBuilderException("SOAP_COMPILER_ERROR", e);
        }
    }

    public String getXmlAsCDATA() throws Exception{
        return "<![CDATA["+this.getXml()+"]]>";
    }

    private String recursiveLookup(SoapPayload payload) throws Exception{

        if(!this.xml_mode) this.validateNamespace(payload.getNames());

        if(payload.getAttributes().size() > 0) return this.readPayload(payload.getAttributes());

        return payload.getValue();
    }

    private String recursiveLookup(SoapPayloadCollection payload,String tag) throws Exception{

        if(!this.xml_mode) this.validateNamespace(new HashSet<String>(){{add(payload.getNamespace());}});

        if(payload.getAttributes().size() > 0) return this.readPayload(payload.getAttributes(),tag);

        return payload.getValue();
    }

    private String readPayload(Map<String,ISoapPayload> attributes) throws Exception
    {
        StringBuilder xmlContent = new StringBuilder();

        for(Map.Entry<String,ISoapPayload> entry : attributes.entrySet()){

            xmlContent = this.xmlContentBuilder(xmlContent, entry.getValue(),entry.getKey());
        }

        return xmlContent.toString();
    }

    private String readPayload(List<ISoapPayload> attributes, String tag) throws Exception
    {
        StringBuilder xmlContent = new StringBuilder();

        for(ISoapPayload iPayload : attributes){

            this.xmlContentBuilder(xmlContent,iPayload,tag);
        }
        return xmlContent.toString();
    }

    private StringBuilder xmlContentBuilder(StringBuilder xmlContent, ISoapPayload payload, String tag) throws Exception{
        String element_attribute;

        String content;

        element_attribute = this.parseElementAttributes(payload.getElementAttributes());

        if(payload instanceof  SoapPayload){
            content = this.recursiveLookup((SoapPayload) payload);
        }
        else{
            content = this.recursiveLookup((SoapPayloadCollection) payload,((SoapPayloadCollection) payload).getChild());
        }

        if(content.isEmpty() && !content.equalsIgnoreCase("0")) xmlContent.append("<").append(tag).append(element_attribute).append("/>");
        else{
            xmlContent.append("<").append(tag).append(element_attribute).append(">");
            xmlContent.append(content);
            xmlContent.append("</").append(tag).append(">");
        }

        return xmlContent;
    }

    /**
     * @return SoapBuilder
     */
    public SoapBuilder setAsResponse()
    {
        this.response = true;
        return this;
    }

    public Set<String> getNames() {
        return names;
    }

    protected Map<String,ISoapPayload> getAttributes()
    {
        return this.attributes;
    }

    private String parseElementAttributes(Map<String,String> elementAttributes)
    {
        String element_attribute = "";
        Set<String> element_attributes = new HashSet<>();
        for(Map.Entry<String,String> entry : elementAttributes.entrySet()){
            element_attributes.add(entry.getKey()+"=\""+entry.getValue()+"\"");
        }

        if(!element_attributes.isEmpty()) element_attribute = " "+ String.join(" ", element_attributes);

        return element_attribute;
    }

    public Boolean isResponse()
    {
        return this.response;
    }

    public SoapBuilder setVersion(String version)
    {
        this.version = version;
        return this;
    }

    public SoapBuilder setEncoding(String encoding)
    {
        this.encoding = encoding;
        return this;
    }
}