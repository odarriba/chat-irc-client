package es.uniovi.UO217138;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class BinaryProtocolConverter {
	private DataInputStream input;
	private DataOutputStream output;
	
	// Constructor en modo entrada de datos
	public BinaryProtocolConverter(InputStream in) {
		this.input = new DataInputStream(in);
	}
	
	// Constructor en modo salida de datos
	public BinaryProtocolConverter(OutputStream out) {
		this.output = new DataOutputStream(out);
	}
	
	// Constructor en modo entrada/salida de datos
	public BinaryProtocolConverter(InputStream in, OutputStream out) {
		this.input = new DataInputStream(in);
		this.output = new DataOutputStream(out);
	}
	
	short readShort() throws IOException {
		return input.readShort();
	}
	
	byte readByte() throws IOException {
		return input.readByte();
	}
	
	byte[] readByteArray(int size) throws IOException {
		byte[] salida = new byte[size];
		
		for(int n = 0; n < size; n++) {
			salida[n]=this.readByte();
		}
		
		return salida;
	}
	
	public byte[] short2bytes(short num) {
		return new byte[]{(byte)(num & 0x00FF),(byte)((num & 0xFF00)>>8)};
	}
	
	public Message getMessage() throws IOException {
		Message salida = new Message();
		
		short sizeLoad;
		short numArgs;
		short sizeArg;
		byte[] argBytes;
		String[] args;
		
		// Leer el tipo de paquete y el tipo de mensaje (2 primeros bytes)
		salida.setPacket(this.readByte());
		salida.setType(this.readByte());
		
		sizeLoad = readShort();
		
		if (sizeLoad > 0) { 
			numArgs = readShort();
			args = new String[numArgs];
			
			for(int n=0; n<numArgs; n++){
				sizeArg = this.readByte();
				
				if (sizeArg > 0){
					argBytes = this.readByteArray(sizeArg);
					args[n] = new String(argBytes, "UTF-8");
				}
				else {
					args[n] = new String();
				}
			}
		}
		else {
			args = new String[0];
		}
		
		salida.setArgs(args);
		
		return salida;
	}
	
	public void sendMessage(Message mensaje) throws IOException {
		short sizeLoad = 0;
		short numArgs = 0;
		byte[][] argsBytes;
		String[] args;
		
		args = mensaje.getArgs();
		numArgs = (short) args.length;
		
		argsBytes = new byte[args.length][];
		
		sizeLoad +=2;
		
		for (int n = 0; n < args.length; n++) {
			argsBytes[n] = args[n].getBytes("UTF-8");
			sizeLoad += (2+argsBytes.length);
		}
		
		// Escritura en el Stream
		this.output.write(mensaje.getPacket());
		this.output.write(mensaje.getType());
		this.output.write(short2bytes(sizeLoad));
		
		if (numArgs > 0) {
			this.output.write(short2bytes(numArgs));
			
			for (int n = 0; n < numArgs; n++) {
				this.output.write(short2bytes((short) argsBytes[n].length));
			}
		}
	}
}
