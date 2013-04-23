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
import java.net.Socket;
import java.io.IOException;

/*
 * Clase ChatIRC
 * Esta clase actua como clase principal del cliente, leyendo los parametros necesarios
 * de la ejecucion y creando los hilos y los objetos intermedios necesarios.
 */
public class ChatIRC {
	public String server;
	public Integer puerto;
	public boolean DEBUG = true;
	
	public String room = new String();
	public String nick = new String();
	
	public ChatIRC(String nick, String server, Integer puerto) {
		this.server = server;
		this.puerto = puerto;
		this.nick = nick;
		
		System.out.println("ChatIRC v0.2 (hito 2)");
		System.out.println("-------------------------");
		
		// Crear los buffers intermedios
		BufferFifo bufferResponses = new BufferFifo(); // Buffer que almacena las respuestas que vienen de la red
		BufferFifo bufferCommands = new BufferFifo(); // Buffer que almacena los comandos del usuario
		
		// Crear hilos de interacci—n con el usuario
		UserOut userOut = new UserOut(bufferResponses, this);
		UserIn userIn = new UserIn(bufferCommands, this);
		
		// Crear interfaces de red y hilos de procesamiento
		try {
			System.out.print("INFO: Conectando a "+this.server+":"+this.puerto+"...");
			Socket socket = new Socket(this.server, this.puerto);
			
			NetworkOut netOut = new NetworkOut(bufferCommands, socket, this);
			NetworkIn netIn = new NetworkIn(bufferResponses, socket, this);

			// Iniciar los hilos
			netIn.start();
			netOut.start();
			
			// Capturar el paquete inicial HELLO
			Message msgHello = new Message();
			
			try {
				msgHello = bufferResponses.get();
			} catch(InterruptedException e){
				System.err.println("Error!");
				System.err.println("Error al conectar con el servidor: "+e.getMessage());
				e.printStackTrace();
			}
			
			if (msgHello.getPacket() == Message.PKT_OK && msgHello.getType() == Message.TYPE_HELLO) {
				System.out.println("Listo!"); // Conexion correcta
				
				System.out.println("SERVER: "+msgHello.getArgs()[0]);
				// Fijacion inicial del nombre de usuario (nickname)
				System.out.print("INFO: Intentando cambiar el nick a '"+this.nick+"'...");
				
				Message msgNick = new Message();
				msgNick.setPacket(Message.PKT_CMD);
				msgNick.setType(Message.TYPE_NICK);
				msgNick.setArgs(new String[]{this.nick});
				
				try {
					// Meter el mensaje en el buffer de comandos
					bufferCommands.put(msgNick);
					System.out.println("Listo!");
				} catch(InterruptedException e) {
					System.err.println("Error!");
					System.err.println("Error al enviar el paquete al buffer de comandos: "+e.getMessage());
					e.printStackTrace();
				}
				
				userOut.start();
				userIn.start();
			}
			else {
				System.err.println("Error: No se recibi— el comando HELLO esperado.");
			}
		} catch (IOException e) {
			System.err.println("Error al crear el socket: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Cantidad de parametros incorrectos.\n\nSintaxis:\n  java ChatIRC <nickname> <servidor> <puerto>");
			return;
		}
		
		Integer puerto = new Integer(args[2]);
		
		ChatIRC principal = new ChatIRC(args[0], args[1], puerto);
		return;
	}

}
