/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverversioncontrol;

import DirWatcher.*;
import java.io.*;
import java.util.*;

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
    public void commit(List<File> files){
        for(File f : files){
            
        }
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
