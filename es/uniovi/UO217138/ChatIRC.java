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

public class ChatIRC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Pilafifo bufferentrada = new Pilafifo();
		Pilafifo buffersalida = new Pilafifo();
		Network networksalida = new Network();
		Network networkentrada = new Network();
		SalidaRed salidared = new SalidaRed(buffersalida, networksalida);
		EntradaRed entradared = new EntradaRed(bufferentrada, networkentrada);

	}

}
