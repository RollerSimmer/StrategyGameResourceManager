/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcemanager;

import java.util.Objects;

/**
 * Directed graph which holds resource nodes and their dependencies by ID
 * @author John Anderson
 */
public class ResourceGraph
    {
    ResourceLookupTable lookup;
    ResourceFileLoader loader;
    ResourceFileSaver saver;
    int cid=0;

    /**
     * ResourceGraph's constructor
     */
    public ResourceGraph()
        {
        lookup = new ResourceLookupTable();
        loader = new ResourceFileLoader(this);
        saver = new ResourceFileSaver(this);
        }
    
    /**
     * Load a data definition file for this graph
     * @param fname the name of the file
     */
    public void loadDefFile(String fname)
        {
        loader.load(fname);
        cid=0;  
        resetUsableStates();
        resetReferencedStates();
        }    

    /**
     * Save this graph to a data definition file
     * @param fname the name of the file
     */
    public void saveDefFile(String fname)
        {        
        saver.save(fname);            
        }    
    
    /**
     * Add a resource link to the graph from a resource to a dependency
     * @param rsc The name of the resource.  It doesn't have to exist yet.
     * @param depen The name of the resource that needs to exist in order for this resource to be usable.
     */
    public void addResourceLink(String rsc, String depen)
        {
        ResourceNode node;
        
        Integer rid,did;
        
        rid=lookup.lookupId(rsc);
        if(rid==null)
            rid=this.lookup.put(rsc);            
        
        did=lookup.lookupId(depen);
        if(did==null)
            did=this.lookup.put(depen);            
        
        node=lookup.nodes.get(rid);
        if(node!=null)
            node.addDependency(did);
        }

    /**
     * Sever the link between two resources
     * @param rsc The name of the resource
     * @param depen The name of the former dependency
     */
    public void unlinkDependency(String rsc, String depen)
        {
        ResourceNode node;
        
        Integer rid,did;
        
        rid=lookup.lookupId(rsc);
        if(rid==null)
            return;
        
        did=lookup.lookupId(depen);
        if(did==null)
            return;
        
        node=lookup.nodes.get(rid);
        if(node!=null)
            node.removeDependency(did);
        }
    
    /**
     * Delete a resource
     * @param rsc the name of the active resource
     */
    public void deleteResource(String rsc)
        {
        this.lookup.remove(rsc);
        this.resetUsableStates();
        this.resetReferencedStates();
        }

    /**
     * Restore a previously deleted resource to the graph
     * @param rname the name of the inactive resource
     */
    public void restoreResource(String rname)
        {
        this.lookup.restore(rname);
        this.resetUsableStates();
        this.resetReferencedStates();
        }
    
    /**
     * Answers this question: Is this node usable?  Usable nodes are active and have all dependencies met.
     * @param id the nodes identifier number
     * @return true if usable
     */
    public boolean isUsable(Integer id)
        {
        boolean iu=false;
        System.out.printf("isUsable(%d):\n",id);
        System.out.printf("\tlookup.size()=%d\n",lookup.size());        
        
        if(id<lookup.size()) 
            {
            System.out.println("\tid in range");
            System.out.print("\t");
            System.out.println(lookup.slotsUsable.get(id));

            if(lookup.slotsUsable.get(id)==UsabilityState.unchecked)
                {
                System.out.println("\tunchecked");
                if(isActive(id)) 
                    {
                    System.out.println("\tactive id");
                    ResourceNode node=lookup.nodes.get(id);
                    iu=true;
                    for(Integer did: node.depenIds)
                        {
                        System.out.println("\tdependency exists");
                        if(!isUsable(did))
                            {
                            iu=false;
                            break;
                            }
                        }        
                    }
                else
                    {
                    System.out.println("\tinactive id");
                    }
                }
            else // checked - just report the previously discovered usability state
                {
                System.out.println("\talready checked");
                UsabilityState us=lookup.slotsUsable.get(id);
                if(us==UsabilityState.usable) 
                    iu=true;
                else if(us==UsabilityState.unusable)                    
                    iu=false;                  
                }
            //update usability state
            lookup.slotsUsable.set(id,iu? UsabilityState.usable: UsabilityState.unusable);            
            }
        else
            {
            System.out.println("\tid out of range");
            }

        
        return iu;
        }

    /**
     * Answers this question: Is this node active?  Deleted nodes are kept in the graph to maintain ID numbers for dependencies, but are made inactive.
     * @param id the ID number of the node
     * @return true if active.
     */
    public boolean isActive(Integer id)
        {
        if(id>=lookup.size()) 
            {
            //System.out.printf("isActive(%d): id is out of range.\n",id.intValue());
            return false;
            }
        if(!lookup.slotsActive.get(id))
            {
            //System.out.printf("isActive(%d): slot is not active.\n",id.intValue());
            return false;
            }
        return true;
        }

    /**
     * Sets usability states for each node to unchecked.
     */
    public void resetUsableStates()
        {
        lookup.resetUsableStates();
        }
    
    /**
     * Sets reference states for each node to unchecked.
     */
    public void resetReferencedStates()
        {
        lookup.resetReferencedStates();
        }

    /**
     * Answers the question: Is this node used at all? Used = active OR referenced.
     * @param rid the ID of the resource node.
     * @return true if used.
     */
    public boolean isUsed(Integer rid)
        {
        return isActive(rid)||isReferenced(rid);
        }

    /**
     * Answers the question: Is this node referenced by another node's dependencies?
     * @param rid the ID of the resource node.
     * @return true if referenced.
     */
    public boolean isReferenced(Integer rid)
        {
        if(rid==null) return false;
        if(rid.intValue()>=this.lookup.size()) return false;
        
        ReferenceState rs = this.lookup.slotsReferenced.get(rid);
        
        if(rs==ReferenceState.referenced)
            return true;
        else if(rs==ReferenceState.referenced)
            return false;
        else if(rs==ReferenceState.unchecked)
            {
            //check for references
            if(rawReferenceCheck(rid))
                {
                this.lookup.slotsReferenced.set(rid,ReferenceState.referenced);
                return true;
                }
            else
                {
                this.lookup.slotsReferenced.set(rid,ReferenceState.unreferenced);
                return false;
                }
            }
        else
            return false;
        }

    /**
     * Used by isReferenced() to determine existence of references without using the lookup table's stored referenced states.
     * @param rid the ID of the resource node.
     * @return true if referenced.
     */
    private boolean rawReferenceCheck(Integer rid)
        {
        for(ResourceNode node: this.lookup.nodes)
            {
            if(node!=null)
                {
                for(Integer did: node.depenIds)            
                    {        
                    if(Objects.equals(did, rid))
                        return true;
                    }
                }                    
            }        
        return false;
        }

    /**
     * Clears the graph of all entries.  
     */
    public void clear()
        {
        lookup.clear();
        }
    }
