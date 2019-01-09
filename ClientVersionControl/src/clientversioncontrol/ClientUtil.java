/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientversioncontrol;

import DirWatcher.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author Marth
 */
public class ClientUtil {
    /**
     * @method: commit
     * Aqui se va a recibir como parametro una lista de registros, donde el registro contendrá:
     * 1) El archivo a guardar. Datatype: File
     * 2) Ruta del archivo a guardar (utilizando "~" como la raíz del proyecto, no ruta absoluta). Datatype: String
     * @throws : IOException
     * @param: List<FileRecord> files
     */
    public static void commit(File [] files, File changes){
        try{
            String host = "127.0.0.1";
            if(changes.length()>0){
                Socket socket = new Socket(host, 4444);
                //Se envía primero el archivo de cambios
                byte[] bytes = new byte[16 * 1024];
                InputStream in = new FileInputStream(changes);
                OutputStream out = socket.getOutputStream();

                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                out.close();
                in.close();
                socket.close();
                
                for(File f : files){
                    if(!"ref.txt".equals(f.getName())){
                        while(!socket.isClosed()){
                            System.out.println("Cerrado");
                        }
                        socket = new Socket(host, 4444);
                        long length = f.length();
                        bytes = new byte[16 * 1024];
                        in = new FileInputStream(f);
                        out = socket.getOutputStream();

                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                        out.close();
                        in.close();
                        socket.close();
                    }
                }
            }
        }catch (FileNotFoundException e){
            System.out.println("No se encontró el archivo");
        }
        catch (IOException e){
            System.out.println("Error de escritura");
        }
    }
    
    /**
     * @method: update
     * Se retornorá el commit con mayor timestamp al solicitante.
     * @throws : IOException, 
     */
    public static List<File> update(){
        return null;
    }
}
