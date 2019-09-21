
import java.net.*;
import java.io.*;
import java.util.*;


public class Cliente {

	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	private Socket socket;
	
	private String host, nick;
	private int puerto;
	
	Cliente(String host, int puerto, String nick) {
		this.host = host;
		this.puerto = puerto;
		this.nick = nick;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		limpiarPantalla();

		System.out.print("Ingrese la IP del servidor: ");
		String host = sc.nextLine();

		System.out.print("Ingrese el puerto: ");
		int puerto = sc.nextInt();

		sc.nextLine();

		System.out.print("Ingrese su nick: ");
		String nick = sc.nextLine();

		limpiarPantalla();

		Cliente cliente = new Cliente(host, puerto, nick);

		if (!cliente.iniciar())
			return;
		
		while (true) {
			System.out.print("> ");
			String mensaje = sc.nextLine();

			cliente.enviarPaquete(new Paquete(mensaje, cliente.getSocket().getInetAddress(), nick));
		}

		//sc.close();
		//cliente.disconnect();	
	}

	public Socket getSocket() {
		return socket;
	}
	
	public boolean iniciar() {
		try {
			socket = new Socket(host, puerto);
		} catch(Exception e) {
			imprimir("Error: " + e);
			return false;
		}
		
		String mensaje = "Conectado con la dirección " + socket.getInetAddress() + ":" + socket.getPort();
		imprimir(mensaje);
	
		try {
			inputStream  = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			imprimir("Error: " + e);
			return false;
		}

		new ClienteListener().start();

		try {
			outputStream.writeObject(new Paquete("!i", socket.getInetAddress(), nick));
		} catch (IOException eIO) {
			imprimir("Exception doing login : " + eIO);
			desconectar();
			return false;
		}

		return true;
	}

	private void imprimir(String mensaje) {
		System.out.println(mensaje);
		System.out.flush();
	}
	
	void enviarPaquete(Paquete paquete) {
		try {
			outputStream.writeObject(paquete);
		} catch(IOException e) {
			imprimir("Exception writing to server: " + e);
		}
	}

	private void desconectar() {
		try { 
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();
			if (socket != null) socket.close();
		} catch(Exception e) {

		}
	}

	static private void limpiarPantalla() {
		System.out.print("\033[H\033[2J");  
        System.out.flush();
	}

	class ClienteListener extends Thread {

		public void run() {
			while (true) {
				try {
					Paquete paquete = (Paquete) inputStream.readObject();
					System.out.println(paquete.getMensajeFormateado());
					System.out.print("> ");
				} catch(IOException e) {
					imprimir("Servidor has closed the connection: " + e);
					break;
				} catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}

