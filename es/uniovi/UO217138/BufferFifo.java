/*
 * Cliente de chat IRC-style
 * Trabajo grupal de Computadores
 * 
 * Clase BufferFifo
 * 
 * Autores:
 *  - Lucas çlvarez
 *  - îscar de Arriba
 *  - Estefan’a Gonz‡lez
 */
package es.uniovi.UO217138;

import java.util.concurrent.ArrayBlockingQueue;

/*
 * Clase BufferFifo
 * 
 * Esta clase ser‡ usada como un elemento intermedio entre 
 * las E/S de red y de usuario para evitar bloqueos, esperas
 * o cualquier otro efecto fruto de la desincronizaci—n entre
 * ambas partes.
 * 
 * En un principio s—lo instancia y trabaja con un objeto de
 * la clase ArrayBlockingQueue, pero si fuera necesario se podr’a
 * implementar un modelo Productor-Consumidor con buffer circular
 * usando sem‡foros y sincronizaci—n.
 */
public class BufferFifo {
	private ArrayBlockingQueue<Message> buffer;
	private static final int DEFAULT_SIZE = 20;
	
	/*
	 * Constructor de la clase BufferFifo
	 * 
	 * Si no se pasa par‡metro de tama–o, usar el de por defecto.
	 */
	BufferFifo() {
		this.buffer = new ArrayBlockingQueue<Message>(DEFAULT_SIZE);
	}
	
	/*
	 * Constructor de la clase BufferFifo
	 * 
	 * Si se pasa par‡metro de tama–o, usar el recibido.
	 * Si se recibe un valor <= 0, utilizar el valor por defecto
	 */
	BufferFifo(Integer size) {
		if (size <= 0) {
			this.buffer = new ArrayBlockingQueue<Message>(DEFAULT_SIZE);
		} else {
			this.buffer = new ArrayBlockingQueue<Message>(size);
		}
	}

	/*
	 * Funci—n get();
	 * 
	 * Obtiene un mensaje del buffer, deteniendo la ejecuci—n del 
	 * hilo hasta que se obtenga un mensaje o de interrumpa con
	 * una excepci—n, que se devolver‡ al llamante.
	 */
	public Message get() throws InterruptedException {
		return this.buffer.take();
	}

	/*
	 * Funci—n put();
	 * 
	 * Introduce un mensaje en el buffer, bloqueando el hilo hasta
	 * que se pueda bloquear el buffer para acceder de forma
	 * s’ncrona (o que sea interrumpido por una excepci—n).
	 */
	public void put(Message mensaje) throws InterruptedException {
		this.buffer.put(mensaje);
	}
	
	/*
	 * Funci—n numElements();
	 * 
	 * Devuelve el nœmero de elementos en el buffer en el
	 * momento de la consulta.
	 */
	public Integer numElements() {
		return this.buffer.size();
	}

	/*
	 * Funci—n empty();
	 * 
	 * Devuelve un valor booleano indicando si el buffer est‡
	 * vac’o o tiene algœn elemento.
	 */
	public boolean empty() {
		return (this.numElements() == 0);
	}

}
