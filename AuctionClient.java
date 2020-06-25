import java.io.*;
import java.net.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class AuctionClient{
   private Socket socket;
   private ObjectInputStream in;
   private ObjectOutputStream out;
   private AuctionItem latestBidItem;
   //private AuctionClientApp clientApplication;
   private Customer customer;
   private Object serverReply;
   
   public AuctionClient(Customer c){customer = c;}
   public AuctionClient(){this(new Customer());}
   
   public AuctionItem getLatestBidItem(){return latestBidItem;}
   public Object getServerReply(){return serverReply;}
   public Customer getCustomer(){return customer;}
   
   private boolean connectToServer(){
      InetAddress address;
      try{
         address = InetAddress.getLocalHost();
      }
      catch(UnknownHostException e){return false;}
      
      try{
         socket = new Socket(address, AuctionServer.SERVER_PORT);
      }
      catch(IOException e){
         handleError("CLIENT: Error Connecting to Server");
         return false;
      }
      
      try{
         in = new ObjectInputStream(socket.getInputStream());
         out = new ObjectOutputStream(socket.getOutputStream());
         return true;
      }
      catch(IOException e){return false;}
   }
   
   private boolean disconnectFromServer(){
      try{
         if(socket != null){
            in.close();
            out.close();
            socket.close();
         }
         return true;
      }
      catch(IOException e){
         handleError("CLIENT: Error Disconnecting from Server");
         return false;
      }
   }
   
   private void handleError(String s){
      serverReply = s;
      //clientApplication.updateStatusField(s);
   }
   
   public boolean registerForClient(){
      if(!connectToServer())
         return false;
      try{
         out.writeObject("register request");
         out.writeObject(customer);
         out.flush();
      }
      catch(IOException e){
         handleError("CLIENT: Error sending registration information to server");
      }
      
      boolean result = false;
      try{
         serverReply = in.readObject();
         if(serverReply != null)
            result = ((String)serverReply).equals("SERVER: Registration received");           
      }
      catch(ClassNotFoundException e){
         handleError("Error in Server reply data");
      }      
      catch(IOException e){
         handleError("CLIENT: Error receiving registration response");
      }
      finally{
         disconnectFromServer();
         return result;
      }
   }
   
   public boolean sendBid(String name, float bid){
      if(!connectToServer())
         return false;
      try{
         out.writeObject("bid request");
         out.writeObject(name);
         out.writeObject(new Float(bid));
         out.flush();
      }
      catch(IOException e){
         handleError("CLIENT: Error sending bid information to server");
      }
      
      boolean result = false;
      try{
         serverReply = in.readObject();
         if(serverReply != null)
            result = ((String)serverReply).equals("SERVER: Bid received");           
      }
      catch(ClassNotFoundException e){
         handleError("Error in Server reply data");
      }      
      catch(IOException e){
         handleError("CLIENT: Error receiving bid response");
      }
      finally{
         disconnectFromServer();
         return result;
      }
   }
   
   public ArrayList<AuctionItem> sendForCatalog(){
      if(!connectToServer()) 
         return new ArrayList<AuctionItem>();
      try{
         out.writeObject("catalog request");
         out.flush();
      }
      catch(IOException e){
         handleError("CLIENT: Error sending catalog request to server");
      }
      
      try{
         serverReply = in.readObject();
         if(serverReply == null)
            return new ArrayList<AuctionItem>();
      }
      catch(ClassNotFoundException e){
         handleError("Error in Server reply data");
      }      
      catch(IOException e){
         handleError("CLIENT: Error receiving catalog response");
      }
      finally{
         disconnectFromServer();
         return (ArrayList<AuctionItem>)serverReply;
      }
   }
   
   public AuctionItem sendForUpdate(){
      if(!connectToServer()) return null;
      try{
         out.writeObject("update request");
         out.flush();
      }
      catch(IOException e){
         handleError("CLIENT: Error sending update request to server");
      }
      
      try{
         serverReply = in.readObject();
         if(serverReply != null){
            latestBidItem = (AuctionItem)serverReply;           
         }
      }
      catch(ClassNotFoundException e){
         handleError("Error in Server reply data");
      }      
      catch(IOException e){
         handleError("CLIENT: Error receiving update response");
      }
      finally{
         disconnectFromServer();
         return latestBidItem;
      }
   }

}