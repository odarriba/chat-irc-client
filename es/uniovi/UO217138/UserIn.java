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
				if (this.hiloPadre.ejecucion) {
					System.err.println("Error al tratar mensaje de salida.");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sendNick(String newNick) {
		Message msgOut = new Message();
		
		if (newNick.length() == 0) {
			this.hiloPadre.serverLogPrintln("ERROR: Para cambiar el nick debes introducir uno nuevo primero.");
			return;
		}
		
		this.hiloPadre.serverLogPrintln("INFO: Enviado comando NICK para cambiar a "+newNick);
		
		msgOut.setType(Message.TYPE_NICK);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{newNick});
		
		insertMessage(msgOut);
	}
	
	public void sendJoin(String room) {
		Message msgOut = new Message();
		
		if (room.length() == 0) {
			this.hiloPadre.serverLogPrintln("ERROR: No se han recibido los datos necesarios para enviar el comando JOIN.");
			return;
		}
		
		this.hiloPadre.serverLogPrintln("INFO: Enviado comando JOIN a la sala "+room);
		
		msgOut.setType(Message.TYPE_JOIN);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{room});
		
		insertMessage(msgOut);
	}
	
	public void sendLeave(String room) {
		Message msgOut = new Message();
		
		if (room.length() == 0) {
			this.hiloPadre.serverLogPrintln("ERROR: No se han recibido los datos necesarios para enviar el comando LEAVE.");
			return;
		}
		
		msgOut.setType(Message.TYPE_LEAVE);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{room});
		
		this.hiloPadre.serverLogPrintln("INFO: Enviado comando LEAVE de la sala "+room);
		this.hiloPadre.mainWindow.print2Room(room,"INFO: Enviado comando LEAVE de la sala "+room);
		
		insertMessage(msgOut);
	}
	
	public void sendList() {
		Message msgOut = new Message();
		
		msgOut.setType(Message.TYPE_LIST);
		msgOut.setPacket(Message.PKT_CMD);
		
		this.hiloPadre.serverLogPrintln("INFO: Enviado comando LIST de info de salas");
		
		insertMessage(msgOut);
	}
	
	public void sendWho(String room) {
		Message msgOut = new Message();
		
		if (room.length() == 0) {
			this.hiloPadre.serverLogPrintln("ERROR: No se han recibido los datos necesarios para enviar el comando WHO.");
			return;
		}
		
		this.hiloPadre.serverLogPrintln("INFO: Enviado comando WHO de la sala "+room);
		this.hiloPadre.mainWindow.print2Room(room,"INFO: Enviado comando WHO de la sala "+room);
		
		msgOut.setType(Message.TYPE_WHO);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{room});
		
		insertMessage(msgOut);
	}
	
	public void sendQuit() {
		Message msgOut = new Message();
		
		msgOut.setType(Message.TYPE_QUIT);
		msgOut.setPacket(Message.PKT_CMD);
		
		this.hiloPadre.serverLogPrintln("INFO: Enviado comando QUIT.");
		
		insertMessage(msgOut);
	}
	
	public void sendMessage(String msg, String room) {
		Message msgOut = new Message();
		
		if (msg.length() == 0) {
			this.hiloPadre.mainWindow.print2Room(room,"ERROR: Para enviar un mensaje debes escribir algo primero.");
			return;
		}
		
		if (room.length() == 0) {
			this.hiloPadre.serverLogPrintln("ERROR: No se han recibido los datos necesarios para enviar el comando MSG.");
			return;
		}
		
		// Crear el mensaje
		msgOut.setType(Message.TYPE_MSG);
		msgOut.setPacket(Message.PKT_CMD);
		msgOut.setArgs(new String[]{room, msg});
				
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
