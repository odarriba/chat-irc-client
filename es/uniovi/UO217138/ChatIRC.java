/*
 * Cliente de chat IRC-like
 * Trabajo grupal de Computadores
 * 
 * Clase ChatIRC
 * 
 * Autores:
 *  - Lucas çlvarez
 *  - îscar de Arriba
 *  - Estefan’a Gonz‡lez
 */
package es.uniovi.UO217138;

/*
 * Clase ChatIRC
 * Esta clase actœa como clase principal del cliente, leyendo los par‡metros necesarios
 * de la ejecuci—n y creando los hilos y los objetos intermedios necesarios.
 */
public class ChatIRC {
	
	ChatIRC(String nick) {
		BufferFifo bufferResponses = new BufferFifo(); // Buffer que almacena las respuestas que vienen de la red
		BufferFifo bufferCommands = new BufferFifo(); // Buffer que almacena los comandos del usuario
		Network network = new Network();
		SalidaRed netIn = new SalidaRed(bufferCommands, network);
		EntradaRed netOut = new EntradaRed(bufferResponses, network);
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Cantidad de par‡metros incorrectos.\n\nSintaxis:\n  java ChatIRC <nickname>");
			return;
		}
		
		ChatIRC principal = new ChatIRC(args[0]);
		return;
	}

}
