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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/*
 * Clase ChatIRC
 * Esta clase actua como clase principal del cliente, mostrando la pantalla inicial y 
 * esperando a la respuesta del usuario respecto a los datos de conexion.
 */
public class ChatIRC extends Thread {
	public final static String version = "1.0";
	
	public String server;
	public Integer port;
	public Boolean DEBUG = false;
	public Boolean ejecucion = true;
	
	public String room = new String("pruebas");
	public String nick = new String();
	
	public Interface mainWindow;
	public final ChatIRC mainObject;
	
	// Hilos accesibles de E/S de mensajes
	public UserOut userOut;
	public UserIn userIn;
	
	// Hilos de red. No accesibles.
	private NetworkOut netOut;
	private NetworkIn netIn;
	
	// Buffers de mensajes. Privados.
	private BufferFifo bufferResponses;
	private BufferFifo bufferCommands; 
	
	public static void main(String[] args) {
		new ChatIRC();
		return;
	}
	
	public ChatIRC() {
		// Asignar el objeto actual a una variable final para ser
		// accesible desde los eventos de la ventana de bienvenida
		this.mainObject = this;
		
		// Mostrar la ventana de bienvenida
		createWelcomeScreen();
	}
	
	/*
	 * Ejecutor del arranque de la aplicacion. Es llamado al arrancar
	 * el hilo principal desde la ventana de bienvenida.
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// Crear los buffers intermedios
		this.bufferResponses = new BufferFifo(); // Buffer que almacena las respuestas que vienen de la red
		this.bufferCommands = new BufferFifo(); // Buffer que almacena los comandos del usuario
				
		// Crear hilos de E/S de mensajes
		// Son accesibles para poder acceder hacia/desde la GUI
		this.userOut = new UserOut(this.bufferResponses, this);
		this.userIn = new UserIn(this.bufferCommands, this);
				
		this.mainWindow = new Interface(this);
		
		serverLogPrintln("INFO: Ejecutando ChatIRC v"+ChatIRC.version);
		serverLogPrintln("");
		
		// Crear interfaces de red y hilos de procesamiento
		try {
			serverLogPrint("INFO: Conectando a "+this.server+":"+this.port+"...");
			Socket socket = new Socket(this.server, this.port);
			
			this.netOut = new NetworkOut(this.bufferCommands, socket, this);
			this.netIn = new NetworkIn(this.bufferResponses, socket, this);

			// Iniciar los hilos
			this.netIn.start();
			this.netOut.start();
			
			// Capturar el paquete inicial HELLO
			Message msgHello = new Message();
			
			try {
				msgHello = this.bufferResponses.get();
			} catch(InterruptedException e){
				serverLogPrint("Error! ");
				serverLogPrintln("Consulte la consola para tener mas info al respecto.");
				e.printStackTrace();
			}
			
			if (msgHello.getPacket() == Message.PKT_OK && msgHello.getType() == Message.TYPE_HELLO) {
				serverLogPrintln("Listo!"); // Conexion correcta
				
				// Arrancar el resto de hilos necesarios.
				this.userOut.start();
				this.userIn.start();
				
				serverLogPrintln("SERVER: "+msgHello.getArgs()[0]);
				
				// Fijacion inicial del nombre de usuario (nickname)
				serverLogPrintln("INFO: Intentando cambiar el nick a '"+this.nick+"'.");
				
				Message msgNick = new Message();
				msgNick.setPacket(Message.PKT_CMD);
				msgNick.setType(Message.TYPE_NICK);
				msgNick.setArgs(new String[]{this.nick});
				
				// Solicitar informacion sobre las salas disponibles
				serverLogPrintln("INFO: Solicitando info sobre las salas disponibles.");
				
				Message msgList = new Message();
				msgList.setPacket(Message.PKT_CMD);
				msgList.setType(Message.TYPE_LIST);
				msgList.setArgs(new String[]{});
				
				try {
					// Meter los mensajes en el buffer de comandos
					this.bufferCommands.put(msgNick);
					this.bufferCommands.put(msgList);
				} catch(InterruptedException e) {
					serverLogPrintln("ERROR");
					serverLogPrintln("Consulte la consola para tener mas info al respecto.");
					e.printStackTrace();
				}
			}
			else {
				serverLogPrintln("ERROR: No se recibio el comando HELLO esperado.");
			}
		} catch (IOException e) {
			serverLogPrintln("\nERROR: Error al crear el socket: "+e.getMessage());
			serverLogPrintln("Consulte la consola para tener mas info al respecto.");
			e.printStackTrace();
		}
	}
	
	/*
	 * Funcion para imprimir texto en la consola de servidor.
	 */
	public void serverLogPrint(String text) {
		final JTextArea txtServerLog = this.mainWindow.txtServer;
		final String[] textFinal = new String[1];
		
		textFinal[0] = text;
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				txtServerLog.append(textFinal[0]);
			}
		});
	}
	
	/*
	 * Funcion para imprimir texto en la consola de servidor.
	 * Finaliza con un salto de l�nea.
	 */
	public void serverLogPrintln(String text) {
		final JTextArea txtServerLog = this.mainWindow.txtServer;
		final String[] textFinal = new String[1];
		
		textFinal[0] = text+"\n";
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				txtServerLog.append(textFinal[0]);
			}
		});
	}

	/*
	 * Esta funcion crea la ventana de entrada donde se piden los datos para
	 * la conexion y se recogen para la conexion.
	 * Una vez que se tienen esos datos se lanza el hilo principal.
	 */
	private void createWelcomeScreen() {
		// Final los objetos que seran accedidos desde las acciones
		final JFrame welcomeScreen;
		final JTextField txtServer;
		final JTextField txtNick;
		final JSpinner slcPort;
		JLabel lblServidor;
		JLabel lblNick;
		final JButton btnConnect;
		JButton btnAbout;
		JButton btnExit;
		
		// Crear la ventan principal
		welcomeScreen = new JFrame("ChatIRC version "+ChatIRC.version);
		welcomeScreen.setResizable(false);
		welcomeScreen.setSize(new Dimension(398, 187));
		welcomeScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		welcomeScreen.getContentPane().setLayout(null);
		
		// Etiqueta del titulo
		JLabel lblChatircVersion = new JLabel("ChatIRC version "+ChatIRC.version);
		lblChatircVersion.setFont(new Font("Verdana", Font.BOLD, 16));
		lblChatircVersion.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatircVersion.setBounds(12, 12, 372, 25);
		welcomeScreen.getContentPane().add(lblChatircVersion);
		
		// Etiqueta del campo de servidor (direccion y puerto)
		lblServidor = new JLabel("Servidor");
		lblServidor.setHorizontalAlignment(SwingConstants.RIGHT);
		lblServidor.setFont(new Font("Verdana", Font.PLAIN, 14));
		lblServidor.setBounds(12, 53, 70, 15);
		welcomeScreen.getContentPane().add(lblServidor);
		
		// Etiqueta del nick
		lblNick = new JLabel("Nick");
		lblNick.setHorizontalAlignment(SwingConstants.RIGHT);
		lblServidor.setFont(new Font("Verdana", Font.PLAIN, 14));
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
				// Comprobacion de los datos
				if (txtServer.getText().length() == 0){
					JOptionPane.showMessageDialog(welcomeScreen, "Se debe introducir un servidor al que conectar", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (txtNick.getText().length() == 0){
					JOptionPane.showMessageDialog(welcomeScreen, "Se debe introducir un nick para conectar", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// Recoger la informacion, fijarla en los objetos necesarios
				// y arrancar el resto del programa
				mainObject.server = txtServer.getText();
				mainObject.port = (Integer)slcPort.getValue();
				mainObject.nick = txtNick.getText();
				
				// Cerrar la ventana
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
		
		// Al pulsar enter mientras se escribe en algun campo, intentar
		// enviar los datos
		txtServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Al pulsar enter, simular un click en el boton de conectar
				// para proceder con la conexion
				btnConnect.doClick();
			}
		});
		txtNick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Al pulsar enter, simular un click en el boton de conectar
				// para proceder con la conexion
				btnConnect.doClick();
			}
		});
		
		// Finalmente, mostrar la ventana
		welcomeScreen.setVisible(true);
	}
}
