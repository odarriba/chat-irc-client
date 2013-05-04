package es.uniovi.UO217138;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTree;
import javax.swing.JTabbedPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.Dimension;

public class Interface {

	private JFrame frame;
	private JTextField msg;

	/**
	 * Launch the application.
	 */
	//esta iniciado aqui pero esto se puede transportar tanquilamente al hilo lanzador y meterlo como parametro  a user in y out
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface window = new Interface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// creacion de ventan
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		//creacion de area de texto central
		JTextArea txtmsg = new JTextArea();
		frame.getContentPane().add(txtmsg, BorderLayout.CENTER);
		
		JPanel panelinferior = new JPanel();
		frame.getContentPane().add(panelinferior, BorderLayout.SOUTH);
		panelinferior.setLayout(new BorderLayout(0, 0));
	
		//creacion de boton enviar
		JButton btnenviar = new JButton("ENVIAR");
		btnenviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				//accion que se produce al apretar enviar
			}
		});
		
		panelinferior.add(btnenviar, BorderLayout.EAST);
		//creacion de campo de entrada de texto
		msg = new JTextField();
		panelinferior.add(msg, BorderLayout.CENTER);
		msg.setColumns(10);
		//creacion de panel de comandos con scroll
		String[] commandlist = {"/MSG","/JOIN","/LEAVE","/NICK","/QUIT", 
				"/LIST","/WHO"}; 
		JPanel paneldecomandos = new JPanel();
		paneldecomandos.setMinimumSize(new Dimension(10, 40));
		panelinferior.add(paneldecomandos, BorderLayout.WEST);
		JList commando = new JList(commandlist);
		paneldecomandos.add(commando);
		commando.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION); 
		commando.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		commando.setLayoutOrientation(JList.VERTICAL);
		commando.setVisibleRowCount(1);
		JScrollPane scrollPane = new JScrollPane(commando);
		paneldecomandos.add(scrollPane);
		//creacion de jtree
		DefaultMutableTreeNode padre = new DefaultMutableTreeNode("servidor");
		DefaultMutableTreeNode hijo1 = new DefaultMutableTreeNode("sala1");
		DefaultMutableTreeNode hijo2 = new DefaultMutableTreeNode("sala2");
		DefaultMutableTreeNode nieto1 = new DefaultMutableTreeNode("usuario1");
		DefaultTreeModel modelo = new DefaultTreeModel(padre);
		modelo.insertNodeInto(hijo1, padre, 0);
		modelo.insertNodeInto(hijo2, padre, 1);
		modelo.insertNodeInto(nieto1, hijo1, 0);
	
		JTree users = new JTree(modelo);
		// en teoria deveria funcionar perro no me añade los nodos
		frame.getContentPane().add(users, BorderLayout.EAST);
	
	
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		//creacion de panel para tabular conversaciones (tambien se podria hacer con el jtree haciendo doble click en la sala
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		panel.add(tabbedPane);
	}

}
