package es.uniovi.UO217138;

public class NetworkOut extends Thread {
	private BufferFifo bufferCommands;
	private Message message;
	private Network netInterface;

	public NetworkOut(BufferFifo bufferCommands, Network netInterface) {
		this.bufferCommands = bufferCommands;
		this.netInterface = netInterface;
	}

	public void run() {
		// Bucle infinito de ejecución para la obtención de mensajes del buffer para enviarlos.
		while (true) {
			this.message = new Message();
			
			// Intentar conseguir un mensaje de la red
			try {
				this.message = this.bufferCommands.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Comprobar que el tipo es el esperado.
			/*
			 *  TODO: Aquí se realizará una comprobación más exahustiva de los parámetros necesarios
			 *  en cada tipo de trama a enviar, antes de ser enviada.
			 *  
			 *  Además, es donde se espera que se realice la conversión al formato binario final.
			 */
			if (this.message.getType() == Message.TYPE_MSG) {
				try {
					this.netInterface.send("/MSG;" + this.message.getNick() + ";" + this.message.getRoom() + ";"
							+ this.message.getMessage());
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
