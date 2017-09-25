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
public class ProxyPane {
    
    public class mh{
        Circle mh_node;
        int mh_id;
        proxy init_proxy;
    }
    
    public class request{
        int node_id;
        proxy node_proxy;
    }
    
    public class proxy{
        Circle proxy_node;
        int proxy_id;
        ArrayList<request> request_queue ;
    }
    ArrayList<Circle> mss_db = new ArrayList<>();
    ArrayList<mh> mh_db = new ArrayList<>();
    ArrayList<proxy> proxy_db = new ArrayList<>();
    Line vl;
    int request_queue_pointer_1 = 0, request_queue_pointer_2 = 0, hop=0, token_sender_flag, moveover, entered =0; 
    Pane pn = new Pane();
    request hop_request = null;
    double  x_old =0, y_old =0;
    Label left_msg= new Label();
    Label right_msg = new Label();
    VBox label_box = new VBox();
    
    public ProxyPane(){
        //set inform and join labels
        label_box.getChildren().addAll(right_msg,left_msg);
        label_box.setSpacing(20);
        //set boundary for proxies
        vl = new Line(350,50,350,400);
        pn.getChildren().add(vl); 
        //initialize MHs
        for(int i=0; i<24; i++){
            mh new_mh = new mh();
            new_mh.mh_node = new Circle(10,Color.BLUE);
            new_mh.mh_id = i;
            mh_db.add(new_mh);
        }
        //initialize proxies
        for(int i=0;i<2;i++){
            proxy new_proxy = new proxy();
            new_proxy.proxy_node = new Circle(15,Color.RED);
            new_proxy.proxy_id = i;
            new_proxy.request_queue = new ArrayList<request>();
            proxy_db.add(new_proxy);
        }      
        //Initialize mss, mss dont play big role in proxy strategy so dont need a separate class for them
        for(int i=0;i<4;i++){
            mss_db.add(new Circle(10,Color.BLACK));
        }
        //set centers for MSS nodes and add them to the pane
        mss_db.get(0).setCenterX(200); mss_db.get(1).setCenterX(500);  mss_db.get(2).setCenterX(200); mss_db.get(3).setCenterX(500);
        mss_db.get(0).setCenterY(150); mss_db.get(1).setCenterY(150); mss_db.get(2).setCenterY(329); mss_db.get(3).setCenterY(329); 
        for(int i=0;i<4;i++){
            pn.getChildren().add(mss_db.get(i));
        }
        //set centers for proxys nodes and add them to the pane 
        proxy_db.get(0).proxy_node.setCenterX(200); proxy_db.get(1).proxy_node.setCenterX(500); 
        proxy_db.get(0).proxy_node.setCenterY(25); proxy_db.get(1).proxy_node.setCenterY(25);
        for(int i=0;i<2;i++)
            pn.getChildren().add(proxy_db.get(i).proxy_node);
        //set centers for MH and add them to tha pane
        double slice = 2 * Math.PI / 6;
        int baseX;
        int baseY;
        int radius =65;
        for(int i=0;i<24;i++)
            {   
                if(i<6)
                {
                    double angle = slice * i;
                    baseX=200; baseY=150;
                    int x = (int)(radius*cos(angle) + baseX);
                    int y = (int)(radius*sin(angle) + baseY);
                    mh_db.get(i).mh_node.setCenterX(x);
                    mh_db.get(i).mh_node.setCenterY(y);
                }
                else if(i<12)
                {
                    baseX = 500; baseY = 150;
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
        //request processing
        mh_db.stream().forEach((mh myNode) -> {
            EventHandler<MouseEvent> onMouseClickedEventHandler = (MouseEvent event) -> {
                if(event.getClickCount()==2){
                    if(myNode.mh_node.getCenterX()<350){
                        request new_request = new request();
                        new_request.node_id = myNode.mh_id;
                        if(myNode.mh_node.getFill().equals(Color.BLUE)){
                            myNode.init_proxy = new_request.node_proxy = proxy_db.get(0);
                            myNode.mh_node.setFill(Color.LIGHTGREEN);
                            proxy_db.get(0).request_queue.add(new_request);
                        }
                    }
                    else{
                        request new_request = new request();
                        new_request.node_id = myNode.mh_id;
                        if(myNode.mh_node.getFill().equals(Color.BLUE)){
                            myNode.init_proxy = new_request.node_proxy = proxy_db.get(1);
                            proxy_db.get(1).request_queue.add(new_request);
                            myNode.mh_node.setFill(Color.LIGHTPINK);
                        }
                    }
                }
            };
            myNode.mh_node.setOnMouseClicked(onMouseClickedEventHandler);
        });
        //token circulation 
        Timeline token_pass = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionevent){
                //check if token is with proxy or mh
                if(proxy_has_token()){
                    //check which proxy has token
                    if(proxy_db.get(0).proxy_node.getFill().equals(Color.YELLOW)){
                        //check if request_queue is null 
                        if(proxy_db.get(0).request_queue.isEmpty() && hop!=1)
                            forward_next_proxy();
                        else if(hop==1){//check if it is a hop request
                            //service the request
                            proxy_db.get(0).proxy_node.setFill(Color.RED);
                            mh_db.get(hop_request.node_id).mh_node.setFill(Color.YELLOW);
                        }
                        else if(request_queue_pointer_1<proxy_db.get(0).request_queue.size()){ //if request queue is populated
                            request service_request;
                            service_request = proxy_db.get(0).request_queue.get(request_queue_pointer_1);
                            //check if mh that submitted the request is in the same proxy
                            if(service_request.node_proxy.proxy_id == proxy_db.get(0).proxy_id){
                                //deliver token to the mh
                                proxy_db.get(0).proxy_node.setFill(Color.RED);
                                mh_db.get(service_request.node_id).mh_node.setFill(Color.YELLOW);
                                request_queue_pointer_1++;
                                token_sender_flag =0;
                            }
                            else{
                                //deliver token to the other proxy and raise the hop flag
                                proxy_db.get(1).proxy_node.setFill(Color.YELLOW);
                                proxy_db.get(0).proxy_node.setFill(Color.RED);
                                hop=1;
                                hop_request = service_request;
                                request_queue_pointer_1++;
                            }                        
                        }
                        else{
                            //request_queue_pointer_1 =0;
                            forward_next_proxy();
                        }
                            
                    }
                    else{
                        //check if request_queue is null 
                        if(proxy_db.get(1).request_queue.isEmpty() && hop!=1)
                            forward_next_proxy();
                        else if(hop==1){//check if it is a hop request
                            //service the request
                            proxy_db.get(1).proxy_node.setFill(Color.RED);
                            mh_db.get(hop_request.node_id).mh_node.setFill(Color.YELLOW);
                        }
                        else if(request_queue_pointer_2 < proxy_db.get(1).request_queue.size()){ //if request queue is populated
                            request service_request;
                            service_request = proxy_db.get(1).request_queue.get(request_queue_pointer_2);
                            //check if mh that submitted the request is in the same proxy
                            if(service_request.node_proxy.proxy_id == proxy_db.get(1).proxy_id){
                                //deliver token to the mh
                                proxy_db.get(1).proxy_node.setFill(Color.RED);
                                mh_db.get(service_request.node_id).mh_node.setFill(Color.YELLOW);
                                request_queue_pointer_2++;
                                token_sender_flag =1;
                            }
                            else{
                                //deliver token to the other proxy and raise the hop flag
                                proxy_db.get(0).proxy_node.setFill(Color.YELLOW);
                                proxy_db.get(1).proxy_node.setFill(Color.RED);
                                hop=1;
                                hop_request = service_request;
                                request_queue_pointer_2++;
                            }                        
                        }
                        else{
                           // request_queue_pointer_2 =0;
                            forward_next_proxy();
                        } 
                            
                    }
                }
                else if(mh_has_token()){ //token is with mh
                    int i;
                    //check which mh has the token
                    for(i=0;i<24;i++){
                       if(mh_db.get(i).mh_node.getFill().equals(Color.YELLOW))
                           break;
                    }
                    //check if it was a normal request or hop request which has been fulfilled
                    if(hop == 1){ //return the token to the other proxy
                        mh_db.get(hop_request.node_id).mh_node.setFill(Color.BLUE);
                        mh_db.get(hop_request.node_id).init_proxy = null;
                        if(proxy_db.get(0).equals(hop_request.node_proxy))
                            proxy_db.get(1).proxy_node.setFill(Color.YELLOW);
                        else
                            proxy_db.get(0).proxy_node.setFill(Color.YELLOW);
                        hop =0;
                        hop_request = null;
                    }
                    else if(token_sender_flag ==0){
                        proxy_db.get(0).proxy_node.setFill(Color.YELLOW);
                        mh_db.get(i).mh_node.setFill(Color.BLUE);
                        mh_db.get(i).init_proxy = null;
                    }
                    else{
                        proxy_db.get(1).proxy_node.setFill(Color.YELLOW);
                        mh_db.get(i).mh_node.setFill(Color.BLUE);
                        mh_db.get(i).init_proxy = null;
                    }
                    
                }
                else
                    forward_next_proxy();
            }
        }));
        token_pass.setCycleCount(Timeline.INDEFINITE);
        token_pass.play();

        for(int i=0;i<24;i++){
            makedraggable(mh_db.get(i).mh_node);
        }
    }
    
    public void forward_next_proxy(){ //token forwarding between proxies
        if(proxy_db.get(0).proxy_node.getFill().equals(Color.RED)){
            proxy_db.get(0).proxy_node.setFill(Color.YELLOW);
            proxy_db.get(1).proxy_node.setFill(Color.RED);
        }
        else{
            proxy_db.get(0).proxy_node.setFill(Color.RED);
            proxy_db.get(1).proxy_node.setFill(Color.YELLOW);
        }          
    }
    
    public boolean mh_has_token(){
        int i;
        for(i=0;i<24;i++){
            if(mh_db.get(i).mh_node.getFill().equals(Color.YELLOW))
                break;
        }
        if(i>23)
            return false;
        else
            return true;
    }
    
    public boolean proxy_has_token(){
        int i;
        for(i=0;i<2;i++){
            if(proxy_db.get(i).proxy_node.getFill().equals(Color.YELLOW))
                break;
        }
        if(i>1)
            return false;
        else
            return true;
    }
    
    public void makedraggable(Circle circle){
        circle.setOnMousePressed(onMousePressedEventHandler);
        circle.setOnMouseDragged(onMouseDraggedEventHandler);
        circle.setOnMouseReleased(onMouseReleasedEventHandler);
    }
    
    public void mh_hop(int moveover_local, mh mh_node){
        if(moveover_local == 12){
            request contains_request =  new request();
            contains_request.node_id = mh_node.mh_id;
            contains_request.node_proxy = mh_node.init_proxy;
            for(int i=0;i<proxy_db.get(0).request_queue.size();i++) //can use to print request queue
                if(proxy_db.get(0).request_queue.get(i).node_id == contains_request.node_id && proxy_db.get(0).request_queue.get(i).node_proxy.proxy_id == contains_request.node_proxy.proxy_id){
                    proxy_db.get(0).request_queue.get(i).node_proxy = proxy_db.get(1);
                    break;
                }
        }
        else if(moveover == 21){
            request contains_request =  new request();
            contains_request.node_id = mh_node.mh_id;
            contains_request.node_proxy = mh_node.init_proxy;
            for(int i=0; i<proxy_db.get(1).request_queue.size();i++){
                if(proxy_db.get(1).request_queue.get(i).node_id == contains_request.node_id && proxy_db.get(1).request_queue.get(i).node_proxy.proxy_id == contains_request.node_proxy.proxy_id){
                    proxy_db.get(1).request_queue.get(i).node_proxy = proxy_db.get(0);
                    break;
                }
            }
        }
    }
   
    public class send_join_and_inform_msg extends Thread{
        int moveover_local;
        mh mh_node;
        public send_join_and_inform_msg(int moveover_local, mh mh_node){ 
            this.moveover_local = moveover_local;
            this.mh_node = mh_node;
             
        }
        public void run(){
        if(moveover_local == 12){
            if(mh_node.init_proxy!=null){
                Platform.runLater(() -> right_msg.setText("join(" + mh_node.mh_id+"," +mh_node.init_proxy.proxy_id+") to MSS"));
                Platform.runLater(() ->left_msg.setText("inform("+mh_node.mh_id+",1) to p1")); 
                try{
                    sleep(3000);
                    Platform.runLater(() ->right_msg.setText(null));
                    Platform.runLater(() -> left_msg.setText(null));
                }
                catch(Exception e){}
            }
            else{
                Platform.runLater(() -> right_msg.setText("join(" + mh_node.mh_id+"," +"null) to MSS"));
                try{
                    sleep(3000);
                    Platform.runLater(() ->right_msg.setText(null));
                }
                catch(Exception e){}
            }
        }
        else if(moveover_local==21){
            if(mh_node.init_proxy!=null){
                Platform.runLater(() -> right_msg.setText("join(" + mh_node.mh_id+"," +mh_node.init_proxy.proxy_id+") to MSS"));
                Platform.runLater(() ->left_msg.setText("inform("+mh_node.mh_id+",1) to p2")); 
                try{
                    sleep(3000);
                    Platform.runLater(() ->right_msg.setText(null));
                    Platform.runLater(() -> left_msg.setText(null));
                }
                catch(Exception e){}
            }
            else{
                Platform.runLater(() -> right_msg.setText("join(" + mh_node.mh_id+"," +"null) to MSS"));
                try{
                    sleep(3000);
                    Platform.runLater(() ->right_msg.setText(null));
                }
                catch(Exception e){
                    System.out.print(e);
                }
            }
        }
            
        }
       
    }       
    
    EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent event){
            if(event.getClickCount()!=2){
                Circle circle = (Circle) event.getSource();
                x_old = circle.getCenterX();
                y_old = circle.getCenterY();
            }
        }
    };
    
    EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent event){
            Circle circle = (Circle) event.getSource();
            circle.setCenterX(event.getSceneX());
            circle.setCenterY(event.getSceneY());
            if(x_old<350 && event.getSceneX()>=350 && entered !=1){
               moveover = 12;  
               x_old =0; y_old =0;
               entered =1;
               for(int i =0 ;i<24;i++){
                   if(mh_db.get(i).mh_node.equals(circle)){
                       mh_hop(moveover,mh_db.get(i));
                        new send_join_and_inform_msg(moveover,mh_db.get(i)).start();
                       break;
                   }
                   
               }
            }
            else if(x_old>350 && event.getSceneX()<=350 && entered !=1){
               moveover =21;
               x_old =0; y_old =0;
               entered =1;
               for(int i =0 ;i<24;i++){
                   if(mh_db.get(i).mh_node.equals(circle)){
                       mh_hop(moveover,mh_db.get(i));
                       new send_join_and_inform_msg(moveover,mh_db.get(i)).start();
                       break;
                   }
                }
            }
        }
    };
    
    EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent event){
            entered =0;
        }
    };
}
