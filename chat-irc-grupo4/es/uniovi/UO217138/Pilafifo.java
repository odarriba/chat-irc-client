import java.util.concurrent.ArrayBlockingQueue;


public class Pilafifo {

	private ArrayBlockingQueue<String> msgQueue;
	private int cola=0;
	private boolean vacio =true;
	public String sacar() throws InterruptedException  {
		cola--;
		
		
		return msgQueue.take();
		}
	public void meter(String comando) throws InterruptedException  {
		cola++;
		
		
		msgQueue.put(comando);
		}
	public boolean vacia()  {
		
		if(cola<=0)
			vacio=true;
		else
			vacio=false;
			
		return vacio;
		
		
		}
	
	
	

}
