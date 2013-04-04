package es.uniovi.UO217138;

public class UserOut extends Thread {
	private BufferFifo bufferResponses;
	
	UserOut (BufferFifo bufferResponses) {
		this.bufferResponses = bufferResponses;
	}
	
	public void run() {
		Message message;
		
		while(true) {
			message = new Message();
			
			try {
				message = this.bufferResponses.get();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			
			if (message.getType() == Message.TYPE_MSG) {
				System.out.println(message.getRoom()+"|"+message.getNick()+">"+message.getMessage());
			}
		}
	}
}
