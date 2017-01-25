/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcemanager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Lookup table used primarily by a resource graph which allows hashed search by name.
 * @author John Anderson
 */
public class ResourceLookupTable
    {        
    HashMap<String,Integer> map;
    ArrayList<ResourceNode> nodes;    
    ArrayList<Boolean> slotsActive;    
    ArrayList<String> names;
    ArrayList<UsabilityState> slotsUsable;
    ArrayList<ReferenceState> slotsReferenced;

    /**
     * ResourceLookupTable's constructor
     */
    public ResourceLookupTable()
        {
        this.map = new HashMap<>();
        this.nodes = new ArrayList<>();
        this.names = new ArrayList<>();
        this.slotsActive=new ArrayList<>();
        this.slotsUsable=new ArrayList<>();
        this.slotsReferenced=new ArrayList<>();
        }
    
    /**
     * Retrieves the ID corresponding to a resource name by using a hash table.
     * @param name the name of the resource
     * @return the ID of the node which has that name
     */
    public Integer lookupId(String name)
        {
        Integer id=map.get(name);
        if(id==null) return null;
        else return id;
        }
    
    ResourceNode lookupNode(String name)
        {
        Integer id=map.get(name);
        if(id==null) return null;
        else return nodes.get(id.intValue());
        }
    
    /**
     * Puts a resource onto the lookup table 
     * @param name the name of the new resource 
     * @return the ID of the new (or pre-existing) resource
     */
    public Integer put(String name)
        {
        
        Integer id=map.get(name);
        ResourceNode node=null;
        if (id==null) // doesn't exist, so add
            {
            id=this.nextId();        
            node=new ResourceNode(id);
            }
        else
            {
            node=this.nodes.get(id);
            }       
        
        this.addName(id,name);
        this.addNode(id,node);
        this.map.put(name,id);
        this.addSlotActiveState(id,true);
        this.addSlotUsableState(id);
        this.addSlotReferencedState(id);       
        
        return id;                
        } 

    /**
     * Gets the next available ID in the graph
     * @return that ID
     */
    public Integer nextId()
        {
        Integer nid;
        // since old ids need to be there to check for deleted links, add to end
        nid=this.slotsActive.size();
        return nid;
        }

    /**
     * Adds a name to the dynamic name array.
     * @param id the target ID of the name slot.
     * @param name the resource name string to add
     */
    public void addName(Integer id, String name)
        {
        while(id>=this.names.size())
            this.names.add(null);
        this.names.set(id,name);        
        }

    /**
     * Add a node to the table's array of nodes.
     * @param id the target ID of the node slot.
     * @param node the resource node to add
     */
    public void addNode(Integer id, ResourceNode node)
        {
        while(id>=this.nodes.size())
            this.nodes.add(null);
        this.nodes.set(id,node);        
        }

    /**
     * Adds an entry to the active state array for this table.
     * @param id target ID of the active state slot
     * @param as the active state to add
     */
    public void addSlotActiveState(Integer id,boolean as)
        {
        while(id>=this.slotsActive.size())
            this.slotsActive.add(null);
        this.slotsActive.set(id,as);
        }

    /**
     * Adds an "unchecked" state entry to the usable state array for this table. 
     * @param id The target ID of the usable state slot
     */
    public void addSlotUsableState(Integer id)
        {
        while(id>=this.slotsUsable.size())
            this.slotsUsable.add(null);
        this.slotsUsable.set(id,UsabilityState.unchecked);
        }
    
    /**
     * Adds an "unchecked" state entry to the referenced state array for this table.
     * @param id The target ID of the referenced state slot
     */
    public void addSlotReferencedState(Integer id)
        {
        while(id>=this.slotsReferenced.size())
            this.slotsReferenced.add(null);
        this.slotsReferenced.set(id,ReferenceState.unchecked);
        }

    /**
     * counts the number of links to a node
     * @param id the ID of the node
     * @return the amount of references counted
     */
    private int countlinks(Integer id)
        {
        int c=0;
        for(ResourceNode node: nodes)
            {
            for(Integer did: node.depenIds)                                    
                {
                if(did==id)
                    ++c;
                }
            }            
        return c;
        }
    
    /**
     * Shift-left indexes in table above a certain index value by one
     * @param id the index value
     */
    private void shiftRefsLeft(int id)
        {        
        int ni=0;
        for(ResourceNode node: nodes)
            {
            if(node.id>id)
                --node.id;            
            ArrayList<Integer> rrl=new ArrayList<>();
            for(int i=0;i<node.depenIds.size();i++)                
                {                
                Integer did=node.depenIds.get(i);
                if(node.depenIds.get(i)==id)
                    rrl.add(did);
                }
            for(Integer i: rrl)
                node.removeDependency(i);
            for(int i=0;i<node.depenIds.size();i++)                
                {                
                if(node.depenIds.get(i)>id)
                    node.depenIds.set(i,node.depenIds.get(i)-1);
                node.depenIds.set(i,node.depenIds.get(i)-1);
                node.depenIds.set(i,node.depenIds.get(i)+1);
                }
            ++ni;
            }            
        }
    
    /**
     * Shift the ID values in the map above a certain index value by one
     * @param id the index value
     */
    private void shiftMappedIdsLeft(int id)
        {
        for(Integer cid=id+1;cid<names.size();cid++)        
            {
            map.put(names.get(cid),cid-1);
            }
        }

    /**
     * fully deletes node from lookup table and graph
     * @param id the ID of the node to delete
     */
    private void fulldelete(int id)
        {
        if(id==0 && this.size()==1)
            clear();
        else
            {
            // get id'th name and remove that name from hash map index               
            map.remove(names.get((int)id),id);
            shiftMappedIdsLeft(id);
            // fix reference indices
            shiftRefsLeft(id);
            // remove id'th node from node list
            nodes.remove((int)id);
            // remove id'th slot from active slot list
            slotsActive.remove((int)id);
            // remove id'th name from name list
            names.remove((int)id);
            // remove id'th slot from usable slot list
            slotsUsable.remove((int)id);        
            // remove id'th slot from referenced slots list
            slotsReferenced.remove((int)id);        
            }
        }
    
    /**
     * Deactivates a resource from the graph without fully deleting it.
     * @param rname the name of the resource to remove from active duty.
     */
    public void remove(String rname)
        {
        Integer id=this.lookupId(rname);
        if(id!=null)
            {
            if(countlinks(id)==0)
                {
                // delete node fully
                fulldelete(id);
                }
            else
                {
                //don't delete name, but do set active flag to false
                this.slotsActive.set(id,false);
                //leave the node as is as well.
                }
            }
        }

    /**
     * Restore a resource to the graph.
     * @param rname the name of the resource to restore to active duty.
     */
    public void restore(String rname)
        {
        Integer id=this.lookupId(rname);
        if(id!=null)
            {
            //don't restore name, but do set active flag to true
            this.slotsActive.set(id,true);
            //leave the node as is as well.
            }
        }
    
    /**
     * Gets the number of lookup entries in this table.
     * @return the number of entries
     */
    public int size()
        {
        int sz=this.names.size();
        sz=Math.min(sz,this.nodes.size());
        sz=Math.min(sz,this.slotsActive.size());
        sz=Math.min(sz,this.slotsUsable.size());
        return sz;
        }

    /**
     * Sets all usable states in the table back to "unchecked".
     */
    public void resetUsableStates()
        {
        for(int id=0;id<this.slotsUsable.size();id++)
            {
            this.slotsUsable.set(id,UsabilityState.unchecked);
            }
        }

    /**
     * Sets all referenced states in the table back to "unchecked".
     */
    public void resetReferencedStates()
        {
        for(int id=0;id<this.slotsReferenced.size();id++)
            {
            this.slotsReferenced.set(id,ReferenceState.unchecked);
            }
        }

    /**
     * Clears the graph of all entries; reduces size to zero.
     */
    public void clear()
        {
        map.clear();
        nodes.clear();
        this.slotsActive.clear();
        this.names.clear();
        this.slotsReferenced.clear();
        }

    }
