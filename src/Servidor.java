import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
	private ArrayList<ClienteThread> clientes;
	private int puerto;
	private boolean seguir;
	
	public Servidor(int puerto) {
		this.puerto = puerto;
		clientes = new ArrayList<ClienteThread>();
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		limpiarPantalla();
        
        System.out.print("Ingrese puerto del servidor: ");
		int puerto = sc.nextInt();
		
		Servidor servidor = new Servidor(puerto);
		servidor.iniciar();

		sc.close();
	}
	
	public void iniciar() {
		seguir = true;

		try {
			ServerSocket serverSocket = new ServerSocket(puerto);

			limpiarPantalla();

			System.out.println("Servidor esperando por clientes en el puerto " + puerto);

			while (seguir) {
				Socket socket = serverSocket.accept();

				if (!seguir)
					break;

				ClienteThread t = new ClienteThread(socket);
				clientes.add(t);
				t.start();
			}
			
			serverSocket.close();

			for (int i = 0; i < clientes.size(); i++) {
				ClienteThread actual = clientes.get(i);
				actual.inputStream.close();
				actual.outputStream.close();
				actual.socket.close();
			}
				
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	protected void detener() {
		seguir = false;
	}
	
	static private void limpiarPantalla() {
		System.out.print("\033[H\033[2J");  
        System.out.flush();
	}

	private synchronized void enviarATodos(Paquete paquete) {
		
		System.out.println(paquete.getMensajeFormateado());

		for (int i = clientes.size() - 1; i >= 0; i--) {
			ClienteThread actual = clientes.get(i);
			if (!actual.enviarPaquete(paquete)) {
				clientes.remove(i);
			}
		}
	}

	synchronized void logout(String nick) {
		InetAddress host = null;

		for (int i = 0; i < clientes.size(); i++) {
			ClienteThread actual = clientes.get(i);
			if (actual.nick == nick) {
				host = actual.getSocket().getInetAddress();
				clientes.remove(i);
			}
		}
		enviarATodos(new Paquete("!q", host, nick));
	}

	class ClienteThread extends Thread {
		Socket socket;
		ObjectInputStream inputStream;
		ObjectOutputStream outputStream;

		String nick;
		Paquete paquete;
	
		ClienteThread(Socket socket) {
			this.socket = socket;

			try {
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream  = new ObjectInputStream(socket.getInputStream());

				
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		
		public String getNick() {
			return nick;
		}

		public Socket getSocket() {
			return socket;
		}	
		
		public void run() {
			boolean estaConectado = true;
			Paquete paquete = null;
			while (estaConectado) {
				try {
					paquete = (Paquete) inputStream.readObject();
					if (paquete.esComandoONotificacion() && paquete.getMensaje() == "!q") {
						estaConectado = false;
					}
					nick = paquete.getNick();
					enviarATodos(paquete);
					
				} catch (IOException e) {
					estaConectado = false;
				} catch (ClassNotFoundException e) {
					System.out.println("Error corriendo el thread" + e);
				} catch (Exception e) {
					System.out.println("Otra Excepction corriendo el thread" + e);
				}
			}
			logout(nick);
			cerrar();
		}

		private void cerrar() {
			try {
				if (outputStream != null) outputStream.close();
				if (inputStream != null) inputStream.close();
				if (socket != null) socket.close();				
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	
		public boolean enviarPaquete(Paquete paquete) {
			if (!socket.isConnected()) {
				cerrar();
				return false;
			}

			try {
				outputStream.writeObject(paquete);
			} catch (IOException e) {
				logout(paquete.getNick());
			}
			return true;
		}
	}
}

