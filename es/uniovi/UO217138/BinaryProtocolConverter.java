package es.uniovi.UO217138;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class BinaryProtocolConverter {
	private DataInputStream input;
	private DataOutputStream output;
	
	/**
	 * Constructor en modo entrada de datos
	 * @param in InputStream de entrada
	 */
	public BinaryProtocolConverter(InputStream in) {
		this.input = new DataInputStream(in);
	}
	
	/**
	 * Constructor en modo salida de datos
	 * @param out OutputStream de salida
	 */
	public BinaryProtocolConverter(OutputStream out) {
		this.output = new DataOutputStream(out);
	}
	
	/**
	 * Constructor en modo entrada/salida de datos
	 * @param in InputStream de entrada
	 * @param out OutputStream de salida
	 */
	public BinaryProtocolConverter(InputStream in, OutputStream out) {
		this.input = new DataInputStream(in);
		this.output = new DataOutputStream(out);
	}
	
	/**
	 * Función para leer un short desde el InputStream
	 * @return short leído
	 * @throws IOException
	 */
	short readShort() throws IOException {
		return input.readShort();
	}
	
	/**
	 * Función para leer un byte desde el InputStream
	 * @return byte leído
	 * @throws IOException
	 */
	byte readByte() throws IOException {
		return input.readByte();
	}
	
	/**
	 * Función para leer una cantidad de bytes desde el InputStream
	 * @param size La cantidad de bytes a leer
	 * @return Array de bytes leído
	 * @throws IOException
	 */
	byte[] readByteArray(int size) throws IOException {
		byte[] salida = new byte[size];
		
		for(int n = 0; n < size; n++) {
			salida[n]=this.readByte();
		}
		
		return salida;
	}
	
	/**
	 * Función que convierte una variable de tipo short a un array de dos bytes.
	 * @param num Short a convertir
	 * @return
	 */
	public byte[] short2bytes(short num) {
		return new byte[]{(byte)(num & 0x00FF),(byte)((num & 0xFF00)>>8)};
	}
	
	/**
	 * Función que recibe un mensaje binario de la red y lo convierte al formato Message
	 * @return Message El mensaje recibido
	 * @throws IOException
	 */
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
		
		// Leer tamaño de la carga
		sizeLoad = readShort();
		
		if (sizeLoad > 0) { 
			// Si hay carga, leer el número de parámetros
			numArgs = readShort();
			args = new String[numArgs];
			
			// Procesar los argumentos recibidos
			for(int n=0; n<numArgs; n++) {
				// Tamaño en bytes del argumento
				sizeArg = this.readShort();
				
				if (sizeArg > 0){
					// Leer argumento y convertirlo
					argBytes = this.readByteArray((int)sizeArg);
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
		
		// Almacenar argumentos
		salida.setArgs(args);
		
		return salida;
	}
	
	/**
	 * Función que convierte un objeto de tipo Message a binario y lo envía en el OutputStream
	 * @param mensaje Mensaje a imprimir
	 * @throws IOException
	 */
	public void sendMessage(Message mensaje) throws IOException {
		short sizeLoad = 0;		// Tamaño de la carga
		short numArgs = 0;		// Número de argumentos del mensaje
		byte[][] argsBytes;		// Array con los argumentos en formato binario
		String[] args;			// Array con los argumentos en formato texto
		
		// Obtención de los argumentos del mensaje
		args = mensaje.getArgs();
		numArgs = (short) args.length;
		
		// Inicializar el array de argumentos binarios
		argsBytes = new byte[args.length][];
		
		sizeLoad +=2; //Ya van dos bytes de carga en el número de argumentos
		
		// Codificar los argumentos
		for (int n = 0; n < args.length; n++) {
			argsBytes[n] = args[n].getBytes("UTF-8");
			sizeLoad += (2+argsBytes.length);
		}
		
		// Escritura en el Stream
		this.output.write(mensaje.getPacket());
		this.output.write(mensaje.getType());
		this.output.write(short2bytes(sizeLoad));
		
		if (sizeLoad > 0) {
			// Si hay argumentos, escribir el número
			this.output.write(short2bytes(numArgs));
			
			for (int n = 0; n < numArgs; n++) {
				// Escribir la longitud del argumento y el argumento en sí.
				this.output.write(short2bytes((short) argsBytes[n].length));
				this.output.write(argsBytes[n]);
			}
		}
	}
}
