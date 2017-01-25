/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcemanager;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Structure that holds a single resource node ID and IDs of its dependencies. 
 * @author John Anderson
 */
public class ResourceNode
    {
    /** identification value */
    Integer id;
    /** list of dependency IDs for this resource */
    ArrayList<Integer> depenIds; 

    /** constructor
     * @param id identification value
     */
    public ResourceNode(Integer id)
        {
        this.id=id;
        this.depenIds=new ArrayList<>();
        }
    
    /** adds a dependency to the node
     * @param name the dependency to add
     * @param lookup the graph's lookup table
     */
    public void addDependency(String name,ResourceLookupTable lookup)
        {
        Integer did=lookup.map.get(name);
        if(did!=null)
            {
            depenIds.remove(did);
            depenIds.add(did);
            }
        }

    /** adds a dependency to the node
     * @param did the ID of the dependency to add
     */
    public void addDependency(Integer did)
        {
        this.removeDependency(did);
        if(did!=null)
            depenIds.add(did);
        }    

    /** removes a dependency from the node
     * @param did the ID of the dependency to remove from this node
     */    
    void removeDependency(Integer did)
        {
        if(did!=null)
            depenIds.remove(did);
        }
    
    /**
     * check to see if this is a useable resource with dependencies met
     * @param lookup the lookup table for this node's graph
     * @return true if useable
     */
    public boolean isUseable(ResourceLookupTable lookup)
        {
        for(Integer depid: depenIds)
            {
            // check if ID is active node
            if(!lookup.slotsActive.get(depid.intValue()))
                return false;
            }
        return true;
        }


    }
