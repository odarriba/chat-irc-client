/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase ChatIRC
 * 
 * Autores:
 *  - Lucas çlvarez
 *  - îscar de Arriba
 *  - Estefan’a Gonz‡lez
 */
package es.uniovi.UO217138;

import java.util.Date;
/*
 * Clase Message
 * Usada para intercambiar mensajes entre los diferentes hilos de
 * la aplicaci—n.
 */
public class Message {
	/* Tipos de mensajes que se pueden enviar/recibir */
	public static final int TYPE_MSG = 1;
	public static final int TYPE_JOIN = 2;
	public static final int TYPE_LEAVE = 3;
	public static final int TYPE_NICK = 4;
	public static final int TYPE_LIST = 5;
	public static final int TYPE_WHO = 6;
	public static final int TYPE_QUIT = 7;
	public static final int TYPE_ERROR = 8;
	
	private int type;
	private String nick;
	private String room;
	private String message;
	private String newNick;
	@SuppressWarnings("unused")
	private Date timeStamp;
	
	/*
	 * Constructor de la clase
	 * Genera la marca de tiempo de creaci—n del objeto
	 */
	public Message(){
		// Generar la marca de tiempo del mensaje
		this.timeStamp = new Date();
	}
	
	/*
	 * Getters y Setters de cada una de las variables
	 * de los mensajes intercambiados
	 */
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		boolean validType = false;
		
		// Comprobar que sea de uno de los tipos especificados
		switch(type){
			case TYPE_MSG:
				validType=true;
				break;
			case TYPE_JOIN:
				validType=true;
				break;
			case TYPE_LEAVE:
				validType=true;
				break;
			case TYPE_NICK:
				validType=true;
				break;
			case TYPE_LIST:
				validType=true;
				break;
			case TYPE_WHO:
				validType=true;
				break;
			case TYPE_QUIT:
				validType=true;
				break;
			case TYPE_ERROR:
				validType=true;
				break;
		}
		
		if (validType == true)
			this.type = type;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getRoom() {
		return room;
	}
	
	public void setRoom(String room) {
		this.room = room;
	}
	
	public String getNewNick() {
		return newNick;
	}
	
	public void setNewNick(String newNick) {
		this.newNick = newNick;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
