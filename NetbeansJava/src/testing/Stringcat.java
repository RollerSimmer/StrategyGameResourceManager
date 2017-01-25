/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testing;

/**
 *
 * @author John Anderson
 */
public class Stringcat
    {
    public static void main(String[] args)
        {
        String f="Steve";
        String s=" ";
        String l="Bartlet"; 
        StringBuilder n;
        n=new StringBuilder("");
        n.append(f);System.out.println(f);System.out.println(n);
        n.append(s);System.out.println(s);System.out.println(n);
        n.append(l);System.out.println(l);;System.out.println(n);
        }
    
    }
