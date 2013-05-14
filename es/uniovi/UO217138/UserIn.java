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

/*
 * Clase UserIn
 * 
 * Se encarga de procesar la entrada de teclado del usuario
 * y convertirla en un objeto de tipo Message que se enviar�
 * al hilo de salida de red mediante el buffer de comandos
 */
public class UserIn extends Thread {
	private ChatIRC hiloPadre;
	private BufferFifo bufferCommands;
	private BufferFifo bufferHilo;
	
	/*
	 * Constructor de la clase UserIn
	 */
	public UserIn(BufferFifo bufferCommands, ChatIRC principal) {
		this.hiloPadre = principal;
		this.bufferCommands = bufferCommands;
		this.bufferHilo = new BufferFifo(100); // 5 veces el buffer de salida de red
	}
	
	/*
	 * Funci�n de ejecuci�n del Thread
	 */
	public void run() {
		Message msg;
		
		while(this.hiloPadre.ejecucion) {
			try {
				msg = this.bufferHilo.get();
				// Meter el mensaje en el buffer de comandos
				this.bufferCommands.put(msg);
			} catch (InterruptedException e) {
				System.err.println("Error al tratar mensaje de salida.");
				e.printStackTrace();
			}
		}
	}
	
	public void sendNick(String newNick) {
		Message msgOut = new Message();
		
		if (newNick.length() == 0) {
			System.err.println("\n\nSintaxis de /NICK:");
			System.err.println("/NICK <nuevonick>\n");
		}
		
		msgOut.setType(Message.TYPE_NICK);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{newNick});
		
		insertMessage(msgOut);
	}
	
	public void sendJoin(String room) {
		Message msgOut = new Message();
		
		msgOut.setType(Message.TYPE_JOIN);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{room});
		
		insertMessage(msgOut);
	}
	
	public void sendLeave(String room) {
		Message msgOut = new Message();
		
		if (room.length() == 0) {
			System.err.println("\n\nSintaxis de /LEAVE:");
			System.err.println("/LEAVE <sala>\n");
		}
		
		msgOut.setType(Message.TYPE_LEAVE);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{room});
		
		insertMessage(msgOut);
	}
	
	public void sendList() {
		Message msgOut = new Message();
		
		msgOut.setType(Message.TYPE_LIST);
		msgOut.setPacket(Message.PKT_CMD);
		
		insertMessage(msgOut);
	}
	
	public void sendWho(String room) {
		Message msgOut = new Message();
		
		if (room.length() == 0) {
			System.err.println("\n\nSintaxis de /WHO:");
			System.err.println("/WHO <sala>\n");
		}
		
		msgOut.setType(Message.TYPE_WHO);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{room});
		
		insertMessage(msgOut);
	}
	
	public void sendQuit() {
		Message msgOut = new Message();
		
		msgOut.setType(Message.TYPE_QUIT);
		msgOut.setPacket(Message.PKT_CMD);
		
		insertMessage(msgOut);
		
		synchronized(this.hiloPadre.ejecucion) {
			this.hiloPadre.ejecucion = false;
		}
	}
	
	public void sendMessage(String textArray) {
		Message msgOut = new Message();
		String msg = "";
		
		
		// Crear el mensaje
		msgOut.setType(Message.TYPE_MSG);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{"SALA", msg});
				
		insertMessage(msgOut);
	}
	
	private void insertMessage(Message msg) {
		try {
			// Meter el mensaje en el buffer del hilo (gran capacidad)
			this.bufferHilo.put(msg);
		} catch(InterruptedException e) {
			System.err.println("Error al enviar el paquete al buffer interno del hilo: "+e.getMessage());
			e.printStackTrace();
		}
	}
}
