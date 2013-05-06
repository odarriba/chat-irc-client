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

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Clase UserOut
 * 
 * Se encarga de procesar las respuestas almacenadas en
 * el buffer de respuestas y mostrar al usuario la
 * informaci—n pertinente.
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
	 * Funci—n de ejecuci—n del Thread
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
	
	/**
	 * Procesar los mensajes tipo MSG, enviandolos a la sala que toque.
	 * @param message
	 */
	private void processMsg( Message message) {
		String[] args = message.getArgs();
		
		if (message.getPacket() == Message.PKT_INF) {
			// Mensaje del servidor MSG INF: mensaje que ha enviado otro usuario
			hiloPadre.mainWindow.print2Room(args[1],args[0]+">"+args[2]);
		}else if (message.getPacket() == Message.PKT_ERR) {
			// Error delservidor	
			hiloPadre.serverLogPrintln("ERROR: Error al enviar el mensaje - "+args[0]);
		}
	}
	
	/**
	 * Procesar los mensajes JOIN
	 * @param message
	 */
	private void processJoin (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo unirse a sala
		if (message.getPacket() == Message.PKT_INF) {
			// Si se recibe un JOIN INF, se ha unido otro usuario a una sala en la que se est‡
			// Notificar en la sala que se ha unido el usuario
			hiloPadre.mainWindow.print2Room(args[1], "INFO: El usuario "+ args[0]+" se ha unido a la sala.");
			
			if (hiloPadre.room2Users.get(args[1]) != null) {
				// Actualizar la lista de usuarios
				synchronized (hiloPadre.room2Users) {
					hiloPadre.room2Users.get(args[1]).add(args[0]);
				}
				// Actualizar la UI
				hiloPadre.mainWindow.setUsersRoom(args[1], hiloPadre.room2Users.get(args[1]));
			} else {
				hiloPadre.serverLogPrintln("ERROR: Se reciben notificaciones de salas en las que no se esta unido.");
			}
			
		}else if (message.getPacket() == Message.PKT_OK) {
			// Si se recibe un JOIN OK, se ha unido el cliente a una sala
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
	
	/**
	 * Procesamiento de mensajes tipo Leave
	 * @param message
	 */
	private void processLeave (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo abandornar sala
		if (message.getPacket() == Message.PKT_INF) {
			// Notificar en la sala que ha salido el usuario
			hiloPadre.mainWindow.print2Room(args[1], "INFO: El usuario "+ args[0]+" ha abandonado la sala.");
			
			// Actualizar la lista de usuarios de la sala
			synchronized (hiloPadre.room2Users) {
				hiloPadre.room2Users.get(args[1]).remove(args[0]);
				hiloPadre.mainWindow.setUsersRoom(args[1], hiloPadre.room2Users.get(args[1]));
			}
			
		}else if (message.getPacket() == Message.PKT_OK) {
			// Notificar en la consola
			hiloPadre.serverLogPrintln("INFO: Has abandonado la sala "+args[1]);
			
			// Eliminar la pesta–a de la ventana principal
			hiloPadre.mainWindow.removeRoom(args[1]);
			
			// Eliminar los datos de usuarios de dicha sala
			synchronized(this.hiloPadre.room2Users) {
				this.hiloPadre.room2Users.remove(args[1]);
			}
			
			// Recargar las lista de salas.
			hiloPadre.userIn.sendList();
			
		}else if (message.getPacket() == Message.PKT_ERR) {
			// error del servidor	
			hiloPadre.serverLogPrintln("ERROR: Error al abandonar la sala - "+args[0]);
		}
	}
	
	/**
	 * Procesar mensajes tipo NICK del servidor
	 * @param message
	 */
	private void processNick (Message message) {
		String[] args = message.getArgs();
		
		if (message.getPacket() == Message.PKT_INF) {
			// Mensaje tipo NICK INF: notifica que un usuario de una sala en la que estas ha cambiado su nick
			this.hiloPadre.serverLogPrintln("INFO: El usuario "+ args[0]+" ha cambiado su nick por "+args[1]);
			
			synchronized(this.hiloPadre.room2Users) {
				Object[] rooms = this.hiloPadre.room2Users.keySet().toArray();
				for (int i = 0; i < rooms.length; i++) {
					String key = (String)rooms[i];
					
					// Comprobar si el usuario estaba en esta sala
					if (this.hiloPadre.room2Users.get(key).indexOf(args[0]) != -1) {
						this.hiloPadre.room2Users.get(key).remove(args[0]);
						this.hiloPadre.room2Users.get(key).add(args[1]);
						// Actualizar la UI
						this.hiloPadre.mainWindow.setUsersRoom(key, this.hiloPadre.room2Users.get(key));
						this.hiloPadre.mainWindow.print2Room(key, "INFO: El usuario "+ args[0]+" ha cambiado su nick por "+args[1]);
					}
				}
			}
		}else if (message.getPacket() == Message.PKT_OK) {
			// Mensaje tipo NICK OK: Has cambiado tu nick correctamente
			this.hiloPadre.serverLogPrintln("INFO: Tu nick ha sido correctamente cambiado de "+ args[0]+" a "+args[1]);
			
			// Actualizar la informacion del nick actual
			synchronized(this.hiloPadre.nick) {
				this.hiloPadre.nick = args[1];
			}
			
			// Hay que actualizar todas las salas, pues se esta en todas.
			synchronized(this.hiloPadre.room2Users) {
				Object[] rooms = this.hiloPadre.room2Users.keySet().toArray();
				for (int i = 0; i < rooms.length; i++) {
					String key = (String) rooms[i];
					// Comprobar si el usuario estaba en esta sala
					this.hiloPadre.room2Users.get(key).remove(args[0]);
					this.hiloPadre.room2Users.get(key).add(args[1]);
					// Actualizar la UI
					this.hiloPadre.mainWindow.setUsersRoom(key, this.hiloPadre.room2Users.get(key));
					this.hiloPadre.mainWindow.print2Room(key, "INFO: Tu nick ha sido correctamente cambiado de "+ args[0]+" a "+args[1]);
				}
			}
		}else if (message.getPacket() == Message.PKT_ERR) {
			// error del servidor	
			hiloPadre.serverLogPrintln("ERROR: Error al cambiar el nick - "+args[0]);
		}
	}
	
	/**
	 * Procesar mensajes de tipo QUIT
	 * @param message
	 */
	private void processQuit (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo fon de conexion
		if (message.getPacket() == Message.PKT_INF) {
			hiloPadre.serverLogPrintln("INFO: El usuario "+ args[0]+" se ha deconectado");
			
			// Hay que actualizar todas las salas, pues se esta en todas.
			synchronized(this.hiloPadre.room2Users) {
				Object[] rooms = this.hiloPadre.room2Users.keySet().toArray();
				for (int i = 0; i < rooms.length; i++) {
					String key = (String) rooms[i];
					
					if (this.hiloPadre.room2Users.get(key).indexOf(args[0]) != -1) {
						// Comprobar si el usuario estaba en esta sala
						this.hiloPadre.room2Users.get(key).remove(args[0]);
						// Actualizar la UI
						this.hiloPadre.mainWindow.setUsersRoom(key, this.hiloPadre.room2Users.get(key));
						this.hiloPadre.mainWindow.print2Room(key, "INFO: El usuario "+ args[0]+" se ha deconectado");
					}
				}
			}
			//mensaje del servidor
		}else if (message.getPacket() == Message.PKT_OK) {
			// Desconexion correcta. Cierre de la ventana.
			hiloPadre.serverLogPrintln("INFO: Has finalizado la conexion");
			hiloPadre.mainWindow.closeWindow();
		}else if (message.getPacket() == Message.PKT_ERR) {
			hiloPadre.serverLogPrintln("ERROR: Error al desconectarse - "+args[0]);
			// error del servidor
		}
	}
	
	/**
	 * Procesar mensajes de tipo LIST
	 * @param message
	 */
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
	
	/**
	 * Procesar mensajes de tipo WHO
	 * @param message
	 */
	private void processWho (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo usuarios en sala
		if (message.getPacket() == Message.PKT_OK) {
			String[] users = args[1].split(";");
			ArrayList<String> usersList = new ArrayList<String>();
			usersList.addAll(Arrays.asList(users));
			
			// Almacenar la lista recibida
			synchronized (this.hiloPadre.mainObject.room2Users) {
				this.hiloPadre.mainObject.room2Users.put(args[0], usersList);
			}
			
			this.hiloPadre.mainWindow.setUsersRoom(args[0], usersList);
			//constestacion del servidor
		}else if (message.getPacket() == Message.PKT_ERR) {
			hiloPadre.serverLogPrintln("ERROR: Error al obtener los usuarios de la sala - "+args[0]);
			// error del servidor	
		}
	}
	
	/**
	 * Procesar mensajes de tipo HELLO
	 * @param message
	 */
	private void processHello (Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo mensaje de bienvenida del server
		if (message.getPacket() == Message.PKT_OK) {
			hiloPadre.serverLogPrintln("SERVER: "+args[0]);
			//constestacion del servidor
		}
	}
	
	/**
	 * Procesar mensajes de tipo MISC
	 * @param message
	 */
	private void processMisc(Message message) {
		String[] args = message.getArgs();
		
		//paquete del tipo demas errores
		if (message.getPacket() == Message.PKT_ERR) {
			hiloPadre.serverLogPrintln("ERROR: "+args[0]);
			// error delservidor	
		}
	}
}
