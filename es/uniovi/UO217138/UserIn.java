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
		Message message;
		String textReaded;
		
		while(true) {
			message = new Message();
			textReaded = "";
			
			try {
				// Leer una l’nea del teclado
				textReaded = this.input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Comprobar que se ha le’do texto
			if (textReaded.length() > 0) {
				// Crear el mensaje
				message.setType(Message.TYPE_MSG);
				message.setPacket(Message.PKT_CMD);
				
				String[] args = new String[3];
				args[0]=this.hiloPadre.nick;
				args[1]=this.hiloPadre.room;
				args[2]=textReaded;
				message.setArgs(args);
				
				try {
					// Meter el mensaje en el buffer de comandos
					this.bufferCommands.put(message);
				} catch(InterruptedException e) {
					System.err.println("Error al enviar el paquete al buffer de comandos: "+e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}
