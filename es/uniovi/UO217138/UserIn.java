/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase UserIn
 * 
 * Autores:
 *  - Lucas Alvarez
 *  - Oscar de Arriba
 *  - Estefania Gonzalez
 */
package es.uniovi.UO217138;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * Clase UserIn
 * 
 * Se encarga de procesar la entrada de teclado del usuario
 * y convertirla en un objeto de tipo Message que se enviar‡
 * al hilo de salida de red mediante el buffer de comandos
 */
public class UserIn extends Thread {
	private ChatIRC hiloPadre;
	private BufferFifo bufferCommands;
	private BufferedReader input;
	
	/*
	 * Constructor de la clase UserIn
	 */
	public UserIn(BufferFifo bufferCommands, ChatIRC principal) {
		this.hiloPadre = principal;
		this.bufferCommands = bufferCommands;
		this.input =  new BufferedReader(new InputStreamReader(System.in));
	}
	
	/*
	 * Funci—n de ejecuci—n del Thread
	 */
	public void run() {
		String textReaded;
		
		while(this.hiloPadre.ejecucion) {
			textReaded = "";
			
			try {
				// Leer una l’nea del teclado
				textReaded = this.input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Comprobar que se ha le’do texto
			if (textReaded.length() > 0) {
				String[] textArray = textReaded.split(" ");
				
				if (textArray[0].toUpperCase().equals("/NICK")) {
					sendNick(textArray);
				}
				else if (textArray[0].toUpperCase().equals("/LEAVE")) {
					sendLeave(textArray);
				}
				else if (textArray[0].toUpperCase().equals("/LIST")) {
					sendList(textArray);
				}
				else if (textArray[0].toUpperCase().equals("/WHO")) {
					sendWho(textArray);
				}
				else if (textArray[0].toUpperCase().equals("/QUIT")) {
					sendQuit(textArray);
				}
				else if (textArray[0].toUpperCase().equals("/DEBUG")) {
					changeDebug();
				}
				else {
					sendMessage(textArray);
				}
			}
		}
	}
	
	private void sendNick(String[] textArray) {
		Message msgOut = new Message();
		
		if (textArray.length == 1) {
			System.err.println("\n\nSintaxis de /NICK:");
			System.err.println("/NICK <nuevonick>\n");
		}
		
		msgOut.setType(Message.TYPE_NICK);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{textArray[1]});
		
		insertMessage(msgOut);
	}
	
	public void sendJoin(String room) {
		Message msgOut = new Message();
		
		msgOut.setType(Message.TYPE_JOIN);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{room});
		
		insertMessage(msgOut);
	}
	
	private void sendLeave(String[] textArray) {
		Message msgOut = new Message();
		
		if (textArray.length == 1) {
			System.err.println("\n\nSintaxis de /LEAVE:");
			System.err.println("/LEAVE <sala>\n");
		}
		
		msgOut.setType(Message.TYPE_LEAVE);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{textArray[1]});
		
		insertMessage(msgOut);
	}
	
	private void sendList(String[] textArray) {
		Message msgOut = new Message();
		
		msgOut.setType(Message.TYPE_LIST);
		msgOut.setPacket(Message.PKT_CMD);
		
		insertMessage(msgOut);
	}
	
	private void sendWho(String[] textArray) {
		Message msgOut = new Message();
		
		if (textArray.length == 1) {
			System.err.println("\n\nSintaxis de /WHO:");
			System.err.println("/WHO <sala>\n");
		}
		
		msgOut.setType(Message.TYPE_WHO);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{textArray[1]});
		
		insertMessage(msgOut);
	}
	
	private void sendQuit(String[] textArray) {
		Message msgOut = new Message();
		
		msgOut.setType(Message.TYPE_QUIT);
		msgOut.setPacket(Message.PKT_CMD);
		
		insertMessage(msgOut);
		
		synchronized(this.hiloPadre.ejecucion) {
			this.hiloPadre.ejecucion = false;
		}
	}
	
	private void sendMessage(String[] textArray) {
		Message msgOut = new Message();
		String msg = "";
		
		for(int n=0; n< textArray.length; n++) {
			if (n > 0 || textArray[n].toUpperCase().equals("/MSG") == false) {
				msg = msg+textArray[n];
				
				if(n < (textArray.length-1)) {
					msg = msg + " "; // A–adir el espacio intermedio entre palabras
				}
			}
		}

		// Crear el mensaje
		msgOut.setType(Message.TYPE_MSG);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{this.hiloPadre.room, msg});
				
		insertMessage(msgOut);
	}
	
	private void changeDebug() {
		synchronized(this.hiloPadre.DEBUG) {
			this.hiloPadre.DEBUG = !this.hiloPadre.DEBUG;
		}
		
		if(this.hiloPadre.DEBUG) {
			System.out.println("INFO: Modo debug activado.");
		}
		else {
			System.out.println("INFO: Modo debug desactivado.");
		}
	}
	
	private void insertMessage(Message msg) {
		try {
			// Meter el mensaje en el buffer de comandos
			this.bufferCommands.put(msg);
		} catch(InterruptedException e) {
			System.err.println("Error al enviar el paquete al buffer de comandos: "+e.getMessage());
			e.printStackTrace();
		}
	}
}
