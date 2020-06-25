import java.io.*;
import java.net.*;
import java.util.*;

public class AuctionServer extends Thread{
   private ServerSocket serverSocket;
   private ObjectInputStream in;
   private ObjectOutputStream out;
   private Socket socket;
   private boolean online;
   public static int SERVER_PORT = 8080;
   private Auction auction;
   private AuctionServerApp serverApplication;
   
   public AuctionServer(Auction model){
      auction = model;
      online = false;
   }
   
   public AuctionServer(){this(new Auction());}
   
   public Auction getAuction(){return auction;}
   
   public void registerForUpdates(AuctionServerApp app){serverApplication = app;}
   
   public boolean goOnline(){
      try{
         serverSocket = new ServerSocket(SERVER_PORT);
         System.out.println("SERVER: Auction Server Online");
         online = true;
         start();
      }
      catch(IOException e){
         System.out.println("SERVER: Error Getting Server Online");
         System.exit(-1);
      } 
      return online;     
   }
   
   /**
   * causes this server to go offline. {@literal close()} is important. {@link AuctionItem#getBid() acquire the Bid method}
   *
   * @return the boolean indicating offline or not
   * @see #goOnline()
   * @see Auction
   * @since 1.0
   * @inheritDoc
   */
   public boolean goOffline(){
      try{
         if(online){ 
            serverSocket.close();
            System.out.println("SERVER: Auction Server Offline");
            online = false;
         } 
      }
      catch(IOException e){
         System.out.println("SERVER: Error Going Offline");
      } 
      return online; 
   }
   
   private boolean closeConnectFromClient(){
      try{
         if(socket != null){
            socket.close();
            in.close();
            out.close();
         }
         return true;  
      }
      catch(IOException e){
         System.out.println("SERVER: Error disconnecting from the client");
         return false;
      }   
   }
   
   @Override
   public void run(){
      while(online){
         try{
            socket = serverSocket.accept();
         }
         catch(IOException e){System.out.println("SERVER: Error connecting to a client");}
         
         try{
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            String receiveMsg = (String)in.readObject();
            System.out.println("SERVER Received: " + receiveMsg);           
                  
               if(receiveMsg != null){
                  char initialOfMsg = receiveMsg.charAt(0);
                  switch(initialOfMsg){
                     case 'r': registerClient(); break;
                     case 'b': handleIncomingBid(); break;
                     case 'c': handleCatalogRequest(); break;
                     case 'u': handleUpdateRequest(); break;
                     default: System.out.println("SERVER: Invalid Message " + receiveMsg);
                  }
                }
                else
                  System.out.println("SERVER: Invalid client message command");            
          }    
          catch(ClassNotFoundException e){System.out.println("SERVER: Error in client message data");}
          catch(IOException e){System.out.println("SERVER: Error receiving message from the client");}
          finally{
            System.out.println("SERVER: Closing client connection");
            //in.close();
            //out.close();
            closeConnectFromClient();
          }
      }
   }
   
   private void registerClient(){
      Customer c = null;
      try{
         c = (Customer)in.readObject();
      }
      catch(ClassNotFoundException e){System.out.println("SERVER: Error in client registration data");}
      catch(IOException e){System.out.println("SERVER: Error receiving message from the client");}
      
      try{
         if(c != null){
            if(c.hasMissingInformation())
               out.writeObject("SERVER: Information is missing");
            else{
               auction.register(c);
               out.writeObject("SERVER: Registration received");
            }
         }              
         out.flush();
      }
      catch(IOException e){System.out.println("SERVER: Error sending message to the client");}
   }
   
   private void handleIncomingBid(){
      String name = "";
      float amount = 0;
      try{
         name = (String)in.readObject();
         amount = ((Float)in.readObject()).floatValue();
      }
      catch(ClassNotFoundException e){System.out.println("SERVER: Error in client's bid data");}
      catch(IOException e){System.out.println("SERVER: Error receiving message from the client");}   
         
      try{   
         if(auction.customerWithName(name) == null)
            out.writeObject("SERVER: you MUST register first");         
         else if(auction.makeBid(name, amount))
            out.writeObject("SERVER: Bid received");
         else 
            out.writeObject("SERVER: Your Bid is invalid"); 
         out.flush();   
      }
      catch(IOException e){System.out.println("SERVER: Error receiving message from the client");}
      if(serverApplication != null) 
         serverApplication.update();  
   }
   
   private void handleCatalogRequest(){
      ArrayList<AuctionItem> catalog = auction.getInventory();
      try{
         out.writeObject(catalog);
         out.flush();
      }
      catch(IOException e){System.out.println("SERVER: Error sending catalog to the client");}   
   }
   
   private void handleUpdateRequest(){
      try{
         out.writeObject(auction.getBidItem());
         out.flush();
      }
      catch(IOException e){System.out.println("SERVER: Error sending update to the client");}
   }
     
}