package es.uniovi.UO217138;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTree;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Interface {
	private ChatIRC hiloPadre;
	private JFrame window;
	private JTree roomLists;
	public JTextArea txtServer;
	public HashMap<String, JPanel> room2Panel;
	public HashMap<String, JTextArea> room2TextArea;
	public HashMap<String, JTree> room2TreeUsers;
	private JTabbedPane panelTab;

	/**
	 * Crear la ventana.
	 */
	public Interface(ChatIRC hiloPadre) {
		this.room2Panel = new HashMap<String, JPanel>();
		this.room2TextArea = new HashMap<String, JTextArea>();
		this.room2TreeUsers = new HashMap<String, JTree>();
		this.hiloPadre = hiloPadre;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// Variables final para usar en las clases anonimas que manejan
		// los eventos
		final UserIn userIn = this.hiloPadre.userIn;
		
		// Dise–o de la ventana
		window = new JFrame("ChatIRC - "+this.hiloPadre.server+":"+this.hiloPadre.port);
		window.setBounds(100, 100, 715, 537);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(new BorderLayout(0, 0));
		
		// Zona central con pesta–as
		panelTab = new JTabbedPane(JTabbedPane.TOP);
		window.getContentPane().add(panelTab, BorderLayout.CENTER);
		
		// Pesta–a de servidor, con informaci—n de servicio y lista de salas
		JPanel panelServer = new JPanel();
		panelTab.addTab("Servidor", null, panelServer, null);
		panelServer.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("150px"),},
			new RowSpec[] {
				RowSpec.decode("default:grow"),}));
		
		// Scrollpane para la ventana de log de servidor
		JScrollPane scrollServer = new JScrollPane();
		scrollServer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollServer.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panelServer.add(scrollServer, "1, 1, fill, fill");
		
		// Textarea de log del servidor
		this.txtServer = new JTextArea();
		txtServer.setLineWrap(true);
		txtServer.setFont(new Font("Verdana", Font.PLAIN, 14));
		this.txtServer.setEditable(false);
		scrollServer.setViewportView(this.txtServer);
		
		// Lista de salas
		this.roomLists = new JTree();
		this.roomLists.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Salas") {
				{
				}
			}
		));
		this.roomLists.setRootVisible(false);
		// Al hacer doble click en una sala, se debe entrar a la misma.
		this.roomLists.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !e.isConsumed()) {
					e.consume(); // Limpiar evento
					
					// Obtener el nodo seleccionado y su sala.
					TreePath selPath = roomLists.getPathForLocation(e.getX(), e.getY());
					
					// Solo continuar si se pulso algun elemento
					if (selPath != null) {
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selPath.getLastPathComponent();
						final String roomName = (String) selectedNode.getUserObject();
						
						// Enviar peticion de JOIN a esa sala.
						SwingUtilities.invokeLater(new Runnable() { 
							public void run() {
								userIn.sendJoin(roomName);
							}
						});
					}
				}
			}
		});
		panelServer.add(this.roomLists, "3, 1, fill, fill");
		
		// Panel ingerior de la aplicacion
		JPanel panelInferior = new JPanel();
		window.getContentPane().add(panelInferior, BorderLayout.SOUTH);
		panelInferior.setLayout(new BorderLayout(5, 5));
		
		// Margen para evitar estar pegado a los bordes de la ventana
		Border current = panelInferior.getBorder();
		Border empty = new EmptyBorder(0, 6, 6, 6);
		if (current == null) {
			panelInferior.setBorder(empty);
		} else {
			panelInferior.setBorder(new CompoundBorder(empty, current));
		}
	
		// Creacion de boton enviar
		final JButton btnSend = new JButton("Enviar >");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Accion que se produce al apretar enviar
			}
		});
		panelInferior.add(btnSend, BorderLayout.EAST);
		
		// Creacion de campo de entrada de texto
		final JTextField msg = new JTextField();
		panelInferior.add(msg, BorderLayout.CENTER);
		msg.setColumns(10);
		msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// CUando se pulse enter en el campo de escritura, simular env’o con el boton
				btnSend.doClick();
			}
		});
		
		// Creacion de panel de comandos con scroll
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"/MSG","/JOIN","/LEAVE","/NICK","/QUIT", 
				"/LIST","/WHO"}));
		panelInferior.add(comboBox, BorderLayout.WEST);
		
		// Mostrar la ventana
		this.window.setVisible(true);
	}

	public void createRoom(String room) {
		final String[] roomName = new String[]{room};
		final JPanel panelRoom = new JPanel();
		
		// Configuracion del layout del panel de la nueva sala
		panelRoom.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("150px"),},
				new RowSpec[] {
				RowSpec.decode("default:grow"),
		}));

		// Scrollpane para el JTextArea de la sala
		JScrollPane scrollRoom = new JScrollPane();
		scrollRoom.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollRoom.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// Adjuntar el panel de la sala
		panelRoom.add(scrollRoom, "1, 1, fill, fill");

		// Textarea de la sala
		JTextArea txtRoom = new JTextArea();
		txtRoom.setLineWrap(true);
		txtRoom.setFont(new Font("Verdana", Font.PLAIN, 14));
		txtRoom.setEditable(false);
		scrollRoom.setViewportView(txtRoom);

		// Lista de usuarios en la sala
		JTree roomUsers = new JTree();
		roomUsers.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Usuarios") {
				{
				}
			}
		));
		roomUsers.setRootVisible(false);
		// Adjuntar al panel de la sala
		panelRoom.add(roomUsers, "3, 1, fill, fill");
		
		// Agregar las referncias a los HashMaps compartidos
		this.room2Panel.put(room, panelRoom);
		this.room2TextArea.put(room, txtRoom);
		this.room2TreeUsers.put(room, roomUsers);
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				// Se a–ade el panel a la GUI
				panelTab.addTab(roomName[0], null, panelRoom, null);
			}
		});
	}
	
	public void removeRoom(String room) {
		final String[] roomName = new String[]{room};
		
		
		// Agregar las referncias a los HashMaps compartidos
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				JPanel panel = room2Panel.get(roomName[0]);
				// Eliminar los paneles de la UI y eliminar los elementos
				panelTab.remove(panel);
				
				room2Panel.remove(roomName[0]);
				room2TextArea.remove(roomName[0]);
				room2TreeUsers.remove(roomName[0]);
			}
		});
	}
	
	public void print2Room(String room, String text) {
		final String[] args = new String[]{room, text};
		
		if (room2TextArea.get(room) != null) {
			SwingUtilities.invokeLater(new Runnable() { 
				public void run() {
					// Se carga la sala aqui porque esta en un objeto compartido que puede ser modificado/creado despues.
					JTextArea txtRoom = room2TextArea.get(args[0]);
					txtRoom.append(args[1]+"\n");
				}
			});
		} else {
			this.hiloPadre.serverLogPrintln("ERROR: Se intenta escribir a una sala no existente.");
		}
	}
	
	public void updateRoomList(String[] rooms) {
		final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Salas");
		
		for (int i = 0; i < rooms.length; i++) {
			if (rooms[i].length()>0)
			rootNode.add(new DefaultMutableTreeNode(rooms[i]));
		}
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				roomLists.setModel(new DefaultTreeModel(rootNode));
			}
		});
	}
	
	public void setUsersRoom(String room, ArrayList<String> users) {
		final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Usuarios");
		final String[] arg = new String[]{room};
		Object[] usersArray = users.toArray();
		
		for (int i = 0; i < usersArray.length; i++) {
			if (((String)usersArray[i]).length() > 0) {
				rootNode.add(new DefaultMutableTreeNode(usersArray[i]));
			}
		}
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				JTree usersRoom = room2TreeUsers.get(arg[0]);
				usersRoom.setModel(new DefaultTreeModel(rootNode));
			}
		});
	}
	
	public void closeWindow() {
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
				window.setVisible(false);
				window.dispose();
			}
		});
	}
}
