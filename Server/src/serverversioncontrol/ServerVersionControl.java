/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverversioncontrol;

import DirWatcher.DirWatcher;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author Brian
 */
public class ServerVersionControl {
    
    /**Recibe el txt con los cambios y las rutas relativas de los archivos del repo
     * 
     * @return
     * @throws IOException 
     */
    public String recibirCambios(ServerSocket serverSocket) throws IOException{

        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
        File f = new File("./recv.txt");
        f.getParentFile().mkdirs();

        try {
            socket = serverSocket.accept();
            System.out.println("Conexión con el cliente exitosa");
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        try {
            in = socket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        try {
            out = new FileOutputStream(f);
            System.out.println("Se creó el archivo");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }

        byte[] bytes = new byte[16*1024];

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }
        
        out.close();
        in.close();
        socket.close();
        
        System.out.println("El archivo se transfirió correctamente");
        return f.getPath();
    }
    
    /**Segun el archivo de cambios, se crean los archivos y se escribe en ellos cada que se recibe
     * 
     * @param path
     * @throws IOException 
     */
    public void save(ServerSocket serverSocket, String path) throws IOException{
            String line;
            BufferedReader br = new BufferedReader(new FileReader(path));
            while((line = br.readLine()) != null) {
                if(!line.contains("ref.txt")){

                    File f = null;
                    if((line.split(" ").length<2))
                        f = new File(line);
                    else
                        f = new File(line.split(" ")[0]);
                    f.getParentFile().mkdirs();

                    Socket socket = null;
                    InputStream in = null;
                    OutputStream out = null;

                    try {
                        socket = serverSocket.accept();
                        System.out.println("Conexión con el cliente exitosa");
                    } catch (IOException ex) {
                        System.out.println("Can't accept client connection. ");
                    }

                    try {
                        in = socket.getInputStream();
                    } catch (IOException ex) {
                        System.out.println("Can't get socket input stream. ");
                    }

                    try {
                        out = new FileOutputStream(f);
                        System.out.println("Se creó el archivo");
                    } catch (FileNotFoundException ex) {
                        System.out.println("File not found. ");
                    }

                    byte[] bytes = new byte[16*1024];

                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }

                    out.close();
                    in.close();
                    socket.close();

                    System.out.println("El archivo se transfirió correctamente");
                }
            }
    }
    
    public static void main(String[] args) throws IOException{
        // TODO code application logic here
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }
        
        while(true){
            ServerUtil.commit(serverSocket);
        }
    }
}
