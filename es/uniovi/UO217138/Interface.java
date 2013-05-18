package es.uniovi.UO217138;

import javax.swing.JFrame;
import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
	private final JTabbedPane panelTab = new JTabbedPane(JTabbedPane.TOP);

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
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.getContentPane().setLayout(new BorderLayout(0, 0));
		
		window.addWindowListener(new WindowAdapter() {                                                               
			 
			 
            //This is the function that will be invoked when the user clicks on the close button (the X)
            //At the end of this function , there will be no window on the screen , because we set in the 
            //previous line the frame's defautl close operation to DisposeOnClose               

            @Override                              
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "ÀSeguro que quieres salir del chat?", "Confirmaci—n", JOptionPane.YES_NO_OPTION);
                if (confirmed == JOptionPane.YES_OPTION) {
                    window.dispose();
                }
            }

            /*
            This is the function that is invoked when the window is closed (i.e , immediatly after the previous 
            function "windowClosing" exits). 
                            If the frame's default closing operation was "EXIT_ON_CLOSE" , this function wouldn't run.
            */
            @Override
            public void windowClosed(WindowEvent e) {
            	// Enviar el QUIT para acabar con la ejecuci—n del resto de hilos
                hiloPadre.userIn.sendQuit();
            }
        });
		
		// Zona central con pesta–as
		window.getContentPane().add(panelTab, BorderLayout.CENTER);
		
		// Pesta–a de servidor, con informaci—n de servicio y lista de salas
		JPanel panelServer = new JPanel();
		panelTab.addTab("Log Servidor", null, panelServer, null);
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
	
		// Creacion de panel de comandos con scroll
		final JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"/MSG","/JOIN","/LEAVE","/NICK","/QUIT", 
				"/LIST","/WHO"}));
		panelInferior.add(comboBox, BorderLayout.WEST);

		// Creacion de campo de entrada de texto
		final JTextField msg = new JTextField();
		panelInferior.add(msg, BorderLayout.CENTER);
		msg.setColumns(10);
				
		// Creacion de boton enviar
		final JButton btnSend = new JButton("Enviar >");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// se obtiene lo que este marcado en combo box y se guarda en comando
				final String comando=(String)comboBox.getSelectedItem();
				// el texto escrito tambien se guarda
				final String text = msg.getText();
				
				final String room = panelTab.getTitleAt(panelTab.getSelectedIndex());
				
				if (room.equals("Log Servidor") && (comando.toUpperCase().equals("/WHO") || comando.toUpperCase().equals("/MSG") || comando.toUpperCase().equals("/LEAVE"))){
					// Los comandos /LEAVE, /WHO y /MSG dependen de la sala en la que se este,
					// asi que no pueden ser llamados desde el log de servidor
					return;
				}
				
				// Enviar peticion a UserIn
				SwingUtilities.invokeLater(new Runnable() { 
					public void run() {
						// se compara comando para saber que se quiere hacer
						if (comando.toUpperCase().equals("/NICK")) {
							if (text.length() > 0) {
								hiloPadre.userIn.sendNick(text);
							}
						}
						else if (comando.toUpperCase().equals("/LEAVE")) {
							hiloPadre.userIn.sendLeave(room);
						}
						else if (comando.toUpperCase().equals("/LIST")) {
							hiloPadre.userIn.sendList();
						}
						else if (comando.toUpperCase().equals("/WHO")) {
							if (text.length() > 0) {
								hiloPadre.userIn.sendWho(room);
							}
						}
						else if (comando.toUpperCase().equals("/JOIN")) {
							if (text.length() > 0) {
								hiloPadre.userIn.sendJoin(text);
							}
						}
						else if (comando.toUpperCase().equals("/QUIT")) {
								hiloPadre.userIn.sendQuit();
						}
						
						else if(comando.toUpperCase().equals("/MSG")){
							if (text.length() > 0) {
								hiloPadre.userIn.sendMessage(text, room);
							}
						}
					}
				});
					
				//volvemos a colocar la combo box a /MSG y ponemos vacio el espacio de escribir
				comboBox.setSelectedIndex(0);
				msg.setText("");
			}
		});
		panelInferior.add(btnSend, BorderLayout.EAST);
		
		// Accion por defecto al pulsar ENTER en el campo de texto
		msg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// CUando se pulse enter en el campo de escritura, simular env’o con el boton
				btnSend.doClick();
			}
		});
		
		// Mostrar la ventana
		this.window.setVisible(true);
	}

	public void createRoom(String room) {
		final String[] roomName = new String[]{room};
		final JPanel panelRoom = new JPanel();
		
		SwingUtilities.invokeLater(new Runnable() { 
			public void run() {
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
				room2Panel.put(roomName[0], panelRoom);
				room2TextArea.put(roomName[0], txtRoom);
				room2TreeUsers.put(roomName[0], roomUsers);
		
				// Se a–ade el panel a la GUI
				panelTab.addTab(roomName[0], null, panelRoom, null);
				
				// Notidicar en la sala y en el log de servidor que se ha entrado en la sala
				hiloPadre.mainWindow.print2Room(roomName[0], "INFO: Te has unido a la sala "+roomName[0]);
				hiloPadre.serverLogPrintln("INFO: Te has unido a la sala "+roomName[0]);
				
				// Enviar mensaje de WHO para ver los usuarios de la sala
				hiloPadre.userIn.sendWho(roomName[0]);
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
