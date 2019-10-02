# Manual de uso del chat

## Requisitos
Descargar e instalar el Java Development Kit (JDK). se sugiere este enlace: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

## Ejecutar servidor
1. Abrir una terminal `bash` en la raíz del proyecto.
2. Ejecutar el siguiente comando:

```shell
cd src && javac app/Servidor.java -d ../bin && cd ../bin && java app.Servidor && cd ..
```
3. Escribir un puerto donde montar el servidor y presionar `ENTER`. Ejemplo:
```
Ingrese puerto del servidor: 8080
```
4. Si aparece un mensaje similar significa que el servidor está activo:
```
Servidor esperando por clientes en el puerto 8080
```

## Ejecutar cliente
1. Abrir una terminal `bash` en la raíz del proyecto.
2. Ejecutar el siguiente comando:

```shell
cd src && javac app/Cliente.java -d ../bin && cd ../bin && java app.Cliente && cd ..
```
3. Escribir un nick y presionar `ENTER`. Así es como los otros te verán en el chat. Ejemplo:
```
Ingrese su nick: mapache777
```
4. Escribir la dirección donde está montado el servidor y presionar `ENTER`. Ejemplo:
```
Ingrese la IP del servidor: localhost
```
5. Escribir el puerto donde está montado el servidor y presionar `ENTER`. Ejemplo:
```
Ingrese puerto del servidor: 8080
```
6. Si aparece un mensaje similar significa ya estás conectado en el chat y puedes empezar a escribir:
```
Conectado con la dirección localhost/127.0.0.1:8080
> [Wed Oct 02 19:10:22 CLST 2019] *** mapache777 (localhost/127.0.0.1) se unió a la conversación ***
> 
```
## Instrucciones para el cliente
- En caso de querer desloguearse del chat, escribir `!q` para salir o simplemente terminar el proceso.
- En caso de que el servidor deje de estar disponible, el programa le pedirá al usuario que ingrese un nuevo servidor, ver los pasos 4, 5, 6 de la sección "Ejecutar cliente".