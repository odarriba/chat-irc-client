package es.uniovi.UO217138;

import java.util.StringTokenizer;

public class EntradaRed extends Thread {
	private Pilafifo bufferentrada;
	private String entrada;
	private String comando;
	private String usuario;
	private String sala;
	private String mensaje;
	private Network salidadered;

	public EntradaRed(Pilafifo bufferentrada, Network salidadered) {
		this.bufferentrada = bufferentrada;
		this.start();
		this.salidadered = salidadered;
	}

	public void run() {

		while (true) {
			try {
				synchronized (bufferentrada) {

					if (bufferentrada.vacia())
						bufferentrada.wait();

					entrada = bufferentrada.sacar();

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StringTokenizer st = new StringTokenizer(entrada);
			comando = st.nextToken();
			usuario = st.nextToken();
			if (st.hasMoreTokens()) {
				sala = st.nextToken();
				if (st.hasMoreTokens()) {
					mensaje = st.nextToken();
				}
			}

			if (comando.equals("/MSG")) {
				try {
					salidadered.send(comando + ";" + usuario + ";" + sala + ";"
							+ mensaje);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
			
			
			
			
		}
	}
}
