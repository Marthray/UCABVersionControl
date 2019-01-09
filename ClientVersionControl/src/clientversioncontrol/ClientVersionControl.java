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
            System.out.println("Desea hacer commit?");
            String opt = scan.nextLine();

            if("Y".equalsIgnoreCase(opt)){
                DirWatcher dir = new DirWatcher("c:/temp", "" );
                ClientUtil.commit(dir.listf("c:/temp", null), new File("c:/temp/ref.txt"));
            }
            
        }
        
        /*Socket socket = null;
        String host = "127.0.0.1";

        socket = new Socket(host, 4444);

        File file = new File("c:/temp/test.txt");
        
        FileOutputStream fos = new FileOutputStream("c:/temp/test.txt");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (int i = 0; i<5; i++){
            bw.write(String.format("Esto es la prueba numero %d",i));
            bw.newLine();
        }
        bw.close();
        
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        socket.close();*/
    }
}