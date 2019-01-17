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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
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
                String commitPath="";
                if(pos > 0)
                   commitPath = String.format("%s\\%d\\%s",temp.getPath(),directories.length+1,ruta.substring(0, pos));
                else
                    commitPath = String.format("%s\\%d",temp.getPath(),directories.length+1);
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
    public static void update(ServerSocket serverSocket){
        Socket socket = null;
        String rootDir = ".\\commits";
        
        try {
            socket = serverSocket.accept();
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            String fileString = dIn.readUTF();
            dIn.close();
            socket.close();
            
            File temp = new File(String.format("%s\\%s",rootDir,fileString));
            String[] directories = temp.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                  return new File(current, name).isDirectory();
                }
            });
            
            String basePath = String.format("%s\\%s\\%s",rootDir,fileString,directories[directories.length - 1]);
            String relativePath = "";
            
            temp = new File(basePath);
            
            File [] files = ServerUtil.listf(basePath, null);
            
            if(files[0].getPath().startsWith(basePath)){
                relativePath = files[0].getPath().substring(basePath.length()+1);
            }
            
            socket = serverSocket.accept();
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

            dOut.writeUTF(relativePath);
            dOut.flush(); // Send off the data

            dOut.close();
            socket.close();
            
            socket = serverSocket.accept();
            
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(files[0]);
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.close();
            in.close();
            
            socket.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ServerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void returnFiles(ServerSocket serverSocket){
        Socket socket = null;
        String files = "";
        String rootDir = "./commits";
        File temp = new File(rootDir);
        
        String[] directories = temp.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
              return new File(current, name).isDirectory();
            }
        });
        
        for (int i = 0; i<directories.length; i++){
            String current = String.format("%s.%s",directories[i].split("_")[0],directories[i].split("_")[1]);
            if(i < directories.length - 1)
                files = files.concat(current+"|");
            else
                files = files.concat(current);
        }
        
        try {
            socket = serverSocket.accept();
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

            // Send first message
            dOut.writeUTF(files);
            dOut.flush(); // Send off the data

            dOut.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static File[] listf(String directoryName, ArrayList<File> files) {
        File directory = new File(directoryName);
        // Get all files from a directory.
        File[] fList = directory.listFiles();
        
        if(files == null){
            files = new ArrayList<File>();
        }
        
        if(fList != null){
            for (File file : fList) {      
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    listf(file.getPath(), files);
                }
            }
            return files.toArray(new File[files.size()]);
        }
        return null;
    }

    public static void checkout(ServerSocket serverSocket) {
        Socket socket = null;
        String rootDir = ".\\commits";
        
        try {
            socket = serverSocket.accept();
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            String fileString = dIn.readUTF();
            dIn.close();
            socket.close();
            
            String [] tempString = fileString.split("\\|");
            String basePath = String.format("%s\\%s\\%s",rootDir,tempString[0],tempString[1]);
            String relativePath = "";
            
            File temp = new File(basePath);
            
            File [] files = ServerUtil.listf(basePath, null);
            
            if(files[0].getPath().startsWith(basePath)){
                relativePath = files[0].getPath().substring(basePath.length()+1);
            }
            
            socket = serverSocket.accept();
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

            dOut.writeUTF(relativePath);
            dOut.flush(); // Send off the data

            dOut.close();
            socket.close();
            
            socket = serverSocket.accept();
            
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(files[0]);
            OutputStream out = socket.getOutputStream();

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
            out.close();
            in.close();
            
            socket.close();
            
        } catch(IOException e){
            
        }
    }

    static void returnVersions(ServerSocket serverSocket) {
        String rootDir = "commits";
        String versions = "";
        try {
            Socket socket = serverSocket.accept();
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            String folder = dIn.readUTF();
            dIn.close();
            socket.close();
            
            
            File temp = new File(rootDir+"\\"+folder);
            String[] directories = temp.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
              return new File(current, name).isDirectory();
            }
            });
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            
            for (int i = 0; i<directories.length; i++){
                if(i < directories.length - 1)
                    versions = versions.concat(String.format("%s %s|",directories[i], sdf.format(new File(rootDir+"\\"+folder+"\\"+directories[i]).lastModified())));
                else
                    versions = versions.concat(String.format("%s %s",directories[i], sdf.format(new File(rootDir+"\\"+folder+"\\"+directories[i]).lastModified())));
            }
            
            socket = serverSocket.accept();
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            // Send first message
            dOut.writeUTF(versions);
            dOut.flush(); // Send off the data
            dOut.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
