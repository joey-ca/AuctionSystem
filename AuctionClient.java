import java.io.*;
import java.net.*;
import java.util.*;

/**
* The client of auction allows communication, such as bidding and responses, between the client and the server.
*
* @author Guannan Zhao
* @version %I%, %G%
* @since 1.0
*/
@SuppressWarnings("unchecked")
public class AuctionClient{
   
   // These variables hold the necessary socket information for communicating with the auction server
   private Socket socket;
   private ObjectInputStream in;
   private ObjectOutputStream out;
   
   private AuctionItem latestBidItem;
   
   // This is the Customer who is attached to this client as its model
   private Customer customer;
   
   private Object serverReply;
   
   public AuctionClient(Customer c){customer = c;}
   public AuctionClient(){this(new Customer());}
   
   // Return the AuctionItem being bid on
   public AuctionItem getLatestBidItem(){return latestBidItem;}
   
   // Return the server's latest reply for client application's status pane
   public Object getServerReply(){return serverReply;}
   
   // Return the Customer info pertaining to this client
   public Customer getCustomer(){return customer;}
   
   // Try connecting to the auction server
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
   
   // Try disconnecting from the auction server
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
   
   // Use this to display errors and also to record them for client application
   private void handleError(String s){
      serverReply = s;
   }
   
   // Send a request to be registered for the auction by the server
   public boolean registerForClient(){
      if(!connectToServer())
         return false;
      
      // Output the appreciate registration info
      try{
         out.writeObject("register request");
         out.writeObject(customer);
         out.flush();
      }
      catch(IOException e){
         handleError("CLIENT: Error sending registration information to server");
      }
      
      // This variable is used to see if the registration went through
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
   
   // Send a bid to the server
   public boolean sendBid(String name, float bid){
      if(!connectToServer())
         return false;
      
      // Output the appreciate bid info
      try{
         out.writeObject("bid request");
         out.writeObject(name);
         out.writeObject(new Float(bid));
         out.flush();
      }
      catch(IOException e){
         handleError("CLIENT: Error sending bid information to server");
      }
      
      // This variable is used to see if the bid went through
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
   
   // Send a request for a catalog of auction items to the server
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
   
   // Send a request for the latest bid info to the server
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
