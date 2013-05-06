/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase UserOut
 * 
 * Autores:
 *  - Lucas Alvarez
 *  - Oscar de Arriba
 *  - Estefania Gonzalez
 */
package es.uniovi.UO217138;


import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

/*
 * Clase UserOut
 * 
 * Se encarga de procesar las respuestas almacenadas en
 * el buffer de respuestas y mostrar al usuario la
 * informaci�n pertinente.
 */
public class UserOut extends Thread {
	private ChatIRC hiloPadre;
	private BufferFifo bufferResponses;
	
	/*
	 * Constructor de la clase UserOut
	 */
	public UserOut (BufferFifo bufferResponses, ChatIRC principal) {
		this.hiloPadre = principal;
		this.bufferResponses = bufferResponses;
	}
	
	/*
	 * Funci�n de ejecuci�n del Thread
	 */
	public void run() {
		Message message;
		
		while(this.hiloPadre.ejecucion) {
			message = new Message();
			
			try {
				// Intentar obtener una respuesta del buffer
				message = this.bufferResponses.get();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			if (message.esValido()) {
				switch(message.getType()) {
					case Message.TYPE_MSG:
						processMsg(message);
						break;
			
					case Message.TYPE_JOIN:
						processJoin(message);
						break;
						
					case Message.TYPE_LEAVE:
						processLeave(message);
						break;
						
					case Message.TYPE_NICK:
						processNick(message);
						break;
						
					case Message.TYPE_QUIT:
						processQuit(message);
						break;
						
					case Message.TYPE_LIST:
						processList(message);
						break;
						
					case Message.TYPE_WHO:
						processWho(message);
						
						break;
						
					case Message.TYPE_HELLO:
						processHello(message);
						break;
						
					case Message.TYPE_MISC:
						processMisc(message);
						break;
				}
			}
		}
	}
	
	private void processMsg( Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo mensaje
		if (message.getPacket() == Message.PKT_INF) {
		//mensaje del servidor
			System.out.println(args[1]+"|"+args[0]+">"+args[2]);
		}else if (message.getPacket() == Message.PKT_ERR) {
		// error delservidor	
			System.out.println("ERROR: Error al enviar el mensaje - "+args[0]);
		}
	}
	
	private void processJoin (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo unirse a sala
		if (message.getPacket() == Message.PKT_INF) {
			hiloPadre.mainWindow.print2Room(args[1], "INFO: El usuario "+ args[0]+" se ha unido a la sala.");
			//mensaje del servidor
		}else if (message.getPacket() == Message.PKT_OK) {
			// Crear la sala en la UI
			hiloPadre.mainWindow.createRoom(args[1]);
			
			// Notidicar en la sala y en el log de servidor que se ha entrado en la sala
			hiloPadre.mainWindow.print2Room(args[1], "INFO: Te has unido a la sala "+args[1]);
			hiloPadre.serverLogPrintln("INFO: Te has unido a la sala "+args[1]);
			
			// Enviar mensaje de WHO para ver los usuarios de la sala
			hiloPadre.serverLogPrintln("INFO: Peticion de info sobre los usuarios de la sala '"+args[1]+"' enviada.");
			hiloPadre.userIn.sendWho(args[1]);
			
			// Enviar mensaje LIST para actualizar la lista de salas.
			hiloPadre.userIn.sendList();

		}else if (message.getPacket() == Message.PKT_ERR) {
			// Error del servidor	
			hiloPadre.serverLogPrintln("ERROR: Error al unirse a la sala - "+args[0]);
		}
	}
	
	private void processLeave (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo abandornar sala
		if (message.getPacket() == Message.PKT_INF) {
			hiloPadre.serverLogPrintln("INFO: El usuario "+ args[0]+" ha abandonado la sala "+args[1]);
			//mensaje del servidor
		}else if (message.getPacket() == Message.PKT_OK) {
			hiloPadre.serverLogPrintln("INFO: Has abandonado la sala "+args[1]);
			//constestacion del servidor
		}else if (message.getPacket() == Message.PKT_ERR) {
			// error del servidor	
			hiloPadre.serverLogPrintln("ERROR: Error al abandonar la sala - "+args[0]);
		}
	}
	
	private void processNick (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo cambio de nick
		if (message.getPacket() == Message.PKT_INF) {
			hiloPadre.serverLogPrintln("INFO: El usuario "+ args[0]+" ha cambiado su nick por "+args[1]);

			//mensaje del servidor
		}else if (message.getPacket() == Message.PKT_OK) {
			hiloPadre.serverLogPrintln("INFO: Tu nick ha sido correctamente cambiado de "+ args[0]+" a "+args[1]);
			
			// Actualizar la informacion del nick actual
			synchronized(this.hiloPadre.nick) {
				this.hiloPadre.nick = args[1];
			}
			//constestacion del servidor
		}else if (message.getPacket() == Message.PKT_ERR) {
			// error del servidor	
			hiloPadre.serverLogPrintln("ERROR: Error al cambiar el nick - "+args[0]);
		}
	}
	
	private void processQuit (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo fon de conexion
		if (message.getPacket() == Message.PKT_INF) {
			hiloPadre.serverLogPrintln("INFO: El usuario "+ args[0]+" se ha deconectado");
			//mensaje del servidor
		}else if (message.getPacket() == Message.PKT_OK) {
			hiloPadre.serverLogPrintln("INFO: Has finalizado la conexion");
			
			//constestacion del servidor
		}else if (message.getPacket() == Message.PKT_ERR) {
			hiloPadre.serverLogPrintln("ERROR: Error al desconectarse - "+args[0]);
			// error del servidor
		}
	}
	
	private void processList (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo list de salas
		if (message.getPacket() == Message.PKT_INF) {
			final String[] rooms = args[0].split(";");
			int num_salas = rooms.length;
			
			if (rooms[0].length() == 0) {
				num_salas--;
			}
			
			// Actualizar la lista cuando se pueda
			this.hiloPadre.mainWindow.updateRoomList(rooms);
			
			// Mostrar la informacion en la consola
			this.hiloPadre.serverLogPrintln("INFO: Se ha recibido info de un total de "+num_salas+" salas disponibles.");
			
		}else if (message.getPacket() == Message.PKT_ERR) {
			this.hiloPadre.serverLogPrintln("ERROR: Error al pedir las salas actuales - "+args[0]);
			// error del servidor	
		}
	}
	
	private void processWho (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo usuarios en sala
		if (message.getPacket() == Message.PKT_OK) {
			String[] users = args[1].split(";");
			
			// Almacenar la lista recibida
			synchronized (this.hiloPadre.mainObject.room2Users) {
				this.hiloPadre.mainObject.room2Users.put(args[0], users);
			}
			
			this.hiloPadre.mainWindow.setUsersRoom(args[0], users);
			//constestacion del servidor
		}else if (message.getPacket() == Message.PKT_ERR) {
			hiloPadre.serverLogPrintln("ERROR: Error al obtener los usuarios de la sala - "+args[0]);
			// error del servidor	
		}
	}
	
	private void processHello (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo mensaje de bienvenida del server
		if (message.getPacket() == Message.PKT_OK) {
			hiloPadre.serverLogPrintln("SERVER: "+args[0]);
			//constestacion del servidor
		}
	}
	
	private void processMisc(Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo demas errores
		if (message.getPacket() == Message.PKT_ERR) {
			hiloPadre.serverLogPrintln("ERROR: "+args[0]);
			// error delservidor	
		}
	}
}
