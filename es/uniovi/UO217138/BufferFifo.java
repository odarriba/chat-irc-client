/*
 * Cliente de chat IRC-like
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
	private ArrayBlockingQueue<Message> msgQueue;

	/*
	 * Funci—n get();
	 * 
	 * Obtiene un mensaje del buffer, deteniendo la ejecuci—n del 
	 * hilo hasta que se obtenga un mensaje o de interrumpa con
	 * una excepci—n, que se devolver‡ al llamante.
	 */
	public Message get() throws InterruptedException {
		return msgQueue.take();
	}

	/*
	 * Funci—n put();
	 * 
	 * Introduce un mensaje en el buffer, bloqueando el hilo hasta
	 * que se pueda bloquear el buffer para acceder de forma
	 * s’ncrona (o que sea interrumpido por una excepci—n).
	 */
	public void put(Message mensaje) throws InterruptedException {
		msgQueue.put(mensaje);
	}
	
	/*
	 * Funci—n numElements();
	 * 
	 * Devuelve el nœmero de elementos en el buffer en el
	 * momento de la consulta.
	 */
	public Integer numElements() {
		return msgQueue.size();
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
