/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

import java.util.ArrayList;

/**
 * 
 * @author John Anderson
 */
public class ArrayListDelete
    {
    
    ArrayList<Integer> a;

    public ArrayListDelete()
        {
        this.a = new ArrayList<>();
        
        for(Integer i=1;i<25;i++)
            a.add(i%5);
        
        Integer b=a.get(12);        
        
        System.out.format("b=%d size=%d\n",b,a.size());        
        System.out.println();
        
        for(Integer i=0;i<a.size();i++)
            System.out.printf("%d\t",i);
        System.out.println();
        for(Integer i:a)
            System.out.printf("%d\t",i);
        System.out.println();
       
        a.remove(12);

        System.out.format("b=%d size=%d\n",b,a.size());        
        System.out.println();

        for(Integer i=0;i<a.size();i++)
            System.out.printf("%d\t",i);
        System.out.println();
        for(Integer i:a)
            System.out.printf("%d\t",i);
        System.out.println();
        }  
    
    public static void main(String[] args)
        {
        System.out.println("Starting program.");
        ArrayListDelete ald = new ArrayListDelete();
        }    
    }
