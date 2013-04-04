package es.uniovi.UO217138;

import java.util.StringTokenizer;

public class EntradaRed extends Thread {
	private BufferFifo buffersalida;
	private String salida;

	private String usuario;
	private String sala;
	private String mensaje;
	private Network entradadered;

	public EntradaRed(BufferFifo buffersalida, Network entradadered) {
		this.buffersalida = buffersalida;

		this.entradadered = entradadered;
		start();
	}

	public void run() {

		while (true) {

			try {
				salida = entradadered.recv();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			StringTokenizer st = new StringTokenizer(salida, ";");

			usuario = st.nextToken();

			sala = st.nextToken();

			mensaje = st.nextToken();

			salida = "/MSG " + usuario + " " + sala + " " + mensaje;

			try {
				synchronized (buffersalida) {

					buffersalida.put(salida);

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
