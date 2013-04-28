/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase NetworkIn
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
 * Clase NetworkIn
 * 
 * Clase que procesa los datos entrantes desde la red 
 * para convertirlos en respuestas de la clase Message
 * e introducirlos dentro del buffer de respuestas.
 */
public class NetworkIn extends Thread {
	private ChatIRC hiloPadre;
	private BufferFifo bufferResponses;
	private Socket socket;
	private BinaryProtocolConverter protocolConverter;

	/*
	 * Constructor de la clase NetworkIn
	 */
	public NetworkIn(BufferFifo bufferResponses, Socket netInterface, ChatIRC principal) {
		this.hiloPadre = principal;
		this.bufferResponses = bufferResponses;
		this.socket = netInterface;
		
		try {
			this.protocolConverter = new BinaryProtocolConverter(this.socket.getInputStream());
		} catch (IOException e) {
			System.err.println("Error al obtener el stream de entrada de la red: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * Mï¿½todo de ejecuciï¿½n contï¿½nua como Thread
	 */
	public void run() {
		Message inputMsg; // Mensaje obtenido desde la red
		
		while (this.hiloPadre.ejecucion) {
			inputMsg = new Message(); // Limpiar el mensaje y asegurar que no se repitan mensajes ante entradas no v‡lidas
			
			try {
				// Intentar obtener un paquete de la red
				inputMsg = this.protocolConverter.getMessage();
			} catch (IOException e) {
				System.err.println("Error al obtener el mensaje desde la red: "+e.getMessage());
				e.printStackTrace();
			}
			
			if (this.hiloPadre.DEBUG) {
				inputMsg.showDebug();
			}

			try {
				// Introducir el objeto en el buffer de respuestas (si es valido).
				if (inputMsg.esValido()) {
					this.bufferResponses.put(inputMsg);
				}
			}
			catch (InterruptedException e) {
				System.err.println("Error al cargar el mensaje en el buffer de respuestas: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
