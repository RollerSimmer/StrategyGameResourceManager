/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author John Anderson
 */
public class FileLoader
    {

    public FileLoader()
        {
        
        
        }
    
    
    static public void main(String[] args)
        {
        File f;
        Scanner fs = null;
        
        String fname="/home/youbumto/Desktop/programming/smu guildhall assignment/resource.txt";
        
        System.out.printf("Loading file %s ...\n",fname);
        f=new File(fname);
        try 
            { fs = new Scanner(f); }
        catch(FileNotFoundException e) 
            { System.err.printf("FileNotFoundException: " + e.getMessage()); }                 
        
        
        String line;
        
        Pattern p = Pattern.compile("(\\p{Alpha}+) (\\p{Alpha}+)");
        
        if(fs!=null)
            {
            int lnum=1;
            System.out.println("The file was successfully opened.");
            while(fs.hasNextLine())
                {                
                line = fs.nextLine();
                //System.out.println(line);
                System.out.printf("Line %d: %s\n",lnum,line);

                Scanner lineScanner=new Scanner(line);                
                lineScanner.useDelimiter(" ");

                String currsc, curdepen;
                currsc=lineScanner.next();
                curdepen=lineScanner.next();

                System.out.printf("The resource %s depends on the resource %s.\n",currsc,curdepen);
                lnum++;

                }
            }
        else
            System.out.printf("The file %s was not found.\n",fname);
        
        }    
    }
