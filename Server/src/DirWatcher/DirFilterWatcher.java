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
import java.io.*;

public class DirFilterWatcher implements FileFilter {
  private String filter;

  public DirFilterWatcher() {
    this.filter = "";
  }

  public DirFilterWatcher(String filter) {
    this.filter = filter;
  }
  
  public boolean accept(File file) {
    if ("".equals(filter))
        return true;
    
    return (file.getName().endsWith(filter));
  }
}
