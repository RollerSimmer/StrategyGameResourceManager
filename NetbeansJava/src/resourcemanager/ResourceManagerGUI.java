/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resourcemanager;


import com.sun.javafx.tk.Toolkit;
import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.swing.JFileChooser;

/**
 * Graphical User Interface for the Resource Manager Application
 * @author John Anderson
 */
public class ResourceManagerGUI extends Application
    {

    private class NodePoint
        {
        public int x;
        public int y;        
        NodePoint(int x,int y) { this.x=x; this.y=y; }
        NodePoint() { this.x=0; this.y=0; }
        }
    
    StackPane graphstack;
    ScrollPane graphscroll;
    Canvas graphcanvas;
    TextArea nodeLister;            
    Text nodeNameLabel;    
    TextField nodeNameText;
    Text depenNameLabel;    
    TextField depenNameText;
    Text scenewidth;
    Text sceneheight;    
    Text filenameLabel;    
    TextField filenameText;    
    
    Button swapbtn;
    Button delbtn;
    Button restorebtn;
    Button quitbtn;    
    Button linkbtn;
    Button unlinkbtn;
    Button choosebtn;
    Button savebtn;
    Button loadbtn;
    Button clearbtn;    
    
    HBox nodeEntryBox,buttonbox,filebox;
    VBox layout;
    ScrollPane root;
    Scene scene;
    
    String loadname;
    String savename;
    
    ResourceGraph graph;
    
    int maxNodeBoxWidth;
    int maxNodeBoxHeight;
    int graphPadding;

    double clmspan;
    double rowspan; 
    double top;
    double left;
    int maxAmtNodeColumns;
    double canvasWidth,canvasHeight;
    
    int amtNodeColumns;
    
    int fontsize;   
    
    double paneSpacing=5.0;
    
    /**
     * ResourceManagerGUI's constructor
     */
    public ResourceManagerGUI()
        {        
        graph=new ResourceGraph();
        loadname="data/resource.txt";
        savename="data/resource_save.txt";
        graph.loadDefFile(loadname);
        this.canvasWidth=1500;
        this.canvasHeight=canvasWidth*2;
        this.maxNodeBoxWidth = this.maxNodeBoxHeight = 60;
        this.rowspan = this.maxNodeBoxHeight*3/2;
        this.clmspan = this.maxNodeBoxWidth*3/2;       
        this.graphPadding=100;
        maxAmtNodeColumns=(int) Math.floor((canvasWidth-graphPadding*2)/clmspan);
        this.left=this.graphPadding+this.clmspan/2;
        this.top=this.graphPadding+this.rowspan/2;
        this.amtNodeColumns=0;        
        this.fontsize=15;
        }
    
    @Override
    public void start(Stage primaryStage)
        {        
        graphcanvas=new Canvas(canvasWidth,canvasHeight);                
        GraphicsContext gc=graphcanvas.getGraphicsContext2D();
        
        drawGraph(gc);
        //drawShapes(gc);        
        
        graphstack=new StackPane();
        graphstack.getChildren().add(graphcanvas);
        graphscroll=new ScrollPane(graphstack);
        graphscroll.setPrefSize(800,400);
            
        nodeLister=new TextArea();
        nodeLister.setPrefSize(800,150);
        
        updateNodeLister();
        
        nodeNameLabel = new Text("Node: ");
        nodeNameText = new TextField("");
        depenNameLabel = new Text("Dependency: ");
        depenNameText = new TextField("");
        swapbtn=new Button("Swap Fields");
        delbtn = new Button("Delete Node");
        restorebtn = new Button("Restore Node");        
        linkbtn = new Button("Add Dependency");        
        unlinkbtn = new Button("Unlink Dependency");
        quitbtn = new Button("Quit");
        scenewidth=new Text("800");
        sceneheight=new Text("600");
        choosebtn=new Button("...");
        savebtn=new Button("Save");        
        loadbtn=new Button("Load");
        clearbtn=new Button("Clear Graph");        
        this.filenameLabel=new Text("File Name:");
        this.filenameText=new TextField(savename);

        nodeEntryBox=new HBox();
        nodeEntryBox.setSpacing(paneSpacing);
        nodeEntryBox.getChildren().add(nodeNameLabel);
        nodeEntryBox.getChildren().add(nodeNameText);
        nodeEntryBox.getChildren().add(depenNameLabel);
        nodeEntryBox.getChildren().add(depenNameText);
        nodeEntryBox.getChildren().add(swapbtn);
        
        buttonbox = new HBox();
        buttonbox.setSpacing(paneSpacing);
        buttonbox.getChildren().add(delbtn);
        buttonbox.getChildren().add(restorebtn);
        buttonbox.getChildren().add(linkbtn);
        buttonbox.getChildren().add(unlinkbtn);        
        buttonbox.getChildren().add(quitbtn);
        
        filebox=new HBox();
        filebox.setSpacing(paneSpacing);
        filebox.getChildren().add(this.filenameLabel);
        filebox.getChildren().add(this.filenameText);
        filebox.getChildren().add(this.choosebtn);
        filebox.getChildren().add(this.savebtn);
        filebox.getChildren().add(this.loadbtn);
        filebox.getChildren().add(clearbtn);
        
        //buttonbox.getChildren().add(scenewidth);
        //buttonbox.getChildren().add(sceneheight);
        
        layout = new VBox();
        layout.setSpacing(paneSpacing);
        layout.setPadding(new Insets(5));
        layout.getChildren().add(graphscroll);
        layout.getChildren().add(nodeLister);
        layout.getChildren().add(nodeEntryBox);        
        layout.getChildren().add(buttonbox);
        layout.getChildren().add(filebox);
        
        root=new ScrollPane(layout);
        
        scene = new Scene(root, 815, 660);
        updateRootSize();
        
        primaryStage.setTitle("Resource Manager");
        primaryStage.setScene(scene);
        
        initHandlers();
        
        primaryStage.show();
        
        }
    
    private void updateRootSize()
        {
        scenewidth.setText(Double.toString(scene.getWidth()));
        sceneheight.setText(Double.toString(scene.getHeight()));
        
        //System.out.println("Updated width and height");
        }

    /**
     * Creates a transparent color
     * @param c the RGB base color
     * @param opacity the color's alpha/opacity value
     * @return the new color
     */
    private Color alphaColor(Color c,double opacity)
        {
        double r=c.getRed();
        double g=c.getGreen();
        double b=c.getBlue();
        return Color.color(r, g, b,opacity);
        }

    /**
     * Gets a node's coordinates in the canvas for drawing the graph
     * @param id the ID of the node
     * @return the node's coordinates
     */
    private NodePoint getNodePoint(int id)
        {
        NodePoint np=new NodePoint();
        
        int clm,row;
        int yofs;

        clm=id%amtNodeColumns;
        row=id/amtNodeColumns;
        yofs=/*clm*rowspan/amtNodeColumns*/ 0;
        np.x = (int) (left + clm*clmspan);
        np.y = (int) (top + row*rowspan + yofs);
        return np;
        }
    
    /**
     * Draws a resource node with the node's name on top of a colored rectangle.
     * @param gc the graphics context
     * @param cx center x
     * @param cy center y
     * @param nname the name of the node
     * @param fill the box fill color
     * @param stroke the box outline color
     * @param textfill the color of the name font
     */    
    private void drawNode(GraphicsContext gc,int cx,int cy, String nname,
                          Color fill, Color stroke, Color textfill)
        {
        
        Font savefont;
        Paint savefill,savestroke;
        double savelw;
        
        savelw=gc.getLineWidth();
        savefill=gc.getFill();
        savestroke=gc.getStroke();
        savefont=gc.getFont();
        
        gc.setLineWidth(1);
        gc.setStroke(stroke);
        
        gc.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, fontsize));  // could have font args, but use default for now
        
        float textwidth = Toolkit.getToolkit().getFontLoader().computeStringWidth(nname, gc.getFont());
        textwidth=Math.min(textwidth,maxNodeBoxWidth);
        float textheight = Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();
        double textPadding=3.0;
        double boxwidth=textwidth+textPadding;
        double boxheight=textheight+textPadding;

        gc.setFill(fill);        
        gc.fillRect(cx-boxwidth/2,cy-boxheight/2,boxwidth,boxheight);
        gc.strokeRect(cx-boxwidth/2,cy-boxheight/2,boxwidth,boxheight);

        gc.setFill(textfill);        
        gc.fillText(nname, cx-textwidth/2, cy+textheight/2-6, textwidth);

        //gc.setFill(Color.VIOLET);
        //int dotsize=10;
        //gc.fillOval(cx-dotsize/2, cy-dotsize/2, dotsize, dotsize);

        gc.setFont(savefont);
        gc.setFill(savefill);        
        gc.setStroke(savestroke);
        gc.setLineWidth(savelw);

        }
    
    /**
     * Draw a triangular link from a node to one of its dependencies.
     * @param gc The graphics context of the canvas
     * @param nid the node ID
     * @param did the dependency node's ID
     * @param fill the fill color of the triangular link
     * @param stroke the outline color of the link triangle
     */
    private void drawGraphLink(GraphicsContext gc,Integer nid, Integer did,Color fill,Color stroke)
        {
        Color alphafill=alphaColor(fill,0.5);
        
        NodePoint np,dp;
        
        np=this.getNodePoint(nid);
        dp=this.getNodePoint(did);
        
        double savelw=gc.getLineWidth();
        Paint savestroke=gc.getStroke();
        Paint savefill=gc.getFill();
        
        gc.setLineWidth(1);
        gc.setFill(alphafill);
        gc.setStroke(stroke);
        
        //gc.strokeLine(np.x,np.y,dp.x,dp.y);
        
        // next 3 paragraphs draws a triangle
        double deltax=dp.x-np.x,deltay=dp.y-np.y;
        double mag=Math.sqrt(deltay*deltay + deltax*deltax);
        double normx=mag>0.0? deltax/mag: 1;
        double normy=mag>0.0? deltay/mag: 0;
        double triwidth=10.0;
        double trix=normy*triwidth/2.0,triy=-normx*triwidth/2.0;
        
        double xs[]=new double[3];
        double ys[]=new double[3];        
        xs[0]=dp.x-trix;
        ys[0]=dp.y-triy;
        xs[1]=dp.x+trix;
        ys[1]=dp.y+triy;
        xs[2]=np.x;
        ys[2]=np.y;
        
        gc.fillPolygon(xs,ys,3);
        gc.strokePolygon(xs,ys,3);

        gc.setFill(savefill);
        gc.setStroke(savestroke);
        gc.setLineWidth(savelw);
        //draw a line from node point to dependency point with arrow at end
        }

    /**
     * Draw all links on the graph
     * @param gc the graphics context of the canvas
     */
    private void drawGraphLinks(GraphicsContext gc)
        {
        Integer nid;
        
        for(ResourceNode node: graph.lookup.nodes)
            {
            nid=node.id;
            for(Integer did: node.depenIds)
                {
                if(graph.isUsable(did))
                    drawGraphLink(gc,nid,did,Color.DARKCYAN,Color.DARKBLUE);
                else
                    drawGraphLink(gc,nid,did,Color.DARKGRAY,Color.BLACK);
                }
            }           
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    /**
     * Draw all graph node boxes 
     * @param gc the graphics context of the canvas 
     */
    private void drawGraphBoxes(GraphicsContext gc)
        {
        int id=0;
        NodePoint cpt;
        
        for(String nname: graph.lookup.names)
            {
            cpt=getNodePoint(id);
            
            Color fill,stroke,textfill=Color.WHITE;            
            
            if(graph.isUsable(id))
                {  fill=Color.GREEN; stroke=Color.LIGHTGREEN; }
            else if(graph.isActive(id))
                {  fill=Color.GOLD; stroke=Color.YELLOW; }
            else
                {  fill=Color.RED; stroke=Color.PINK; }
            
            drawNode(gc,cpt.x,cpt.y,nname,fill,stroke,textfill);
            
            ++id;            
            }        
        }

    /**
     * Set the amount of node columns on the graph to a value that will fit on the canvas.
     */
    private void setAmtNodeColumns()
        {
        amtNodeColumns=(int) Math.ceil(Math.sqrt(graph.lookup.size()));
        amtNodeColumns=Math.min(amtNodeColumns,maxAmtNodeColumns);
        }
    
    /**
     * Draw the graph's legend explaining box color coding, etc.
     * @param gc the graphics context of the canvas
     */
    private void drawLegend(GraphicsContext gc)
        {
        //draw a legend for the graph display explaining color coding of nodes  
        //(green=usable, yellow=unusable but active, red=inactive)
        
        Paint savefill=gc.getFill(),savestroke=gc.getStroke();
        Font savefont=gc.getFont();
        double savelw=gc.getLineWidth();
        
        Color textfill=Color.BLACK;
        
        Color boxfills[]= { Color.GREEN,Color.GOLD,Color.RED };
        Color boxstrokes[]= { Color.LIGHTGREEN,Color.YELLOW,Color.PINK };
        String boxdescs[]= { "Usable node","Unusable Node","Inactive Node" };
        String title="Legend";        
        
        gc.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, fontsize));  // could have font args, but use default for now
        float textwidth = Toolkit.getToolkit().getFontLoader().computeStringWidth(title, gc.getFont());
        textwidth=Math.min(textwidth,maxNodeBoxWidth);
        float textheight = Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();
        double spacing=5.0;
        double x,y,maxLabelWidth=75,maxTitleWidth;        
        double boxwidth=25;
        double boxheight=textheight;        
        double yinc=boxheight+spacing;
        
        //print title
        x=10; y=10+textheight; 
        maxTitleWidth=maxLabelWidth+boxwidth+spacing;
        gc.setFill(textfill);        
        gc.fillText(title,x,y,maxTitleWidth);                
        
        //draw color boxes
        x=10; y=10+yinc;      
        gc.setLineWidth(1);
        for(int i=0;i<3;i++)
            {
            gc.setFill(boxfills[i]);        
            gc.setStroke(boxstrokes[i]);        
            gc.fillRect(x,y,boxwidth,boxheight);
            gc.strokeRect(x,y,boxwidth,boxheight);
            y += yinc;
            }
        
        //print labels    
        gc.setFill(textfill);        
        x=10+boxwidth+spacing; y=10+yinc+textheight;
        for(int i=0;i<3;i++)
            {
            gc.fillText(boxdescs[i],x,y,maxLabelWidth);
            y += yinc;
            }
        
        gc.setLineWidth(savelw);
        gc.setFont(savefont);
        gc.setFill(savefill);
        gc.setStroke(savestroke);        
        }
    
    /**
     * Draw the entire graph.
     * @param gc  the graphics context of the canvas
     */
    private void drawGraph(GraphicsContext gc)
        {
        //gc.clearRect(0,0,gc.getCanvas().getWidth()-1,gc.getCanvas().getHeight()-1);
        gc.setFill(Color.SNOW);        
        gc.fillRect(0,0,gc.getCanvas().getWidth()-1,gc.getCanvas().getHeight()-1);
        
        drawLegend(gc);
        
        gc.setStroke(Color.BLACK);
        

        setAmtNodeColumns();

        drawGraphLinks(gc);  // layer 1
        drawGraphBoxes(gc);  // layer 2
        
        //gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
    /**
     * Make a list of all of the node's dependency names.
     * @param id The ID of the node whose dependencies shall be listed
     * @return the list string
     */
    private String listDependencies(Integer id)
        {
        StringBuilder ld=new StringBuilder("");
        ResourceLookupTable lookup=graph.lookup;
        
        if(id<lookup.size())
            {
            ResourceNode node=lookup.nodes.get(id);
            int round=0;
            for(Integer did: node.depenIds)
                {
                if(did<lookup.size())
                    {
                    if(round>0)
                        ld.append(", ");
                    ld.append(lookup.names.get(did));
                    }
                else
                    {
                    ld.append("BAD_DEPENDENCY_ID(");
                    ld.append(Integer.toString(did));
                    ld.append(")");
                    }
                    
                round++;                                                
                }
            }
        else
            ld.append("BAD_NODE_ID");        
        
        return ld.toString();
        }

    /**
     * Update the graph's display, usually after making a change. 
     */
    private void updateGraphDisplay()
        {
        drawGraph(graphcanvas.getGraphicsContext2D());
        }

    /**
     * testing various draw routines
     * @param gc graphics context of the canvas
     */
    private void drawShapes(GraphicsContext gc) 
        {
        gc.setLineWidth(3);
        gc.strokeLine(40, 10, -40, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                       new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                         new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                          new double[]{210, 210, 240, 240}, 4);
        
        
        String msg=new String("This is text from the emergency code testing system");
        gc.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, fontsize));
        gc.setFill(Color.CYAN);

        float textwidth = Toolkit.getToolkit().getFontLoader().computeStringWidth(msg, gc.getFont());
        float textheight = Toolkit.getToolkit().getFontLoader().getFontMetrics(gc.getFont()).getLineHeight();
        
        gc.fillRect(300,25-textheight,Math.min(textwidth,300),textheight);
        gc.strokeRect(300,25-textheight,Math.min(textwidth,300),textheight);
        
        gc.setFill(Color.BLACK);
        gc.fillText(msg, 300, 25-5, 300);
        
        }    

    /** 
     * @param args the command line arguments
     */
    public static void main(String[] args)
        {
        launch(args);
        }

    /**
     * Initializes handlers for GUI elements.
     */
    private void initHandlers()
        {
        root.setOnMouseMoved(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event)
                {
                updateRootSize();
                }
            });

        swapbtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    swapNameFields();
                    }

                }
            );

        delbtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    deleteResource();
                    }
                }
            );

        
        restorebtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    restoreResource();
                    }
                }
            );

        linkbtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    addResourceLink();
                    }
                }
            );

        unlinkbtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    unlinkDependency();
                    }
                }
            );

        quitbtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    quitProgram();
                    }
                }
            );
        
        choosebtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    ChooseFile();
                    }
                }
            );
        
        loadbtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    loadDefFile();
                    }
                }
            );

        savebtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    saveDefFile();
                    }
                }
            );
        clearbtn.setOnAction(new EventHandler<ActionEvent>()
                {
                @Override
                public void handle(ActionEvent event)
                    {
                    clearGraph();
                    }
                }
            );

        }
    
    /**
     * Handler for choose file button; opens a dialog box to choose a file and sets text field to result.
     */
    private void ChooseFile()
        {
        StringBuilder chosenfile=new StringBuilder("");
        
        File f = new File("");        
        
        JFileChooser fc=new JFileChooser();
        
        fc.setSelectedFile(f);
        fc.setDialogTitle("Choose a file");
        fc.setDialogType(JFileChooser.CUSTOM_DIALOG);
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);        
        fc.showDialog(null,"Replace File String");
        
        chosenfile.append(fc.getSelectedFile().getAbsolutePath());
        
        System.out.printf("Choose file dialog:\n  %s is the chosen file.\n", chosenfile.toString());
        
        this.filenameText.setText(chosenfile.toString());
        }
    
    /**
     * Clears the entire graph.
     */
    private void clearGraph()
        {
        graph.clear();
        
        updateNodeLister();
        updateGraphDisplay();
        }
    
    /**
     * Loads a resource definition text file.
     */
    private void loadDefFile()
        {
        loadname=this.filenameText.getText();
        graph.loadDefFile(this.loadname);
        
        updateNodeLister();
        updateGraphDisplay();
        }

    /**
     * Saves the graph as a resource definition text file.
     */
    private void saveDefFile()
        {
        savename=this.filenameText.getText();
        graph.saveDefFile(this.savename);
        
        updateNodeLister();
        updateGraphDisplay();
        }
            
    /**
     * Swap text fields for resource name and dependency name in the GUI.
     */
    private void swapNameFields()
        {
        String tmp=nodeNameText.getText();
        nodeNameText.setText(depenNameText.getText());
        depenNameText.setText(tmp);        
        }
    
    /**
     * Handler for Quit button - quits the entire program.
     */
    private void quitProgram()
        {
        System.out.println("Closing program");
        
        try
            { 
            Platform.exit();
            this.stop();             
            }
        catch(Exception e)
            { System.err.print(e); }
        
        }

    /**
     * Handler Delete Resource button - deletes a resource.
     */
    private void deleteResource()
        {
        String rname=nodeNameText.getText();
        //delete the named resource from graph
        graph.deleteResource(rname);
        
        updateNodeLister();
        updateGraphDisplay();
        
        System.out.printf("Node %s was deleted.\n",rname);
        }    

    /**
     * Handler; Restore the currently specified resource in the resource field.
     */
    private void restoreResource()
        {
        String rname=nodeNameText.getText();
        //restore the named resource to the graph
        graph.restoreResource(rname);
        
        updateNodeLister();
        updateGraphDisplay();
        
        System.out.printf("Node %s was restored.\n",rname);
        }
    
    /**
     * Unlink handler; removes a dependency from a node.
     */
    private void unlinkDependency()
        {
        String rname,dname;
        
        rname=nodeNameText.getText();
        dname=depenNameText.getText();
        
        graph.unlinkDependency(rname,dname);

        updateNodeLister();
        updateGraphDisplay();
        
        System.out.printf("Node %s no longer depends on the existence of %s.\n",rname,dname);
        }
    
    /**
     * Add resource link handler; adds resource link to graph.
     */
    private void addResourceLink()
        {
        String rname,dname;
        
        rname=nodeNameText.getText();
        dname=depenNameText.getText();
        
        graph.addResourceLink(rname,dname);

        updateNodeLister();
        updateGraphDisplay();
        
        System.out.printf("Node %s now depends on the existence of %s.\n",rname,dname);
        }
    
    /**
     * Update the node listing panel. 
     */
    private void updateNodeLister()
        {
        ResourceLookupTable lookup=graph.lookup;
        StringBuilder nl;
        nl = new StringBuilder("");
        //build a node list
        System.out.printf("lookup.size()=%d\n",lookup.size());
        for(int id=0;id<lookup.size();id++)
            {
            String nodename,nodeActivity,nodeUsability;
            
            nodename=lookup.names.get(id);
            if(nodename==null) nodename="(null_node_string)";
            
            if(graph.isUsable(id))
                nodeUsability="Usable";
            else
                nodeUsability="Not Usable";
            
            if(graph.isActive(id))
                nodeActivity="Active";
            else
                nodeActivity="Not Active";
            
            String depenStr=listDependencies(id);
            
            int mappedId=lookup.map.get(nodename);
            
            String newline=String.format("id=%d: %s (maps to %d,%s, %s, Dependencies: (%s))\n",
                                         id,nodename,mappedId,nodeActivity,nodeUsability,depenStr);
            System.out.print(newline);            
            nl.append(newline);
            }
        //replace nodelister's list
        System.out.println("nl=");            
        System.out.print(nl);            
        nodeLister.setText(nl.toString());
        System.out.println("Node lister's text was updated.");
        }

    }
