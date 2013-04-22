/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase Message
 * 
 * Autores:
 *  - Lucas Alvarez
 *  - Oscar de Arriba
 *  - Estefania Gonzalez
 */
package es.uniovi.UO217138;
import java.util.Date;

/*
 * Clase Message
 * Usada para intercambiar mensajes entre los diferentes hilos de
 * la aplicaci�n.
 */
public class Message {
	/* Tipos de mensajes que se pueden enviar/recibir */
	public static final byte TYPE_MSG 	= 0x01;
	public static final byte TYPE_JOIN 	= 0x02;
	public static final byte TYPE_LEAVE = 0x03;
	public static final byte TYPE_NICK 	= 0x04;
	public static final byte TYPE_QUIT 	= 0x05;
	public static final byte TYPE_LIST 	= 0x10;
	public static final byte TYPE_WHO 	= 0x11;
	
	/* Tipos de paquetes posibles */
	public static final byte PKT_CMD 	= 0x00;
	public static final byte PKT_INF 	= 0x01;
	public static final byte PKT_OK 	= 0x02;
	public static final byte PKT_ERR 	= 0x03;
	
	
	private byte type;
	private byte packet;
	private String[] args;
	private Date timeStamp;
	
	/*
	 * Constructor de la clase
	 * Genera la marca de tiempo de creaci�n del objeto
	 */
	public Message(){
		// Generar la marca de tiempo del mensaje
		this.timeStamp = new Date();
	}
	
	/*
	 * Getters y Setters de cada una de las variables
	 * de los mensajes intercambiados
	 */
	public byte getType() {
		return this.type;
	}
	
	public void setType(byte type) {
		boolean validType = true;
		
		/* Comprobar que sea de uno de los tipos de mensaje disponibles */
		switch(type){
			case TYPE_MSG:
				break;
			case TYPE_JOIN:
				break;
			case TYPE_LEAVE:
				break;
			case TYPE_NICK:
				break;
			case TYPE_LIST:
				break;
			case TYPE_WHO:
				break;
			case TYPE_QUIT:
				break;
			default:
				validType = false;
				break;
		}
		
		if (validType) {
			this.type = type;
		}
	}
	
	public byte getPacket() {
		return this.packet;
	}
	
	public void setPacket(byte packet) {
		boolean validPacket = true;
		
		/* Comprobar que sea de uno de los tipos paquetes disponibles */
		switch(packet){
			case PKT_CMD:
				break;
			case PKT_INF:
				break;
			case PKT_OK:
				break;
			case PKT_ERR:
				break;
			default:
				validPacket = false;
				break;
		}
		
		if (validPacket == true) {
			this.packet = packet;
		}
	}
	
	public String[] getArgs() {
		return this.args;
	}
	
	public void setArgs(String[] args) {
		this.args = args;
	}
	
	public boolean esValido() {
		boolean valido = false;
		
		/* Comprobar que sea de uno de los tipos paquetes disponibles */
		switch(this.packet){
			case PKT_CMD:
				break;
			case PKT_INF:
				break;
			case PKT_OK:
				break;
			case PKT_ERR:
				break;
			default:
				valido = false;
				break;
		}
		
		if (valido == false) {
			return false;
		}
		
		/* Comprobar que sea de uno de los tipos de mensaje disponibles */
		switch(this.type){
			case TYPE_MSG:
				break;
			case TYPE_JOIN:
				break;
			case TYPE_LEAVE:
				break;
			case TYPE_NICK:
				break;
			case TYPE_LIST:
				break;
			case TYPE_WHO:
				break;
			case TYPE_QUIT:
				break;
			default:
				valido = false;
				break;
		}
		
		return valido;
	}
}
