package app;

import java.io.*;
import java.net.InetAddress;
import java.util.Date;

public class Paquete implements Serializable {
	private boolean esComandoONotificacion;
	private InetAddress host;
	private String mensaje, nick;
	private Date fecha;
	
	Paquete(String mensaje, InetAddress host, String nick) {
		this.mensaje = mensaje;
		this.host = host;
		this.nick = nick;
		this.fecha = new Date();
		this.esComandoONotificacion = mensaje.startsWith("!");
	}

	public boolean esComandoONotificacion() {
		return mensaje.startsWith("!");
	}

	public InetAddress getHost() {
		return host;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getNick() {
		return nick;
	}

	public String getMensajeFormateado() {
		if (mensaje.startsWith("!i")) {
			return "[" + fecha + "] *** " + nick + " (" + host + ") se unió a la conversación" + " ***";
		} else if (mensaje.startsWith("!q")) {
			return "[" + fecha + "] *** " + nick + " (" + host + ") ha salido de la conversación" + " ***";
		} else {
			return "[" + fecha + "] (" + host + ") " + nick + ": " + mensaje;
		}
		
	}

	public String getMensaje() {
		return mensaje;
	}
}
