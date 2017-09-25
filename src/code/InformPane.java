package code;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 *
 * @author Ronith
 */
public class InformPane{
    public class mh{
        int mh_id;
        Circle mh_node;
        mss req_loc;
    }
    public class mss{
        int mss_id;
        Circle mss_node;
        ArrayList<request> req_queue;
        int request_pointer;
    }
    public class request{
        int node_id;
        mss node_mss;
    }
    ArrayList<mh> mh_db = new ArrayList<>();
    ArrayList<mss> mss_db = new ArrayList<>();
    Pane pn = new Pane();
    int current_mss=-1,token_sender_flag,hop=0,initial_quad,quadrant_moved,count=0;
    Line vl,hl;
    double x_old=0,x_new=0,y_old=0,y_new=0;
    request hop_request;
    VBox label_box = new VBox();
    Label join_label = new Label();
    Label inform_label = new Label();
     
    public InformPane(){
        
        label_box.getChildren().addAll(join_label,inform_label);
        label_box.setSpacing(15);
        //set boundaries for mss
        vl = new Line(350,50,350,400);
        hl = new Line(50,225,650,225);
        pn.getChildren().add(vl);
        pn.getChildren().add(hl);
        //add MSS
        for(int i=0;i<4;i++){
            mss new_mss = new mss();
            new_mss.mss_node = new Circle(10,Color.BLACK);
            new_mss.mss_id = i;
            new_mss.request_pointer =0;
            new_mss.req_queue = new ArrayList<>();
            mss_db.add(new_mss);
        }
        //set centers for MSS
        mss_db.get(0).mss_node.setCenterX(200); mss_db.get(1).mss_node.setCenterX(500);  mss_db.get(2).mss_node.setCenterX(200); mss_db.get(3).mss_node.setCenterX(500);
        mss_db.get(0).mss_node.setCenterY(120); mss_db.get(1).mss_node.setCenterY(120); mss_db.get(2).mss_node.setCenterY(329); mss_db.get(3).mss_node.setCenterY(329);   
        for(int i=0;i<4;i++){
            pn.getChildren().add(mss_db.get(i).mss_node);
        }
        //add MH
        for(int i=0;i<24;i++){
            mh new_mh = new mh();
            new_mh.mh_id=i;
            new_mh.mh_node = new Circle(10,Color.BLUE);
            new_mh.req_loc =null;
            mh_db.add(new_mh);
        } 
        //set centers for MH and draw
        double slice = 2 * Math.PI / 6;
        int baseX =0;
        int baseY =0;
        int radius =65;
        for(int i=0;i<24;i++)
            {   
                if(i<6)
                {
                    double angle = slice * i;
                    baseX=200; baseY=120;
                    int x = (int)(radius*cos(angle) + baseX);
                    int y = (int)(radius*sin(angle) + baseY);
                    mh_db.get(i).mh_node.setCenterX(x);
                    mh_db.get(i).mh_node.setCenterY(y);
                }
                else if(i<12)
                {
                    baseX = 500; baseY = 120;
                    double angle = slice * (i-6);
                    int x = (int)(radius*cos(angle) + baseX);
                    int y = (int)(radius*sin(angle) + baseY);
                    mh_db.get(i).mh_node.setCenterX(x);
                    mh_db.get(i).mh_node.setCenterY(y);
                }
                else if(i<18)
                {
                    baseX = 200; baseY = 329;
                    double angle = slice * (i-12);
                    int x = (int)(radius*cos(angle) + baseX);
                    int y = (int)(radius*sin(angle) + baseY);
                    mh_db.get(i).mh_node.setCenterX(x);
                    mh_db.get(i).mh_node.setCenterY(y);
                }
                else
                {
                    baseX=500; baseY = 329;
                    double angle = slice * (i-18);
                    int x = (int)(radius*cos(angle) + baseX);
                    int y = (int)(radius*sin(angle) + baseY);
                    mh_db.get(i).mh_node.setCenterX(x);
                    mh_db.get(i).mh_node.setCenterY(y);
                }           
                pn.getChildren().add(mh_db.get(i).mh_node);
            }  
        
       for(mh myNode : mh_db){
            EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event)
                {
                    if(event.getClickCount()==2){
                        if(myNode.mh_node.getCenterX()<350 && myNode.mh_node.getCenterY()<250){
                            request new_req = new request();
                            new_req.node_id = myNode.mh_id;    
                            if(myNode.mh_node.getFill().equals(Color.BLUE)){
                                myNode.mh_node.setFill(Color.LIGHTGRAY);
                                myNode.req_loc = new_req.node_mss = mss_db.get(0);
                                mss_db.get(0).req_queue.add(new_req);
                            }
                        }
                        else if(myNode.mh_node.getCenterX()>350 && myNode.mh_node.getCenterY()<250){
                            request new_req = new request();
                            new_req.node_id = myNode.mh_id;         
                            if(myNode.mh_node.getFill().equals(Color.BLUE)){
                                myNode.mh_node.setFill(Color.LIGHTGREEN);
                                myNode.req_loc = new_req.node_mss = mss_db.get(1);
                                mss_db.get(1).req_queue.add(new_req);
                            }
                        }
                        else if(myNode.mh_node.getCenterX()<350 && myNode.mh_node.getCenterY()>250){
                            request new_req = new request();
                            new_req.node_id = myNode.mh_id;     
                            if(myNode.mh_node.getFill().equals(Color.BLUE)){
                                myNode.mh_node.setFill(Color.LIGHTPINK);
                                myNode.req_loc = new_req.node_mss = mss_db.get(2);
                                mss_db.get(2).req_queue.add(new_req);
                            }
                        }
                        else if(myNode.mh_node.getCenterX()>350 && myNode.mh_node.getCenterY()>250){
                            request new_req = new request();
                            new_req.node_id = myNode.mh_id;  
                            if(myNode.mh_node.getFill().equals(Color.BLUE)){
                                myNode.mh_node.setFill(Color.LIGHTCYAN);
                                myNode.req_loc = new_req.node_mss = mss_db.get(3);
                                mss_db.get(3).req_queue.add(new_req);
                            }
                        }
                    }
                }       
            };
            myNode.mh_node.setOnMouseClicked(onMouseClickedEventHandler);             
        }
       
    Timeline tl = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            //check if mss has token
            if(mss_has_token()){
                //check which mss is yellow
                for(int i=0;i<4;i++){
                    if(mss_db.get(i).mss_node.getFill()== Color.YELLOW){;
                        current_mss=i;
                        break;
                    }
                }//if mss req queue is empty, pass token to next mss
                if(mss_db.get(current_mss).req_queue.isEmpty() && hop!=1)
                    change_mss();
                else if(hop==1){
                    mss_db.get(current_mss).mss_node.setFill(Color.BLACK);
                    mh_db.get(hop_request.node_id).mh_node.setFill(Color.YELLOW);
                }
                else if(mss_db.get(current_mss).request_pointer< mss_db.get(current_mss).req_queue.size()){
                    request service_request;
                    service_request = mss_db.get(current_mss).req_queue.get(mss_db.get(current_mss).request_pointer);
                    //check if the mh which submitted the request is in the same mss
                    if(service_request.node_mss.mss_id == mss_db.get(current_mss).mss_id){
                        //service the request
                        mss_db.get(current_mss).mss_node.setFill(Color.BLACK);
                        mh_db.get(service_request.node_id).mh_node.setFill(Color.YELLOW);
                        mss_db.get(current_mss).request_pointer++;
                        token_sender_flag = current_mss;
                    }
                    else{ //deliver token to the mss under which mh is present
                        mss_db.get(current_mss).mss_node.setFill(Color.BLACK);
                        mss_db.get(service_request.node_mss.mss_id).mss_node.setFill(Color.YELLOW);
                        hop =1;
                        hop_request = service_request;
                        mss_db.get(current_mss).request_pointer++;
                        token_sender_flag = current_mss;
                    }
                }
                else
                    change_mss();
            }
            else if(mh_has_token()){
                int i;
                //which mh has token
                for(i=0;i<24;i++){
                    if(mh_db.get(i).mh_node.getFill().equals(Color.YELLOW))
                        break;
                }
                //check if it is a normal request or hop request
                if(hop==1){ // return the token to the original mss
                    mh_db.get(hop_request.node_id).mh_node.setFill(Color.BLUE);
                    mss_db.get(token_sender_flag).mss_node.setFill(Color.YELLOW);
                    mh_db.get(hop_request.node_id).req_loc = null;
                    hop=0;
                    hop_request = null;
                }
                else{ //return the token to the appropriate node
                    mh_db.get(i).mh_node.setFill(Color.BLUE);
                    mss_db.get(token_sender_flag).mss_node.setFill(Color.YELLOW);
                    mh_db.get(i).req_loc = null;
                }
                    
            }
            else
                change_mss();
        }
    }));
    tl.setCycleCount(Timeline.INDEFINITE);
    tl.play();       
           
      
    for(int i=0;i<24;i++)
        makeDraggable(mh_db.get(i).mh_node);
       //check if an MH has moved from one MSS to other
//         for(Circle myNode : mh){
//          eh=(EventHandler<MouseEvent>) myNode.onMouseDragReleasedProperty()
//        //  eh = new EventHandler<MouseEvent>()
//            {
//             @Override
//            public void handle(MouseEvent event){
//                x_new = myNode.getCenterX();
//                y_new = myNode.getCenterY();
//            }
//             };
//      }
         
    }
    
    public boolean mss_has_token(){int j;
        for(j=0;j<4;j++){
           if( mss_db.get(j).mss_node.getFill()== Color.YELLOW)
               break;  
        }
        if(j<4)
            return true;
        else
            return false;
    }
    
    public boolean mh_has_token(){int j;
        for(j=0;j<24;j++){
            if(mh_db.get(j).mh_node.getFill()==Color.YELLOW)
                break;
        }
        if(j<24)
            return true;
        else
            return false;
    }
    
    public void change_mss(){
         if(current_mss<mss_db.size()-1){
            if(current_mss!=-1){
                mss_db.get(current_mss+1).mss_node.setFill(Color.YELLOW);
                mss_db.get(current_mss).mss_node.setFill(Color.BLACK);
                current_mss++;
            }
            else{
                mss_db.get(current_mss+1).mss_node.setFill(Color.YELLOW);
                mss_db.get(3).mss_node.setFill(Color.BLACK);
                current_mss++;
            }
                
        }
       else{
            current_mss =-1;
            mss_db.get(0).mss_node.setFill(Color.YELLOW);
            mss_db.get(3).mss_node.setFill(Color.BLACK);       
        }
                 
    }
    
    public class send_join_and_inform_msg extends Thread{   
        mh mh_node_local;
        public send_join_and_inform_msg(mh mh_node){
            mh_node_local = mh_node;
        }
        
        public void run(){
            if(mh_node_local.req_loc!=null){
                Platform.runLater(() -> join_label.setText("join(" + mh_node_local.mh_id +","+ (quadrant_moved-1)+") to MSS" + (quadrant_moved-1)));
                Platform.runLater(() -> inform_label.setText("inform(" + mh_node_local.mh_id + "," + (quadrant_moved-1) +") to MSS" + (initial_quad-1)));   
                try{
                    sleep(3000);
                    Platform.runLater(() -> join_label.setText(null));
                    Platform.runLater(() -> inform_label.setText(null));
                }
                catch(Exception e ){}
            }
            
        }
    }
    
    public void mh_moved(){
        if(x_old!=0 && y_old!=0 && x_new!=0 && y_new!=0){
             if(x_new < 350 && y_new < 225)
                 quadrant_moved = 1;
             else if(x_new > 350 && y_new < 225)
                 quadrant_moved = 2;
             else if(x_new < 350 && y_new > 225 )
                 quadrant_moved = 3;
             else if(x_new > 350 && y_new > 225 )
                 quadrant_moved = 4;    
        } 
        
    }
     
    public void mh_hop(mh mh_node){
         if(quadrant_moved!=0){
        for(int i=0;i<mss_db.get(initial_quad-1).req_queue.size();i++){
            if(mss_db.get(initial_quad-1).req_queue.get(i).node_id == mh_node.mh_id && mss_db.get(initial_quad-1).req_queue.get(i).node_mss.mss_id == mh_node.req_loc.mss_id){
                mss_db.get(initial_quad-1).req_queue.get(i).node_mss = mss_db.get(quadrant_moved-1);
                break;
            } 
        }       
         }
    }
    
    public void makeDraggable(Circle circle){
    circle.setOnMousePressed(onMousePressedEventHandler);
    circle.setOnMouseDragged(onMouseDraggedEventHandler);
    circle.setOnMouseReleased(onMouseReleasedEventHandler);
  }
     
    EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>(){
     
   @Override
   public void handle(MouseEvent event){
       if(event.getClickCount()!=2){
                Circle circle = (Circle) event.getSource();
                x_old = circle.getCenterX();
                y_old = circle.getCenterY();
                if(x_old <350 && y_old <225)
                    initial_quad = 1;
                else if(x_old >350 && y_old <225)
                    initial_quad =2;
                else if(x_old <350 && y_old >225)
                    initial_quad =3;
                else if(x_old>350 && y_old >225)
                    initial_quad =4;
            }
   }
 };
   
    EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>()
    {
    @Override
    public void handle(MouseEvent event)
    {
        Circle circle = ((Circle) (event.getSource()));    
        circle.setCenterX(event.getSceneX());
        circle.setCenterY(event.getSceneY());  
        if(initial_quad == 1 && (event.getSceneX() >350 || event.getSceneY()>225) && count!=1){
            x_new = event.getSceneX();
            y_new = event.getSceneY();
            count=1;
            mh_moved();
            for(int i=0;i<24;i++){
                if(mh_db.get(i).mh_node.equals(circle)){
                    mh_hop(mh_db.get(i));
                    new send_join_and_inform_msg(mh_db.get(i)).start();
                    break;
                }
            }
        }
        else if(initial_quad ==2 && (event.getSceneX()<350 || event.getSceneY()>225) && count!=1 ){
            x_new = circle.getCenterX();
            y_new = circle.getCenterY();
            count=1;
            mh_moved();
            for(int i=0;i<24;i++){
                if(mh_db.get(i).mh_node.equals(circle)){
                    mh_hop(mh_db.get(i));
                    new send_join_and_inform_msg(mh_db.get(i)).start();
                    break;
                }
            } 
        }
        else if(initial_quad ==3 && (event.getSceneX()>350 || event.getSceneY()<225) && count!=1){
            x_new = circle.getCenterX();
            y_new = circle.getCenterY();
            count=1;
            mh_moved();
            for(int i=0;i<24;i++){
                if(mh_db.get(i).mh_node.equals(circle)){
                    mh_hop(mh_db.get(i));
                    new send_join_and_inform_msg(mh_db.get(i)).start();
                    break;
                }
            }
        }
        else if(initial_quad ==4 && count!=1 && (event.getSceneX()<350 || event.getSceneY() <225)){
            x_new = circle.getCenterX();
            y_new = circle.getCenterY();
            count=1;
            mh_moved();
            for(int i=0;i<24;i++){
                if(mh_db.get(i).mh_node.equals(circle)){
                    mh_hop(mh_db.get(i));
                    new send_join_and_inform_msg(mh_db.get(i)).start();
                    break;
                }
            }
        }    
    }
    };
     
    EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent event){
            count =0;
            initial_quad = quadrant_moved =0;
        }
    };
    
}