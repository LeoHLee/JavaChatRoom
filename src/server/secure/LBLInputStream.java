package secure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

//用于解密的处理流
public class LBLInputStream
{
	private final InputStream innerStream;
	private final Cipher cipher;
	public LBLInputStream(Socket socket, Cipher decryptor) throws IOException {
		innerStream=socket.getInputStream();
		cipher=decryptor;
	}
	protected byte[] readBlock() throws IOException {
		byte[] buffer = new byte[512];
		int n=0;
		while(n < 512) {
			int t = innerStream.read(buffer,n,512-n);
			if(t == -1)
				throw new IOException("Stream closed");
			n+=t;
		}
		try {
			return cipher.doFinal(buffer);
		} catch (BadPaddingException|IllegalBlockSizeException e) {
			throw new IOException("Decryption failed",e);
		}
	}
	public int readInt() throws IOException {
		int ans=0,multiplier=1;
		for(int i=0;i<4;i++) {
			int t=innerStream.read();
			if(t==-1)
				throw new IOException("Stream closed");
			ans+=multiplier*t;
			multiplier<<=8;
		}
		return ans;
	}
	public byte[] readArray() throws IOException {
		int size = readInt();
		int n = (size + 500) / 501;
		//BufferedInputStream bis=new BufferedInputStream(s);
		byte[] buffer = new byte[size];
		for(int i=0;i<n;i++) {
			byte[] temp = readBlock();
			for(int j=0 ; j<501 && i*501+j<size ; j++)
				buffer[i*501+j]=temp[j];
		}
		return buffer;
	}
	public Object readObject() throws IOException
	{
		byte[] buffer=readArray();
		Object obj = null;
		try {
			// bytearray to object
			ByteArrayInputStream bi = new ByteArrayInputStream(buffer);
			ObjectInputStream oi = new ObjectInputStream(bi);
			obj = oi.readObject();
			bi.close();
			oi.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return obj;
	}
}
