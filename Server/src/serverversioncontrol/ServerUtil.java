/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverversioncontrol;

import DirWatcher.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Brian
 */
public class ServerUtil {
    /**
     * @method: commit
     * Aqui se va a recibir como parametro una lista de registros, donde el registro contendrá:
     * 1) El archivo a guardar. Datatype: File
     * 2) Ruta del archivo a guardar (utilizando "~" como la raíz del proyecto, no ruta absoluta). Datatype: String
     * @throws : IOException
     * @param: List<FileRecord> files
     */
    public static void commit(ServerSocket serverSocket) throws IOException{
        String repoRoot = "commits";
        DataInputStream dIn = null;
        Socket socket = null;
        String ruta = "";
        File f = null;
        InputStream in = null;
        OutputStream out = null;
        
        FileUtils.forceMkdir(new File(repoRoot));
        
        try {
            socket = serverSocket.accept();
            dIn = new DataInputStream(socket.getInputStream());
            ruta = dIn.readUTF();
            String name = new File(ruta).getName();
            int pos = name.lastIndexOf(".");
            if (pos > 0) {
                name = String.format("%s_%s",name.substring(0, pos),name.substring(pos+1));
                File temp = new File(String.format("%s\\%s",repoRoot,name));
                FileUtils.forceMkdir(temp);
                String[] directories = temp.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File current, String name) {
                      return new File(current, name).isDirectory();
                    }
                });
                System.out.println(directories);
                pos = ruta.lastIndexOf("\\");
                String commitPath = String.format("%s\\%d\\%s",temp.getPath(),directories.length+1,ruta.substring(0, pos));
                FileUtils.forceMkdir(new File(commitPath));
                f = new File(String.format("%s\\%s",commitPath, new File(ruta).getName()));
            }
            //f.getParentFile().mkdirs();
        } catch (IOException ex) {
            Logger.getLogger(ServerUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dIn.close();
                socket.close();
                System.out.println("Se creó el archivo que se recibirá: "+ruta);
            } catch (IOException ex) {
                Logger.getLogger(ServerUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
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
    
    /**
     * @method: update
     * Se retornorá el commit con mayor timestamp al solicitante.
     * @throws : IOException, 
     */
    public List<File> update(){
        return null;
    }
}
