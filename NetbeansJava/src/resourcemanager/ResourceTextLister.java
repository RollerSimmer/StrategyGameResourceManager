/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcemanager;

/**
 * Testing class to list the contents of a resource graph
 * @author John Anderson
 */
public class ResourceTextLister
    {
    ResourceGraph graph;
    ResourceFileLoader loader;    

    public ResourceTextLister()
        {
        this.graph = new ResourceGraph();
        this.loader=new ResourceFileLoader(graph);
        }
    
    static public void main(String[] args)
        {
        ResourceTextLister lister=new ResourceTextLister();
        
        if(lister.loader.load("/home/youbumto/NetBeansProjects/JavaFXTest/resource.txt"))
            {
            ResourceLookupTable lookup=lister.graph.lookup;
            for(int id=0;id<lister.graph.lookup.slotsActive.size();id++)
                {
                if(lookup.slotsActive.get(id))
                    {
                    System.out.printf("Node %d: ",id);
                    
                    ResourceNode node=lookup.nodes.get(id);
                    System.out.printf("id=%d, ",node.id);

                    String name=lookup.names.get(id);
                    System.out.printf("name=%s, ",name);
                    System.out.print("dependencies={");
                    
                    int depencount=0;
                    for(Integer did: node.depenIds)
                        {
                        if(depencount>0)
                            System.out.print(", ");                        
                        
                        String dname=lookup.names.get(did);                        
                        System.out.print(dname);
                        
                        if(!lookup.slotsActive.get(did))
                            System.out.print("(MISSING)");                                                                                
                        
                        ++depencount;
                        }
                    System.out.print("}");
                    System.out.print("\n");                    
                    }
                }
            
            }
        else
            System.out.println("Error: couldn't load file");     
        }
        
    }
