package es.uniovi.UO217138;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserIn extends Thread {
	private BufferFifo bufferCommands;
	private BufferedReader input;
	private String nick;
	
	public UserIn (BufferFifo bufferCommands, String nick) {
		this.bufferCommands = bufferCommands;
		this.nick = nick;
		this.input =  new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run() {
		Message message;
		String textReaded;
		
		while(true) {
			message = new Message();
			textReaded = "";
			
			try {
				textReaded = this.input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (textReaded.length() > 0) {
				message.setType(Message.TYPE_MSG);
				message.setRoom("pruebas");
				message.setNick(this.nick);
				message.setMessage(textReaded);
				
				try {
					this.bufferCommands.put(message);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
