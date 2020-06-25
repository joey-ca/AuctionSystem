import java.io.*;

public class AuctionItem implements Serializable{
   private String name;
   private float bid;
   private Customer purchaser;
   private boolean sold;
   private String picture;
   
   public AuctionItem(String n, float startingBid, String fileName){
      name = n; 
      bid = startingBid;
      picture = fileName;
      purchaser = null;
      sold = false;
   }
   
   public AuctionItem(){ this("",0.0f,"");}
   
   public String getName(){return name;}
   public float getBid(){return bid;}
   public Customer getPurchaser(){return purchaser;}
   public String getPicture(){return picture;}
   public boolean isSold(){return sold;}
   
   public void setName(String n){name = n;}
   public void setBid(float b){bid = b;}
   public void setPurchaser(Customer c){purchaser = c;}
   public void setPicture(String p){picture = p;}
   public void setSold(){sold = true;}
   
   public String toString(){
      if(sold)
         return "[SOLD " + name + "]";
      else
         return name;
   }
}