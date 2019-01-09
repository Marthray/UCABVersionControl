/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DirWatcher;

/**
 *
 * @author Marth
 */
import java.util.*;
import java.io.*;

/**
 *
 * @author Brian
 */
public class DirWatcher extends TimerTask {
    private String path;
    private File filesArray [];
    private HashMap dir = new HashMap();
    private DirFilterWatcher dfw;

  public DirWatcher(String path) {
    this(path, "");
  }

  public DirWatcher(String path, String filter) {
    this.path = path;
    dfw = new DirFilterWatcher(filter);
    filesArray = this.listf(this.path, null);
    
    File ref = new File("ref.txt");
    
    try{
        if(ref.length()==0){
            FileOutputStream fos = new FileOutputStream(ref);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            for (File f : filesArray){
                bw.write(f.getPath());
                bw.newLine();
            }

            bw.close();
        }
    }catch (FileNotFoundException e){
        System.out.println("No se encontró el archivo");
    }
    catch (IOException e){
        System.out.println("Error de escritura");
    }
    
    // transfer to the hashmap be used a reference and keep the
    // lastModfied value
    for(int i = 0; i < filesArray.length; i++) {
       dir.put(filesArray[i], new Long(filesArray[i].lastModified()));
    }
  }
  
    public File[] listf(String directoryName, ArrayList<File> files) {
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
  
  public final void run() {
    HashSet checkedFiles = new HashSet();
    filesArray = this.listf(path, null);
    
    // scan the files and check for modification/addition
    for(int i = 0; i < filesArray.length; i++) {
      Long current = (Long)dir.get(filesArray[i]);
      checkedFiles.add(filesArray[i]);
      if (current == null) {
        // new file
        dir.put(filesArray[i], new Long(filesArray[i].lastModified()));
        onChange(filesArray[i], "add");
      }
      else if (current.longValue() != filesArray[i].lastModified()){
        // modified file
        dir.put(filesArray[i], new Long(filesArray[i].lastModified()));
        onChange(filesArray[i], "modify");
      }
    }

    // now check for deleted files
    Set ref = ((HashMap)dir.clone()).keySet();
    ref.removeAll((Set)checkedFiles);
    Iterator it = ref.iterator();
    while (it.hasNext()) {
      File deletedFile = (File)it.next();
      dir.remove(deletedFile);
      onChange(deletedFile, "delete");
    }
  }

  public void localLog(String filepath, String action){
    String line;
    ArrayList<String> temp = new ArrayList<String>();
    try{
        
        if(action=="add"){ 
            temp.add(filepath+" "+action);
        }
        BufferedReader br = new BufferedReader(new FileReader("ref.txt"));
        while((line = br.readLine()) != null) {
            //cualquier accion menos agregar
            if (line.contains(filepath)){
                if((line.split(" ").length<2))
                    temp.add(line+" "+action);
                else if(!action.equals(line.split(" ")[1]))
                    temp.add(line.split(" ")[0]+" "+action);
            } else
                temp.add(line);
        }
        br.close();
        
        FileOutputStream fos = new FileOutputStream("ref.txt");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (String s : temp){
            bw.write(s);
            bw.newLine();
        }
        
        bw.close();
        
    }catch (FileNotFoundException e){
      System.out.println("No se encontró el archivo");
    }
    catch (IOException e){
      System.out.println("Error de escritura");
    }
  }
  
  protected void onChange( File file, String action ){
      System.out.println( "File "+ file.getAbsolutePath() +" action: " + action );
      
      localLog(file.getAbsolutePath(), action);
  }
}

