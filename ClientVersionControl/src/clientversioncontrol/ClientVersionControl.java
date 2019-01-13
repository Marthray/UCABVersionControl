/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientversioncontrol;


import DirWatcher.*;
import java.io.*;
import java.util.*;
import clientversioncontrol.ClientUtil;
import java.net.Socket;

/**
 *
 * @author Marth
 */
public class ClientVersionControl {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        TimerTask task = new DirWatcher("c:/temp", "" );

            Timer timer = new Timer();
            timer.schedule( task , new Date(), 1000 );
        
        Scanner scan = new Scanner(System.in);
        while(true){
            String host = "127.0.0.1";
            System.out.println("¿Qué desea hacer?");
            System.out.println("1) Commit");
            System.out.println("2) Update");
            System.out.println("3) Checkout");
            String opt = scan.nextLine();

            if("1".equalsIgnoreCase(opt.trim())){
                ClientUtil.clearScreen();
                String line;
                ArrayList<String> archivos = new ArrayList<String>();
                int cont = 1;
                System.out.println("¿Qué archivos deseas \"committear\"?");
                BufferedReader br = new BufferedReader(new FileReader("ref.txt"));
                while((line = br.readLine()) != null) {
                    if(line.split(" ").length > 1){
                        System.out.println(String.format("%d) %s", cont, line));
                        archivos.add(line.split(" ")[0]);
                        cont++;
                    }
                }
                if(cont == 1){
                    ClientUtil.clearScreen();
                    System.out.println("No hay cambios que committear\n");
                } else {
                    int com = scan.nextInt();
                    if(!(com > archivos.size() || com < 1)){
                        String auxPath = "c:/temp/";
                        File f = new File(archivos.get(com-1));
                        String name = f.getName();
                        int pos = name.lastIndexOf(".");
                        if (pos > 0) {
                            Socket socket = new Socket(host, 4444);; // 
                            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

                            // Sending the server what's gonna be done
                            dOut.writeUTF("1");
                            dOut.flush();
                            dOut.close();
                            socket.close();
                            
                            name = archivos.get(com-1);
                            ClientUtil.commit(new File(auxPath+archivos.get(com-1)), name);
                            ClientUtil.updateRef(archivos.get(com-1));
                        }
                    }
                }
                
            } else if("2".equalsIgnoreCase(opt.trim())){
                ClientUtil.clearScreen();
                
                Socket socket = new Socket(host, 4444);; // 
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

                // Sending the server what's gonna be done
                dOut.writeUTF("2");
                dOut.flush();
                dOut.close();
                socket.close();
                
                int cont = 1;
                System.out.println("¿Qué archivo deseas actualizar?");
                ArrayList<String> files = ClientUtil.getFilesFromServer();
                for (String file : files){
                    System.out.println(String.format("%d) %s", cont, file));
                    cont++;
                }
                if(cont == 1){
                    ClientUtil.clearScreen();
                    System.out.println("No hay archivos en el servidor\n");
                } else{
                    int com = scan.nextInt();
                    if(!(com > files.size() || com < 1)){
                        String [] tempfile = files.get(com-1).split("\\.");
                        ClientUtil.update(String.format("%s_%s", tempfile[0],tempfile[1]));
                        ClientUtil.clearScreen();
                    }
                }
            } else if("3".equalsIgnoreCase(opt.trim())){
                ClientUtil.clearScreen();
                
                Socket socket = new Socket(host, 4444);; // 
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

                // Sending the server what's gonna be done
                dOut.writeUTF("3");
                dOut.flush();
                dOut.close();
                socket.close();
                
                int cont = 1;
                System.out.println("¿Qué archivo deseas trackear?");
                ArrayList<String> files = ClientUtil.getFilesFromServer();
                for (String file : files){
                    System.out.println(String.format("%d) %s", cont, file));
                    cont++;
                }
                if(cont == 1){
                    ClientUtil.clearScreen();
                    System.out.println("No hay archivos en el servidor\n");
                } else{
                    int com = scan.nextInt();
                    if(!(com > files.size() || com < 1)){
                        int versionCont = 1;
                        System.out.println("¿Qué versión del archivo"+files.get(com-1)+" deseas trackear?");
                        String [] tempfile = files.get(com-1).split("\\.");
                        ArrayList<String> versions = ClientUtil.getVersions(String.format("%s_%s", tempfile[0],tempfile[1]));
                        for (String version : versions){
                            System.out.println(String.format("%d) %s", versionCont, version));
                            versionCont++; 
                        }
                        ClientUtil.clearScreen();
                    }
                }
            } else
                ClientUtil.clearScreen();
        }
    }
}