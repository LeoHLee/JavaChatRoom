package secure;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;


public class LBLOutputStream
{

	private final OutputStream innerStream;

	private final Cipher cipher;
	public LBLOutputStream(Socket socket,Cipher encryptor) throws IOException {
		innerStream=socket.getOutputStream();
		cipher=encryptor;
	}
	synchronized public void writeInt(int x) throws IOException {
		for(int i=0;i<4;i++) {
			innerStream.write((byte) (x & 255));
			x >>>= 8;
		}
	}
	synchronized protected void writeBlock(byte[] b) throws IOException {
		try {
			innerStream.write(cipher.doFinal(b));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new IOException("Encryption Error",e);
		}
	}
	synchronized public void writeArray(byte[] a) throws IOException {
		int n=(a.length+500)/501;
		writeInt(a.length);
		for(int i=0;i<n;i++)
			writeBlock(Arrays.copyOfRange(a,i*501,(i+1)*501));
	}
	synchronized public void writeObject(Object obj) throws IOException
	{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream oo = new ObjectOutputStream(bo);
		oo.writeObject(obj);
		writeArray(bo.toByteArray());
	}
}
