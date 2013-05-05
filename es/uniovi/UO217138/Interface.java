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
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

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
		// Diseño de la ventana
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		// Zona central con pestañas
		
		
		JPanel panelInferior = new JPanel();
		frame.getContentPane().add(panelInferior, BorderLayout.SOUTH);
		panelInferior.setLayout(new BorderLayout(5, 5));
		
		Border current = panelInferior.getBorder();
		Border empty = new EmptyBorder(6, 6, 6, 6);
		if (current == null) {
			panelInferior.setBorder(empty);
		} else {
			panelInferior.setBorder(new CompoundBorder(empty, current));
		}
	
		// Creacion de boton enviar
		JButton btnenviar = new JButton("Enviar >");
		btnenviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Accion que se produce al apretar enviar
			}
		});
		
		panelInferior.add(btnenviar, BorderLayout.EAST);
		//Creacion de campo de entrada de texto
		msg = new JTextField();
		panelInferior.add(msg, BorderLayout.CENTER);
		msg.setColumns(10);
		
		//creacion de panel de comandos con scroll
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"/MSG","/JOIN","/LEAVE","/NICK","/QUIT", 
				"/LIST","/WHO"}));
		panelInferior.add(comboBox, BorderLayout.WEST);
		
	
	}

}
