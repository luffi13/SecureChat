/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kij_chat_client;

/*import java.net.Socket;*/
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import sun.misc.BASE64Decoder;

/**
 *
 * @author santen-suru
 */
public class Read implements Runnable {
        
        private String DecryptMsg;
        private String DecryptHashMsg;
        private String HashMsg;
        private EncryptionRSA encryption = new EncryptionRSA();
        
        private Scanner in;//MAKE SOCKET INSTANCE VARIABLE
        String input;
        boolean keepGoing = true;
        ArrayList<String> log;
	
	public Read(Scanner in, ArrayList<String> log)
	{
		this.in = in;
                this.log = log;
	}
        
        public String ShaHash(String password) throws NoSuchAlgorithmException{
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
             sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
     
            return sb.toString();
        }
    
        @Override
	public void run()//INHERIT THE RUN METHOD FROM THE Runnable INTERFACE
	{
		try
		{
			while (keepGoing)//WHILE THE PROGRAM IS RUNNING
			{						
				if(this.in.hasNext()) {
                                                                   //IF THE SERVER SENT US SOMETHING
                                        input = this.in.nextLine();
                                        
                                        //PRINT IT OUT
                                        if (input.split(" ")[0].toLowerCase().equals("success")) {
                                            if (input.split(" ")[1].toLowerCase().equals("logout")) {
                                                System.out.println(input);
                                                keepGoing = false;
                                                 
                                            } else if (input.split(" ")[1].toLowerCase().equals("login")) {
                                                System.out.println(input);
                                                log.clear();
                                                log.add("true");
                                                
                                            }
                                            else{
                                                System.out.println(input);
                                            }
                                        }
                                        else if (input.split(" ")[1].toLowerCase().equals(":")){
                                               
                                            String[] vals = input.split("#");
                                            String username = vals[0].split(": ")[0]+ " : ";
                                            String EncryptMsg = vals[0].split(": ")[1];
                                            String EncryptHashMsg = vals[1].split(" ")[0];
                                            String PubKey = vals[2];
                                            
//                                            //EncryptMsg
//                                            System.out.println(EncryptMsg);
//                                            //EncryptHashMsg
//                                            System.out.println(vals[1]);
//                                            //PublicKey
//                                            System.out.println(vals[2]);
//                                            //PublicKey Plaintext
                                            
                                            String repl = PubKey.replaceAll("~", "\n");
                                            BASE64Decoder decoder = new BASE64Decoder();
                                            byte[] tempPubKey = decoder.decodeBuffer(repl);
                                            
                                            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(tempPubKey);
                                            KeyFactory keyFact = KeyFactory.getInstance("RSA");
                                            PublicKey pubKey2 = keyFact.generatePublic(x509KeySpec);
//                                            System.out.println(pubKey2);
                                            
                                            DecryptMsg = encryption.decrypt(EncryptMsg,pubKey2); 
                                            
//                                            System.out.println(DecryptMsg);
                                            HashMsg = ShaHash(DecryptMsg);
//                                            System.out.println(HashMsg);
                                            
                                            DecryptHashMsg = encryption.decrypt(EncryptHashMsg, pubKey2);
//                                            System.out.println(DecryptHashMsg);
                                            
                                            if (HashMsg.equals(DecryptHashMsg)){
                                                System.out.println(username + DecryptMsg);
                                            }               
                                        } 
                                        else if (input.split(" ")[1].toLowerCase().equals("@")){
                                               
                                            String[] vals = input.split("#");
                                            String username = vals[0].split(":")[0]+ " ";
                                            String EncryptMsg = vals[0].split(": ")[1];
                                            String EncryptHashMsg = vals[1].split(" ")[0];
                                            String PubKey = vals[2];
                                 
                                            String repl = PubKey.replaceAll("~", "\n");
                                            BASE64Decoder decoder = new BASE64Decoder();
                                            byte[] tempPubKey = decoder.decodeBuffer(repl);
                                            
                                            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(tempPubKey);
                                            KeyFactory keyFact = KeyFactory.getInstance("RSA");
                                            PublicKey pubKey2 = keyFact.generatePublic(x509KeySpec);
//                                            System.out.println(pubKey2);
                                            
                                            DecryptMsg = encryption.decrypt(EncryptMsg,pubKey2); 
                                            
//                                            System.out.println(DecryptMsg);
                                            HashMsg = ShaHash(DecryptMsg);
//                                            System.out.println(HashMsg);
                                            
                                            DecryptHashMsg = encryption.decrypt(EncryptHashMsg, pubKey2);
//                                            System.out.println(DecryptHashMsg);
                                            
                                            if (HashMsg.equals(DecryptHashMsg)){
                                                System.out.println(username + DecryptMsg);
                                            }               
                                        }else{
                                            System.out.println(input);
                                        }
                                        
                                }
                                
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();//MOST LIKELY WONT BE AN ERROR, GOOD PRACTICE TO CATCH THOUGH
		} 
	}
}
