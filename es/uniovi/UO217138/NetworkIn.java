package es.uniovi.UO217138;
import java.util.StringTokenizer;

public class NetworkIn extends Thread {
	private BufferFifo bufferResponses;

	private Message message;
	private Network netInterface;

	public NetworkIn(BufferFifo bufferResponses, Network netInterface) {
		this.bufferResponses = bufferResponses;
		this.netInterface = netInterface;
	}

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

			this.message = new Message(); // Iniciar el objeto que contendrá los datos en el buffer
			
			/*
			 * A partir de este punto es código que sólo sirve en este primer momento, sin tener
			 * en cuenta el formato de paquete binario ni los diferentes mensajes, que tendrían
			 * que ser tratados para ser encapsulados dentro de la clase Message.
			 * 
			 * TODO: Cambiar el código para adaptarlo a las especificaciones finales.
			 */
			StringTokenizer st = new StringTokenizer(netData, ";");

			// Se debe comprobar que se reciben 3 o más parámetros
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
