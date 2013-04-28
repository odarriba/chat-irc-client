/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase NetworkOut
 * 
 * Autores:
 *  - Lucas Alvarez
 *  - Oscar de Arriba
 *  - Estefania Gonzalez
 */
package es.uniovi.UO217138;
import java.net.Socket;
import java.io.IOException;

/*
 * Clase NetworkOut
 * 
 * Clase que procesa los datos procedentes del buffer de comandos
 * en formato Message para que puedan ser enviados a la red.
 */
public class NetworkOut extends Thread {
	private ChatIRC hiloPadre;
	private BufferFifo bufferCommands;
	private Socket socket;
	private BinaryProtocolConverter protocolConverter;

	/*
	 * Constructor de la clase NetworkOut
	 */
	public NetworkOut(BufferFifo bufferCommands, Socket netInterface, ChatIRC principal) {
		this.hiloPadre = principal;
		this.bufferCommands = bufferCommands;
		this.socket = netInterface;
		
		try {
			this.protocolConverter = new BinaryProtocolConverter(this.socket.getOutputStream());
		} catch(IOException e) {
			System.err.println("Ha ocurrido un error al obtener el stream de salida: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 * Metodo de ejecucion continua como Thread
	 */
	public void run() {
		Message outputMsg;
		
		// Bucle infinito de ejecucion que obtiene mensajes del buffer y los saca por la red.
		while (true) {
			outputMsg = new Message(); // Limpiar el mensaje anterior con un objeto nuevo
			
			// Intentar conseguir un mensaje del buffer
			try {
				outputMsg = this.bufferCommands.get();
			} catch (InterruptedException e) {
				System.err.println("Error al obtener un mensaje desde el buffer de comandos: "+e.getMessage());
				e.printStackTrace();
			}
			
			if (this.hiloPadre.DEBUG) {
				outputMsg.showDebug();
			}
			
			// Comprobar que es v‡lido y enviarlo.
			try {
				if (outputMsg.esValido()) {
					this.protocolConverter.sendMessage(outputMsg);
				}
			} catch(IOException e){
				System.err.println("Error al enviar el mensaje a la red: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
}
