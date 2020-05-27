package secure;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class SafeStreamPair {
	public LBLInputStream inputStream;
	public LBLOutputStream outputStream;
	public SafeStreamPair(Socket socket) throws Exception {
		KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey  rsaPublicKey =(RSAPublicKey)  keyPair.getPublic();    //¹«Ô¿
        RSAPrivateKey rsaPrivateKey=(RSAPrivateKey) keyPair.getPrivate();   //Ë½Ô¿
		X509EncodedKeySpec x509EncodedKeySpec=
				new X509EncodedKeySpec(rsaPublicKey.getEncoded());
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec=
				new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
		KeyFactory keyFactory=KeyFactory.getInstance("RSA");
		PublicKey publicKey=keyFactory.generatePublic(x509EncodedKeySpec);
		new ObjectOutputStream(socket.getOutputStream()).writeObject(publicKey);
        PrivateKey privateKey =keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		Cipher cipher_in=Cipher.getInstance("RSA");
        cipher_in.init(Cipher.DECRYPT_MODE,privateKey);
		Cipher cipher_out=Cipher.getInstance("RSA");
		PublicKey receivedKey=(PublicKey)
				new ObjectInputStream(socket.getInputStream()).readObject();
		cipher_out.init(Cipher.ENCRYPT_MODE,receivedKey);
        inputStream = new LBLInputStream (socket,cipher_in);
		outputStream= new LBLOutputStream(socket,cipher_out);
	}
	public LBLInputStream getInputStream() {
		return this.inputStream;
	}
	public LBLOutputStream getOutputStream() {
		return this.outputStream;
	}
}
