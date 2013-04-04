/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase BufferFifo
 * 
 * Autores:
 *  - Lucas çlvarez
 *  - îscar de Arriba
 *  - Estefan’a Gonz‡lez
 */
package es.uniovi.UO217138;
import java.util.StringTokenizer;

/*
 * Clase NetworkIn
 * 
 * Clase que procesa los datos entrantes desde la red 
 * para convertirlos en respuestas de la clase Message
 * e introducirlos dentro del buffer de respuestas.
 */
public class NetworkIn extends Thread {
	private BufferFifo bufferResponses;
	private Message message;
	private Network netInterface;

	/*
	 * Constructor de la clase NetworkIn
	 */
	public NetworkIn(BufferFifo bufferResponses, Network netInterface) {
		this.bufferResponses = bufferResponses;
		this.netInterface = netInterface;
	}

	/*
	 * MŽtodo de ejecuci—n cont’nua como Thread
	 */
	public void run() {
		String netData; // Datos obtenidos desde la red
		
		while (true) {
			netData = "";
			
			try {
				// Intentar obtener un paquete de la red
				netData = this.netInterface.recv();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			this.message = new Message(); // Iniciar el objeto que contendrÃ¡ los datos en el buffer
			
			/*
			 * A partir de este punto es cÃ³digo que sÃ³lo sirve en este primer momento, sin tener
			 * en cuenta el formato de paquete binario ni los diferentes mensajes, que tendrÃ­an
			 * que ser tratados para ser encapsulados dentro de la clase Message.
			 * 
			 * TODO: Cambiar el cÃ³digo para adaptarlo a las especificaciones finales.
			 */
			StringTokenizer st = new StringTokenizer(netData, ";");

			// Se debe comprobar que se reciben 3 o mÃ¡s parÃ¡metros
			if (st.countTokens() >= 3) {
				this.message.setType(Message.TYPE_MSG);
				this.message.setNick(st.nextToken());
				this.message.setRoom(st.nextToken());
				this.message.setMessage(st.nextToken());
				
				while (st.hasMoreTokens()) {
					// Posibilidad de enviar mensajes con ';' en medio.
					this.message.setMessage(this.message.getMessage()+";"+st.nextToken());
				}
	
				try {
					// Introducir el objeto en el buffer de respuestas.
					this.bufferResponses.put(this.message);
				}
				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
