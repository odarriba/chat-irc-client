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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/*
 * Clase ChatIRC
 * Esta clase actua como clase principal del cliente, mostrando la pantalla inicial y 
 * esperando a la respuesta del usuario respecto a los datos de conexion.
 */
public class ChatIRC extends Thread {
	public String server;
	public Integer port;
	public Boolean DEBUG = false;
	public Boolean ejecucion = true;
	
	public String room = new String("pruebas");
	public String nick = new String();
	
	public final ChatIRC mainObject;
	
	public static void main(String[] args) {
		new ChatIRC();
		return;
	}
	
	public ChatIRC() {
		this.mainObject = this;
		createWelcomeScreen();
	}
	
	public void run() {
		System.out.println("ChatIRC v0.2 (hito 2)");
		System.out.println("-------------------------");
		
		// Crear los buffers intermedios
		BufferFifo bufferResponses = new BufferFifo(); // Buffer que almacena las respuestas que vienen de la red
		BufferFifo bufferCommands = new BufferFifo(); // Buffer que almacena los comandos del usuario
		
		// Crear hilos de interacci�n con el usuario
		UserOut userOut = new UserOut(bufferResponses, this);
		UserIn userIn = new UserIn(bufferCommands, this);
		
		// Crear interfaces de red y hilos de procesamiento
		try {
			System.out.print("INFO: Conectando a "+this.server+":"+this.port+"...");
			Socket socket = new Socket(this.server, this.port);
			
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
				System.err.println("Error: No se recibi� el comando HELLO esperado.");
			}
		} catch (IOException e) {
			System.err.println("Error al crear el socket: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * Esta funcion crea la ventana de entrada donde se piden los datos para
	 * la conexion y se recogen para la conexion.
	 */
	public void createWelcomeScreen() {
		// Final los objetos que seran accedidos desde las acciones
		final JFrame welcomeScreen;
		final JTextField txtServer;
		final JTextField txtNick;
		final JSpinner slcPort;
		JLabel lblServidor;
		JLabel lblNick;
		JButton btnConnect;
		JButton btnAbout;
		JButton btnExit;
		
		// Crear la ventan principal
		welcomeScreen = new JFrame("ChatIRC version 1.0");
		welcomeScreen.setResizable(false);
		welcomeScreen.setSize(new Dimension(398, 187));
		welcomeScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		welcomeScreen.getContentPane().setLayout(null);
		
		// Etiqueta del titulo
		JLabel lblChatircVersion = new JLabel("ChatIRC version 1.0");
		lblChatircVersion.setFont(new Font("Verdana", Font.BOLD, 16));
		lblChatircVersion.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatircVersion.setBounds(12, 12, 372, 25);
		welcomeScreen.getContentPane().add(lblChatircVersion);
		
		// Etiqueta del campo de servidor (direccion y puerto)
		lblServidor = new JLabel("Servidor");
		lblServidor.setHorizontalAlignment(SwingConstants.RIGHT);
		lblServidor.setFont(new Font("Verdana", Font.BOLD, 14));
		lblServidor.setBounds(12, 53, 70, 15);
		welcomeScreen.getContentPane().add(lblServidor);
		
		// Etiqueta del nick
		lblNick = new JLabel("Nick");
		lblNick.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNick.setBounds(12, 88, 70, 15);
		welcomeScreen.getContentPane().add(lblNick);
		
		// Campo de texto para el servidor
		txtServer = new JTextField();
		txtServer.setText("chat.oscardearriba.com");
		txtServer.setColumns(10);
		txtServer.setBounds(100, 49, 203, 25);
		welcomeScreen.getContentPane().add(txtServer);
		
		// Selector de puerto
		slcPort = new JSpinner();
		slcPort.setModel(new SpinnerNumberModel(7777, 1, 65535, 1));
		slcPort.setBounds(304, 49, 70, 25);
		welcomeScreen.getContentPane().add(slcPort);
		
		// Campo de texto para el nick
		txtNick = new JTextField();
		txtNick.setText("Usuario");
		txtNick.setColumns(10);
		txtNick.setBounds(100, 83, 274, 25);
		welcomeScreen.getContentPane().add(txtNick);
		
		// Boton de conectar
		btnConnect = new JButton("Conectar >");
		btnConnect.setBounds(267, 120, 117, 25);
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Recoger la informacion, fijarla en los objetos necesarios
				// y arrancar el resto del programa
				mainObject.server = txtServer.getText();
				mainObject.port = (Integer)slcPort.getValue();
				mainObject.nick = txtNick.getText();
				
				
				welcomeScreen.setVisible(false);
				welcomeScreen.dispose();
				
				mainObject.start();
			}
		});
		welcomeScreen.getContentPane().add(btnConnect);
		
		// Boton de informacion sobre la app
		btnAbout = new JButton("Acerca de");
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Mostrar informacion super-importante
				JOptionPane.showMessageDialog(welcomeScreen, "Este programa ha sido creado para el trabajo de la asignatura \nde Computadores en el curso 2012/2013.\n\n" +
						"Sus autores han sido:\n" +
						" - Lucas Álvarez Argüero\n" +
						" - Óscar de Arriba González\n" +
						" - Estefanía González García");
			}
		});
		btnAbout.setBounds(141, 120, 117, 25);
		welcomeScreen.getContentPane().add(btnAbout);
		
		// Boton de salir
		btnExit = new JButton("Salir");
		btnExit.setBounds(12, 120, 117, 25);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Al pulsar el botón, cerrar la ventana, acabando así la ejecucion
				// ya que no hay más hilos ejecutandose.
				welcomeScreen.setVisible(false);
				welcomeScreen.dispose();
			}
		});
		welcomeScreen.getContentPane().add(btnExit);
		
		welcomeScreen.setVisible(true);
	}
}
