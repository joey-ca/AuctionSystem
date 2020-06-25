import java.io.*;
import java.lang.annotation.*;

@ClassPreamble(
      author = "Joey", 
      date = "07/05/2020", 
      lastModified = "02/05/2020", 
      reviewers = {"Ana", "Amy", "David"}
   )
public class Customer implements Serializable{
   private String name;
   private String address;
   private String visa;
   private String expire;
   
   public Customer(String n, String a, String v, String e){
      name = n;
      address = a;
      visa = v;
      expire = e;
   }
   
   public Customer(){this("","","","");}
   
   public String getName(){return name;}
   public String getAddress(){return address;}
   public String getVisa(){return visa;}
   public String getExpire(){return expire;}
   
   public void setName(String n){name = n;}
   public void setAddress(String a){address = a;}
   public void setVisa(String v){visa = v;}
   public void setExpire(String e){expire = e;}
   
   public String toString(){return name;}
   
   public boolean hasMissingInformation(){
   return ((name == null)||(name.length() == 0)||
           (address == null)||(address.length() == 0)||
           (visa == null)||(visa.length() == 0)||
           (expire == null)||(expire.length() == 0));
   }
}