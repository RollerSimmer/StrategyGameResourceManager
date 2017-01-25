/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * File saver for a strategy game resource file
 * @author John Anderson
 */
public class ResourceFileSaver
    {
    /** name of the file */
    String fname;
    String curline;
    boolean done=false;
    ResourceGraph graph;
    File file;
    PrintWriter fw;
    String line;
    int lnum=1;
    String currsc, curdepen;

    /**
     * ResourceFileSaver's constructor
     * @param graph The graph object to save
     */
    public ResourceFileSaver(ResourceGraph graph)
        {
        this.graph=graph;
        fname="";
        fw=null;
        file=null;
        }
    
    /**
     * Saves the resource graph to a text data file.
     * @param fname The name of the file to which the method should write 
     * @return true on success
     */
    public boolean save(String fname)
        {

        System.out.print("\n\n");
        
        System.out.printf("Saving file %s ...\n",fname);
        
        this.fname=fname;        
        file = new File(this.fname);
        
        if(file==null || !file.exists()) 
            {
            try 
                { file.createNewFile(); }
            catch(IOException e) 
                { System.err.println("IOException: " + e.getMessage()); }                                     
            System.out.println("New file was created.");
            }
        else if(!file.canWrite() ) 
            {
            System.out.println("Can't write file.");
            return false;
            }
        
        try 
            { fw = new PrintWriter(file); }
        catch(FileNotFoundException e) 
            { System.err.println("FileNotFoundException: " + e.getMessage()); }                 
        
        if(fw!=null)
            {
            lnum=1;

            System.out.println("The file was successfully opened for saving.");

            for(Integer rid=0;rid<graph.lookup.size();rid++)
                {
                saveNode(rid);
                }
            
            System.out.println("All lines processed.");
            
            }
        else
            System.out.printf("The file %s was not found.\n",fname);

        System.out.print("\n\n");
        
        fw.close();
        
        return true;
        }

    private void saveNode(Integer rid)
        {
        System.out.printf("Saving node %d.\r\n",rid);                        
        
        ResourceNode node=graph.lookup.nodes.get(rid);
        
        if(rid==null) return;
        if(!graph.isUsed(rid)) return;            
        
        System.out.printf("Really saving node %d now.\r\n",rid);                        
        
        String rname=graph.lookup.names.get(rid);        
        
        for(Integer did: node.depenIds)
            {
            if(did!=null)
                {
                String dname=graph.lookup.names.get(did);
                
                //System.out.printf("%s %s\r\n",rname,dname);                
                this.fw.printf("%s %s\r\n",rname,dname);
                }           
            }
        }
    }
