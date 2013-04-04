/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase ChatIRC
 * 
 * Autores:
 *  - Lucas Alvarez
 *  - Oscar de Arriba
 *  - Estefania Gonzalez
 */
package es.uniovi.UO217138;

/*
 * Clase ChatIRC
 * Esta clase actï¿½a como clase principal del cliente, leyendo los parï¿½metros necesarios
 * de la ejecuciï¿½n y creando los hilos y los objetos intermedios necesarios.
 */
public class ChatIRC {
	
	public ChatIRC(String nick) {
		BufferFifo bufferResponses = new BufferFifo(); // Buffer que almacena las respuestas que vienen de la red
		BufferFifo bufferCommands = new BufferFifo(); // Buffer que almacena los comandos del usuario
		
		// Crear interfaces de red y hilos de procesamiento
		Network network = new Network();
		NetworkOut netOut = new NetworkOut(bufferCommands, network);
		NetworkIn netIn = new NetworkIn(bufferResponses, network);
		
		// Crear hilos de interacci—n con el usuario
		UserOut userOut = new UserOut(bufferResponses);
		UserIn userIn = new UserIn(bufferCommands, nick);

		// Iniciar los hilos
		netIn.start();
		netOut.start();
		userOut.start();
		userIn.start();
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Cantidad de parï¿½metros incorrectos.\n\nSintaxis:\n  java ChatIRC <nickname>");
			return;
		}
		
		ChatIRC principal = new ChatIRC(args[0]);
		return;
	}

}
