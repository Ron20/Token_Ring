package code;

import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

public class MouseGestures 
{
    ArrayList<Circle> mh_movedTo = new ArrayList<Circle>();
    ArrayList<Circle> mh_movedFrom = new ArrayList<Circle>();
    double xcor=0, x_new=0,ycor=0,y_new=0;
     
     
  class DragContext
  {
    double x;
    double y;
  }

  DragContext dragContext = new DragContext();

  public void makeDraggable(Node node)
  {
    node.setOnMousePressed(onMousePressedEventHandler);
    node.setOnMouseDragged(onMouseDraggedEventHandler);
    node.setOnMouseReleased(onMouseReleasedEventHandler);
    
  }
  
  EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>()
  {
    @Override
    public void handle(MouseEvent event)
    {
       if(event.getClickCount()!=2){
      if(event.getSource() instanceof Circle)
      {
        
         Circle circle = ((Circle) (event.getSource()));
        xcor = circle.getCenterX();
        ycor = circle.getCenterY();
        dragContext.x = circle.getCenterX() - event.getSceneX();
        dragContext.y = circle.getCenterY() - event.getSceneY();
      }
       }
    }
  };

  EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>()
  {
    @Override
    public void handle(MouseEvent event)
    {
      if ( event.getSource() instanceof Circle)
      {
        Circle circle = ((Circle) (event.getSource())); 
        circle.setCenterX( dragContext.x + event.getSceneX());
        circle.setCenterY( dragContext.y + event.getSceneY());
        x_new = circle.getCenterX();
        y_new = circle.getCenterY(); 
      }
    }
  };

  EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>()
  {
    @Override
    public void handle(MouseEvent event) {
        
    }
  };
}
