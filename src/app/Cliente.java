package app;

import java.net.*;
import java.io.*;
import java.util.*;


public class Cliente {

	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	private Socket socket;

	static private boolean servidorCorriendo = true;
	static private boolean estaDesconectado = false;
	
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

		System.out.print("Ingrese su nick: ");
		String nick = sc.nextLine();

		while (!estaDesconectado) {
			System.out.print("Ingrese la IP del servidor: ");
			String host = sc.nextLine();

			System.out.print("Ingrese el puerto: ");
			int puerto = sc.nextInt();

			sc.nextLine();

			limpiarPantalla();

			servidorCorriendo = true;

			Cliente cliente = new Cliente(host, puerto, nick);

			

			if (!cliente.iniciar())
				return;
			
			while (servidorCorriendo) {
				System.out.print("> ");
				String mensaje = sc.nextLine();

				if (mensaje.startsWith("!q")) {
					//sc.close();
					cliente.desconectar();
					estaDesconectado = true;
					break;
				}

				cliente.enviarPaquete(new Paquete(mensaje, cliente.getSocket().getInetAddress(), nick));
			}

			if (estaDesconectado) {
				break;
			}

			System.out.println("Parece que el servidor " + host + ":" + puerto + " ya no está disponible");

			//sc.close();
			cliente.desconectar();	
		}

	}

	public Socket getSocket() {
		return socket;
	}
	
	public boolean iniciar() {
		try {
			socket = new Socket(host, puerto);

			String mensaje = "Conectado con la dirección " + socket.getInetAddress() + ":" + socket.getPort();

			System.out.println(mensaje);

			ClienteListener listener = new ClienteListener();

			listener.start();

			inputStream  = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());

			outputStream.writeObject(new Paquete("!i", socket.getInetAddress(), nick));
		} catch (IOException e) {
			System.out.println("Excepción creado stream o en el login: " + e);
			desconectar();
			return false;
		} catch (NullPointerException e) {
			System.out.println("Excepción creado stream o en el login: " + e);
			desconectar();
			return false;
		} catch (Exception e) {
			return false;
		}
		

		return true;
	}
	
	void enviarPaquete(Paquete paquete) {
		if (servidorCorriendo) {
			try {
				outputStream.writeObject(paquete);
			} catch(IOException e) {
				System.out.println("Excepción escribiendo al server: " + e);
			}
		}
		
	}

	private void desconectar() {
		try { 
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();
			if (socket != null && !socket.isClosed()) socket.close();
		} catch(Exception e) {
			System.out.println("Excepción desconectando: " + e);
		}
	}

	static private void limpiarPantalla() {
		System.out.print("\033[H\033[2J");  
        System.out.flush();
	}

	class ServidorException extends Exception {
		public ServidorException(String mensaje) {
			super(mensaje);
		}
	}

	class ClienteListener extends Thread {

		public void run() {
			while (servidorCorriendo) {
				try {
					Paquete paquete = (Paquete) inputStream.readObject();
					System.out.println(paquete.getMensajeFormateado());
					System.out.print("> ");
				} catch (IOException e) {
					//System.out.println("Servidor detenido?");
					servidorCorriendo = false;
					this.interrupt();

					break;
				} catch (ClassNotFoundException e) {
					System.out.println("ClassNotFoundException: " + e);
				} catch (NullPointerException e) {
				} catch (Exception e) {
					System.out.println("Otra Exception: " + e);
				}
			}
		}
	}
}

