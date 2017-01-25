/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * File loader for a strategy game resource file
 * @author John Anderson
 */
public class ResourceFileLoader
    {
    /** name of the file */
    String fname;
    String curline;
    boolean done=false;
    ResourceGraph graph;
    File file;
    Scanner fs;
    String line;
    int lnum=1;
    String currsc, curdepen;
    
    /**
     * Constructs a ResourceFileLoader object
     * @param graph The graph into which the file should be loaded
     */
    public ResourceFileLoader(ResourceGraph graph)
        {
        this.graph=graph;
        fname="";
        fs=null;
        file=null;
        }
    
    /**
     * Loads a resource data text file into a resource graph
     * @param fname The name of the file holding resource data
     * @return true if successful
     */
    public boolean load(String fname)
        {

        System.out.printf("Loading file %s ...\n",fname);
        
        this.fname=fname;        
        file = new File(this.fname);
        if(file==null || !file.canRead() || !file.exists()) return false;
        
        try 
            { fs = new Scanner(file); }
        catch(FileNotFoundException e) 
            { System.err.println("FileNotFoundException: " + e.getMessage()); }                 
        
        if(fs!=null)
            {
            System.out.println("Clearing old graph.");
            graph.clear();            
            
            lnum=1;

            System.out.println("The file was successfully opened for reading.");

            done=!fs.hasNextLine();
            while(!done)
                loadLine();
            
            System.out.println("All lines processed.");
            
            }
        else
            System.out.printf("The file %s was not found.\n",fname);
        
        fs.close();
        
        return true;
        }
    
    void loadLine()
        {
        //file.
        line = fs.nextLine();
        //System.out.println(line);
        System.out.printf("Line %d: %s\n",lnum,line);

        Scanner lineScanner=new Scanner(line);                
        lineScanner.useDelimiter(" ");
        
        if(lineScanner.hasNext())
            currsc=lineScanner.next();
        else
            currsc="";
        if(lineScanner.hasNext())
            curdepen=lineScanner.next();
        else
            curdepen="";
        
        addResource();

        System.out.printf("The resource %s depends on the resource %s.\n",currsc,curdepen);
        lnum++;
        
        done=!fs.hasNextLine();
        }
    
    void addResource()
        {
        // adds a resource to the graph. 
        if(!currsc.equals("")&&!curdepen.equals(""))
            graph.addResourceLink(currsc,curdepen);
        }
    }
