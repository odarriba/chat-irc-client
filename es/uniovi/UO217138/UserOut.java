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
		
		while(true) {
			message = new Message();
			
			try {
				// Intentar obtener una respuesta del buffer
				message = this.bufferResponses.get();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			if (message.esValido()) {
				String[] args = message.getArgs();
			
				switch(message.getType()) {
					case Message.TYPE_MSG:
						//paquete del tipo mensaje
						if (message.getPacket() == Message.PKT_INF) {
						//mensaje del servidor
							System.out.println(args[1]+"|"+args[0]+">"+args[2]);
						}else if (message.getPacket() == Message.PKT_ERR) {
						// error delservidor	
							System.out.println("ERROR - Error al enviar el mensaje: "+args[0]);
						}
						
						break;
			
					case Message.TYPE_JOIN:
						//paquete del tipo unirse a sala
						if (message.getPacket() == Message.PKT_INF) {
							System.out.println("El usuario "+ args[0]+" se ha unido a la sala "+args[1]);
							//mensaje del servidor
						}else if (message.getPacket() == Message.PKT_OK) {
							//constestacion del servidor
							System.out.println("Te has unido a la sala "+args[1]);
							
							// Actualizar la informacion de la sala actual
							synchronized(this.hiloPadre.room) {
								this.hiloPadre.room = args[1];
							}
						}else if (message.getPacket() == Message.PKT_ERR) {
							// error del servidor	
							System.out.println("ERROR - Error al unirse a la sala: "+args[0]);
						}
						
						break;
						
					case Message.TYPE_LEAVE:
						//paquete del tipo abandornar sala
						if (message.getPacket() == Message.PKT_INF) {
							System.out.println("El usuario "+ args[0]+" ha abandonado la sala "+args[1]);
							//mensaje del servidor
						}else if (message.getPacket() == Message.PKT_OK) {
							System.out.println("Has abandonado la sala  "+args[1]);
							//constestacion del servidor
						}else if (message.getPacket() == Message.PKT_ERR) {
							// error delservidor	
							System.out.println("ERROR - Error al abandonar la sala: "+args[0]);
						}
						
						break;
						
					case Message.TYPE_NICK:
						//paquete del tipo cambio de nick
						if (message.getPacket() == Message.PKT_INF) {
							System.out.println("El usuario "+ args[0]+" ha cambiado su nick por "+args[1]);
			
							//mensaje del servidor
						}else if (message.getPacket() == Message.PKT_OK) {
							System.out.println("Se a realizado satisfactoriamente el cambio de nick de  "+ args[0]+" a "+args[1]);
							
							// Actualizar la informacion del nick actual
							synchronized(this.hiloPadre.nick) {
								this.hiloPadre.nick = args[1];
							}
							//constestacion del servidor
						}else if (message.getPacket() == Message.PKT_ERR) {
							// error delservidor	
							System.out.println("ERROR - Error al cambiar de nick: "+args[0]);
						}
						
						break;
						
					case Message.TYPE_QUIT:
						//paquete del tipo fon de conexion
						if (message.getPacket() == Message.PKT_INF) {
							System.out.println("El usuario "+ args[0]+" se ha deconectado");
							//mensaje del servidor
						}else if (message.getPacket() == Message.PKT_OK) {
							System.out.println("Has finalizado la conexion");
							//constestacion del servidor
						}else if (message.getPacket() == Message.PKT_ERR) {
							System.out.println("ERROR- Error al desconectarse: "+args[0]);
							// error delservidor
						}
						
						break;
						
					case Message.TYPE_LIST:
						//paquete del tipo list de salas
						if (message.getPacket() == Message.PKT_INF) {
							StringTokenizer st= new StringTokenizer(args[0],";");
							System.out.println("Las siguientes salas estan disponibles: ");
								
							while(st.hasMoreTokens()){
								System.out.println(st.nextToken());
							}
							//constestacion del servidor
						}else if (message.getPacket() == Message.PKT_ERR) {
							System.out.println("ERROR - Error al pedir las salas actuales: "+args[0]);
							// error delservidor	
						}
						
						break;
						
					case Message.TYPE_WHO:
						//paquete del tipo usuarios en sala
						if (message.getPacket() == Message.PKT_OK) {
							StringTokenizer st= new StringTokenizer(args[1],";");
							System.out.println("En la sala "+args[0]+ "se encuentran los siguientes usuarios :");
								
							while(st.hasMoreTokens()){
								System.out.println(st.nextToken());
							}
							//constestacion del servidor
						}else if (message.getPacket() == Message.PKT_ERR) {
							System.out.println("ERROR(tipo: lista de usuarios en sala): "+args[0]);
							// error delservidor	
						}
						
						break;
						
					case Message.TYPE_HELLO:
						//paquete del tipo mensaje de bienvenida del server
						if (message.getPacket() == Message.PKT_OK) {
							System.out.println("SERVER: "+args[0]);
							//constestacion del servidor
						}
						
						break;
						
					case Message.TYPE_MISC:
						//paquete del tipo demas errores
						if (message.getPacket() == Message.PKT_ERR) {
							System.out.println("ERROR(tipo: no especifico): "+args[0]);
							// error delservidor	
						}
						
						break;
				}
			}
		}
	}
}
