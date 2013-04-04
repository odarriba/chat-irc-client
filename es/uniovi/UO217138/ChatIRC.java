/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase ChatIRC
 * 
 * Autores:
 *  - Lucas �lvarez
 *  - �scar de Arriba
 *  - Estefan�a Gonz�lez
 */
package es.uniovi.UO217138;

/*
 * Clase ChatIRC
 * Esta clase act�a como clase principal del cliente, leyendo los par�metros necesarios
 * de la ejecuci�n y creando los hilos y los objetos intermedios necesarios.
 */
public class ChatIRC {
	
	public ChatIRC(String nick) {
		BufferFifo bufferResponses = new BufferFifo(); // Buffer que almacena las respuestas que vienen de la red
		BufferFifo bufferCommands = new BufferFifo(); // Buffer que almacena los comandos del usuario
		
		Network network = new Network();
		NetworkOut netOut = new NetworkOut(bufferCommands, network);
		NetworkIn netIn = new NetworkIn(bufferResponses, network);
		
		UserOut userOut = new UserOut(bufferResponses);
		UserIn userIn = new UserIn(bufferCommands, nick);

		netIn.start();
		netOut.start();
		userOut.start();
		userIn.start();
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Cantidad de par�metros incorrectos.\n\nSintaxis:\n  java ChatIRC <nickname>");
			return;
		}
		
		ChatIRC principal = new ChatIRC(args[0]);
		return;
	}

}
