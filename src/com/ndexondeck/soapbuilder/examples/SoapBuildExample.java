package com.ndexondeck.soapbuilder.examples;

import com.ndexondeck.soapbuilder.SoapBuilder;
import com.ndexondeck.soapbuilder.SoapPayload;
import com.ndexondeck.soapbuilder.SoapPayloadCollection;

import java.util.*;

/**
 * Created by Nduka on 5/1/2019.
 */
public class SoapBuildExample {

    public static void main(String[] args) {

        if(args.length == 0) {
            System.out.println("Output type must be set [simpleSoap,complexSoap,complexSoap2,simpleXml,complexXml]");
            return;
        }

        SoapBuilder sb;

        System.out.println("\n\n\n\n\nStart XML Output>>>>\n");

        switch (args[0]){
             case "simpleSoap":
                 sb = new SoapBuilder();

                 sb.set("Header", new SoapPayload());

                 sb.set("Body",new SoapPayload());
                 sb.get("Body").set("Username",new SoapPayload("ndexondeck@gmail"));
                 sb.get("Body").set("Password",new SoapPayload("ndex4Jesus"));

                 System.out.println(sb.getXmlSafe());
                 break;

                case "complexSoap":

                    String mobileNo = "+2347062578879";
                    String msg = "This is the request you made";

                    sb = new SoapBuilder("soap", new HashMap<String,String>(){{
                        put("tem","http://template.org/");
                        put("sms","http://schemas.datacontract.org/2004/07/SMSAppws");
                        put("wsa","http://schemas.xmlsoap.org/ws/2004/08/addressing");
                    }}, "1.2");


                    sb.set("soap__Header", new SoapPayload());
                    sb.get("soap__Header").set("wsa__Action", new SoapPayload("http://tempuri.org/IService/SendMessage", new HashMap<String,String>(){{
                        put("xmlns:wsa","http://www.w3.org/2005/08/addressing");
                    }}));
                    sb.get("soap__Header").set("wsa__To", new SoapPayload("https://sms.sender.example/Service.svc", new HashMap<String,String>(){{
                        put("xmlns:wsa","http://www.w3.org/2005/08/addressing");
                    }}));

                    sb.set("soap__Body", new SoapPayload());
                    sb.get("soap__Body").set("tem__SendMessage", new SoapPayload());
                    sb.get("soap__Body").get("tem__SendMessage").set("sms__Message", new SoapPayload(msg));
                    sb.get("soap__Body").get("tem__SendMessage").set("sms__MobileNo", new SoapPayload(mobileNo));

                    System.out.println(sb.getXmlSafe());
                break;

            case "complexSoap2":

                sb = new SoapBuilder("soapenv", new HashMap<String,String>(){{
                    put("soap","http://dhdddjdj");
                    put("tem","http://template.org");
                }});

                sb.set("soap__Header",new SoapPayload());
                sb.set("soap__Body",new SoapPayload());
                sb.get("soap__Body").set("Username",new SoapPayload("ndexondeck@gmail"));
                sb.get("soap__Body").set("Password",new SoapPayload("YuHAKALATMmsa MOsa Asiv u set the mata"));
                sb.get("soap__Body").set("AntyLol",new SoapPayload("YuHAKALATMmsa MOsa Asiv u set the mata"));
                sb.get("soap__Body").set("tem__Password",new SoapPayload("AMp"));
                sb.get("soap__Body").set("tem__Payment",new SoapPayload());
                sb.get("soap__Body").get("tem__Payment").set("tem__Amount",new SoapPayload("10000"));
                sb.get("soap__Body").get("tem__Payment").set("tem__FromAccount",new SoapPayload("07478747748"));
                sb.get("soap__Body").get("tem__Payment").set("tem__ToAccount",new SoapPayload("9744773838"));
                sb.get("soap__Body").get("tem__Payment").set("tem__chargeAmount",new SoapPayloadCollection("KeyValuePairOfDecimalString",new HashMap<>(),"temi"));

//        //assume 2 loops
                sb.get("soap__Body").get("tem__Payment").getCollection("tem__chargeAmount").setItem(
                        new HashMap<String,Object>(){{
                            put("key Ami","084848848");
                            put("value_John","108");
                        }},
                        new HashMap<>()
                );
                sb.get("soap__Body").get("tem__Payment").getCollection("tem__chargeAmount").setItem(
                        new HashMap<String,Object>(){{
                            put("key Ami","084848848");
                            put("value_John","108");
                        }},
                        new HashMap<String,String>(){{
                            put("count","12");
                            put("abi","Nahim");
                        }},
                        false
                );

                System.out.println(sb.getXmlSafe());
                break;

            case "simpleXml":

                sb = new SoapBuilder().setAsXml().setVersion("1.0");

                sb.set("SearchCriteria", new SoapPayload());
                sb.get("SearchCriteria").set("UserName", new SoapPayload("John"));

                System.out.println(sb.getXmlSafe());
                break;

            case "complexXml":

                Set<User> department1Users = new HashSet<User>(){{
                    add(new SoapBuildExample().new User("Shola","Olaniyi","Lecturer"));
                    add(new SoapBuildExample().new User("Jude","Odiaka","Lecturer"));
                    add(new SoapBuildExample().new User("Anosike","Faith","Senior Lecturer"));
                }};

                Set<User> department2Users = new HashSet<User>(){{
                    add(new SoapBuildExample().new User("Obi","Shedrack","Laison Officer"));
                    add(new SoapBuildExample().new User("Ohadoma","Nduka","Admin"));
                }};

                Collection<Department> departments = new ArrayList<Department>(){{
                    add(new SoapBuildExample().new Department(1,"Information Technology", department1Users));
                    add(new SoapBuildExample().new Department(2,"Project Management Technology", department2Users));
                }};

                sb = new SoapBuilder();
                sb.setVersion("1.0").setAsResponse().setAsXml();
                sb.set("Response", new SoapPayload());
                sb.get("Response").set("ResponseCode", new SoapPayload("00"));
                sb.get("Response").set("UserList", new SoapPayloadCollection("Department"));

                Integer userCount = 0;
                if(!departments.isEmpty()){
                    for(Department department : departments){

                        SoapPayloadCollection collection = new SoapPayloadCollection("User",  new HashMap<String,String>(){{
                            put("Id",department.getId().toString());
                            put("Name",department.getName());
                        }});

                        Integer thisCount = 0;
                        for (User user : department.getUsers()){
                            collection.append(user.toMap(),new HashMap<>(),true);
                            userCount++;
                            thisCount++;
                        }

                        if(thisCount > 0){
                            try {
                                sb.get("Response").getCollection("UserList").set("Department", collection);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                sb.get("Response").getCollection("UserList").getElementAttributes().put("TotalAvailable", userCount.toString());

                System.out.println(sb.getXmlSafe());
                break;

        }

        System.out.println("\n>>>>End XML Output\n\n\n\n\n");

    }

    class Department {
        Integer id;
        String name;
        Collection<User> users;

        Department(Integer id, String name, Collection<User> users) {
            this.id = id;
            this.name = name;
            this.users = users;
        }

        Integer getId() {
            return id;
        }

        String getName() {
            return name;
        }

        Collection<User> getUsers() {
            return users;
        }
    }

    class User {
        String firstName;
        String lastName;
        String role;

        User(String firstName, String lastName, String role) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }

        Map<String,Object> toMap(){
            return new HashMap<String,Object>(){{
                put("firstName",firstName);
                put("lastName",lastName);
                put("role",role);
            }};
        }
    }
}
