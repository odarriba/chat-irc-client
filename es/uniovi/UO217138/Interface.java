package es.uniovi.UO217138;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTree;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;

public class Interface {
	private ChatIRC hiloPadre;
	private JFrame window;
	private JTree roomLists;
	public JTextArea txtServer;

	/**
	 * Crear la ventana.
	 */
	public Interface(ChatIRC hiloPadre) {
		this.hiloPadre = hiloPadre;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// Dise–o de la ventana
		window = new JFrame("ChatIRC - "+this.hiloPadre.server+":"+this.hiloPadre.port);
		window.setBounds(100, 100, 715, 537);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(new BorderLayout(0, 0));
		
		// Zona central con pesta–as
		JTabbedPane panelTab = new JTabbedPane(JTabbedPane.TOP);
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
		txtServer.setFont(new Font("Verdana", Font.PLAIN, 14));
		this.txtServer.setEditable(false);
		scrollServer.setViewportView(this.txtServer);
		
		// Lista de salas
		this.roomLists = new JTree();
		this.roomLists.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Salas") {
				{
					add(new DefaultMutableTreeNode("Uno"));
					add(new DefaultMutableTreeNode("Dos"));
				}
			}
		));
		this.roomLists.setRootVisible(false);
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

}
