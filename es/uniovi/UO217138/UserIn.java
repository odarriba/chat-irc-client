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
	private BufferFifo bufferCommands;
	private BufferedReader input;
	private String nick;
	
	/*
	 * Constructor de la clase UserIn
	 */
	public UserIn (BufferFifo bufferCommands, String nick) {
		this.bufferCommands = bufferCommands;
		this.nick = nick;
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
				message.setRoom("pruebas");
				message.setNick(this.nick);
				message.setMessage(textReaded);
				
				try {
					// Meter el mensaje en el buffer de comandos
					this.bufferCommands.put(message);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
