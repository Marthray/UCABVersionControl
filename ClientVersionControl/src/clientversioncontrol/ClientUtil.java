/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientversioncontrol;

import DirWatcher.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Marth
 */
public class ClientUtil {
    /** Metodo estático que limpia la consola
     * 
     */
    public static void clearScreen() {  
        System.out.println(new String(new char[50]).replace("\0", "\r\n"));
    }
    
    /** Metodo que limpia el archivo ref.txt una vez se realiza el commit
     * 
     * @param ref 
     */
    public static void updateRef(String ref){
        try {
            List<String> lines = Files.readAllLines(new File("ref.txt").toPath());
            ArrayList<String> aux = new ArrayList<String>();
            for(String line:lines){
                if(line.contains(ref)){
                    line = line.split(" ")[0];
                    aux.add(line);
                } else {
                    aux.add(line);
                }
            }
            Files.write(new File("ref.txt").toPath(), aux);
        } catch (IOException ex) {
            Logger.getLogger(ClientUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Metodo que hace commit hacia el server principal del archivo seleccionado
     * 
     * @param f
     * @param ruta
     * @throws IOException 
     */
    public static void commit (File f, String ruta) throws IOException{
        String host = "127.0.0.1";
        Socket socket = new Socket(host, 4444);
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

        // Send first message
        dOut.writeUTF(ruta);
        dOut.flush(); // Send off the data
        
        dOut.close();
        socket.close();
        
        socket = new Socket(host, 4444);
        
        //Se envía el archivo al server
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(f);
        OutputStream out = socket.getOutputStream();

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }
        out.close();
        in.close();
        socket.close();
    }
 
    /**
     * @method: update
     * Se retornorá el commit con mayor ID del archivo solicitado.
     * @throws : IOException, 
     */
    public static void update(String file){
        String auxPath = "c:\\temp";
        String host = "127.0.0.1";
        InputStream in = null;
        OutputStream out = null;
        try {
            //Este bloque obtiene la ruta completa del archivo en el repo
            Socket socket = new Socket(host, 4444);
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeUTF(file);
            dOut.flush();
            dOut.close();
            socket.close();
            
            socket = new Socket(host, 4444);
            
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            String filePath = dIn.readUTF();
            dIn.close();
            socket.close();
            
            socket = new Socket (host, 4444);
            File serverFile = new File(auxPath+"\\"+filePath);
            serverFile.getParentFile().mkdirs();
            
            try {
                in = socket.getInputStream();
            } catch (IOException ex) {
                System.out.println("Can't get socket input stream. ");
            }

            try {
                out = new FileOutputStream(serverFile);
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
            
            
        } catch (IOException ex) {
            Logger.getLogger(ClientUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static ArrayList<String> getFilesFromServer() throws IOException{
        String host = "127.0.0.1";
        Socket socket = new Socket(host, 4444);
        String [] archivos = null;
        
        DataInputStream dIn = new DataInputStream(socket.getInputStream());
        String filesString = dIn.readUTF();
        dIn.close();
        socket.close();
        
        archivos = filesString.split("\\|");
        
        return new ArrayList<String>(Arrays.asList(archivos));
    }
    
    public static ArrayList<String> getVersions(String file){
        String host = "127.0.0.1";
        String[] versions = null;
        try {
            Socket socket = new Socket (host, 4444);
            
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

            // Send first message
            dOut.writeUTF(file);
            dOut.flush(); // Send off the data
            dOut.close();
            socket.close();
            
            socket = new Socket(host, 4444);
            
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            String versionsString = dIn.readUTF();
            dIn.close();
            
            versions = versionsString.split("\\|");
            
            socket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ClientUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new ArrayList<String>(Arrays.asList(versions));
    }
    
    public static void checkout(String folder, int version) throws IOException{
        String auxPath = "c:\\temp";
        String host = "127.0.0.1";
        InputStream in = null;
        OutputStream out = null;
        //Este bloque obtiene la ruta completa del archivo en el repo
        Socket socket = new Socket(host, 4444);
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        dOut.writeUTF(folder+"|"+version);
        dOut.flush();
        dOut.close();
        socket.close();
        
        socket = new Socket(host, 4444);
            
        DataInputStream dIn = new DataInputStream(socket.getInputStream());
        String filePath = dIn.readUTF();
        dIn.close();
        socket.close();

        socket = new Socket (host, 4444);
        File serverFile = new File(auxPath+"\\"+filePath);
        serverFile.getParentFile().mkdirs();

        try {
            in = socket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        try {
            out = new FileOutputStream(serverFile);
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
    }
}
