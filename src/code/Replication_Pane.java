package code;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;

public class Replication_Pane extends Pane
{
  // Constants
  final int NUM_HOSTS = 6;
  final int HALF_X = 350;
  final int HALF_Y = 225;
  final int RADIUS = 10;
  final float REQUEST_PERIOD = 2.0f; // Number of seconds between each request attempt
  final float FULFILL_PERIOD = 1.0f; // Number of seconds between each request fulfillment
  final Color MH_COLOR = Color.BLUE;
  final Color MSS_COLOR = Color.BLACK;
  final Color TOKEN_COLOR = Color.RED;
  final Color REQUEST_FILLED = Color.rgb(97, 223, 202);
  final Color REQUEST_UNFILLABLE = Color.ORANGE;
  final Color REQUEST_COLOR = Color.GREEN;
  final String sMH_COLOR = "Blue";
  final String sMSS_COLOR = "Black";
  final String sTOKEN_COLOR = "Red";
  final String sREQUEST_FILLED = "Light Blue";
  final String sREQUEST_UNFILLABLE = "Orange";
  final String sREQUEST_COLOR = "Green";

  // Variables
  ArrayList<Circle> mh; // List of circles representing mh
  ArrayList<Circle> vmss; // List of circles representing mss
  ArrayList<ArrayList<Circle>> rmss; // Request lists for each mss
  Line vl, hl; // Lines for quadrants
  Text key; // Contains information about which colors mean what
  int current_mss; // Current mss servicing the token
  Random rand; // RNG for determining requests and initial node placement

  // Initialize Replication_Pane
  public Replication_Pane()
  {
    // Initialize ArrayLists
    mh = new ArrayList<Circle>();
    vmss = new ArrayList<Circle>();
    rmss = new ArrayList<ArrayList<Circle>>();
    for (int i = 0; i < 4; i++)
    {
      rmss.add(new ArrayList<Circle>());
    }

    // Draw the Lines
    vl = new Line(HALF_X, 50, HALF_X, 400);
    hl = new Line(50, HALF_Y, 650, HALF_Y);
    getChildren().addAll(vl, hl);

    // Write out the key
    key = new Text(String.format("MH Default: %s\nMSS Default: %s\nToken: %s\nToken Requested: %s\nRequest Filled: %s\nRequest Unfillable: %s",
                   sMH_COLOR, sMSS_COLOR, sTOKEN_COLOR, sREQUEST_COLOR, sREQUEST_FILLED, sREQUEST_UNFILLABLE));
    getChildren().add(key);

    // Prep generator
    rand = new Random();

    // Draw mh
    Circle new_host;
    for (int i = 0; i < NUM_HOSTS * 4; i++)
    {
      int x = 0;
      int y = 0;
      if (i < NUM_HOSTS) // Quad1
      {
        x = rand.nextInt(HALF_X - 50) + 50;
        y = rand.nextInt(HALF_Y - 50) + 50;
      }
      else if (i < NUM_HOSTS * 2) // Quad2
      {
        x = rand.nextInt(650 - HALF_X) + HALF_X;
        y = rand.nextInt(HALF_Y - 30) + 50;
      }
      else if (i < NUM_HOSTS * 3) // Quad3
      {
        x = rand.nextInt(HALF_X - 50) + 50;
        y = rand.nextInt(400 - HALF_Y) + HALF_Y;
      }
      else // Quad4
      {
        x = rand.nextInt(650 - HALF_X) + HALF_X;
        y = rand.nextInt(400 - HALF_Y) + HALF_Y;
      }
      new_host = new Circle(x, y, RADIUS, MH_COLOR);

      // Add mh to pane and list of mhs
      getChildren().add(new_host);
      mh.add(new_host);
    }

    // Draw mss
    Circle mss1 = new Circle(HALF_X - HALF_X / 2, HALF_Y - HALF_Y / 2, RADIUS, TOKEN_COLOR); // Quad1
    Circle mss2 = new Circle(HALF_X + HALF_X / 2, HALF_Y - HALF_Y / 2, RADIUS, MSS_COLOR); // Quad2
    Circle mss3 = new Circle(HALF_X - HALF_X / 2, HALF_Y + HALF_Y / 2, RADIUS, MSS_COLOR); // Quad3
    Circle mss4 = new Circle(HALF_X + HALF_X / 2, HALF_Y + HALF_Y / 2, RADIUS, MSS_COLOR); // Quad4

    getChildren().addAll(mss1, mss2, mss3, mss4);
    vmss.add(mss1);
    vmss.add(mss2);
    vmss.add(mss3);
    vmss.add(mss4);

    // Allow for draggable mobile hosts
    MouseGestures mg = new MouseGestures();
    for (int i = 0; i < mh.size(); i++)
    {
      mg.makeDraggable(mh.get(i));

      // Also send request on double-click
      final int circle_number = i;
      mh.get(i).setOnMouseClicked(e -> requestOnClick(circle_number, e.getClickCount()));
    }

    // Start requesting
    Timeline requesting = new Timeline(new KeyFrame(Duration.seconds(REQUEST_PERIOD), e -> start_requests()));
    requesting.setCycleCount(Timeline.INDEFINITE);
    requesting.play();

    // Start request fulfillment
    current_mss = 0;
    Timeline fulfill = new Timeline(new KeyFrame(Duration.seconds(FULFILL_PERIOD), e -> fulfill_requests()));
    fulfill.setCycleCount(Timeline.INDEFINITE);
    fulfill.play();
  }

  public void requestOnClick(int i, int e)
  {
    if (e == 2 && !mh.get(i).getFill().equals(TOKEN_COLOR) && !mh.get(i).getFill().equals(REQUEST_COLOR))
    {
      // Change color of requested circle
      if (mh.get(i).getFill().equals(REQUEST_FILLED))
      {
        mh.get(i).setFill(REQUEST_UNFILLABLE);
      }
      else mh.get(i).setFill(REQUEST_COLOR);

      // Add circle to every request list
      for (ArrayList<Circle> mss_list : rmss)
      {
        mss_list.add(mh.get(i));
      }
    }
  }

  // Function handling triggering requests
  public void start_requests()
  {
    for (int i = 0; i < mh.size(); i++)//(Circle host : mh)
    {
      // A request is made with probability 1/n each second for every node (avg 1 req per second)
      if (rand.nextInt(mh.size()) % mh.size() == 0 && !mh.get(i).getFill().equals(REQUEST_COLOR) && !mh.get(i).getFill().equals(REQUEST_UNFILLABLE) && !mh.get(i).getFill().equals(TOKEN_COLOR))
      {
        // Change color of requested circle
        if (mh.get(i).getFill().equals(REQUEST_FILLED))
        {
          mh.get(i).setFill(REQUEST_UNFILLABLE);
        }
        else mh.get(i).setFill(REQUEST_COLOR);

        // Add circle to every request list
        for (ArrayList<Circle> mss_list : rmss)
        {
          mss_list.add(mh.get(i));
        }
      }
    }
  }

  // Function handling the fulfillment of requests
  public void fulfill_requests()
  {
    // Does the MSS have the token or does one of its MHs?
    if (vmss.get(current_mss).getFill().equals(TOKEN_COLOR))
    {
      // If the requests list is populated, service the first one
      if (rmss.get(current_mss).size() > 0)
      {
        // Skip over requests until one is in mss quad
        int j = -1;
        boolean match = false;

        while (!match)
        {
          j++;
          if (rmss.get(current_mss).size() <= j)
          {
            j = -1;
            break;
          }

          // Define results based on current quad
          if (current_mss == 0)
          {
            match = (rmss.get(current_mss).get(j).getCenterX() < HALF_X && rmss.get(current_mss).get(j).getCenterY() < HALF_Y);
          }
          else if (current_mss == 1)
          {
            match = (rmss.get(current_mss).get(j).getCenterX() >= HALF_X && rmss.get(current_mss).get(j).getCenterY() < HALF_Y);
          }
          else if (current_mss == 2)
          {
            match = (rmss.get(current_mss).get(j).getCenterX() < HALF_X && rmss.get(current_mss).get(j).getCenterY() >= HALF_Y);
          }
          else
          {
            match = (rmss.get(current_mss).get(j).getCenterX() >= HALF_X && rmss.get(current_mss).get(j).getCenterY() >= HALF_Y);
          }

        }
        // If one is found in your quadrant, pass it the token
        if (j >= 0 && !rmss.get(current_mss).get(j).getFill().equals(REQUEST_UNFILLABLE))
        {
          rmss.get(current_mss).get(j).setFill(TOKEN_COLOR);
          vmss.get(current_mss).setFill(MSS_COLOR);
        }
        else // If not, pass the token to next mss
        {
          vmss.get(current_mss).setFill(MSS_COLOR);
          if (current_mss == vmss.size() - 1)
          {
            vmss.get(0).setFill(TOKEN_COLOR);
            current_mss = -1;
            for (Circle node : mh)
            {
              if (node.getFill().equals(REQUEST_FILLED))
              {
                node.setFill(MH_COLOR);
              }
              else if (node.getFill().equals(REQUEST_UNFILLABLE))
              {
                node.setFill(REQUEST_COLOR);
              }
            }
          }
          else vmss.get(current_mss + 1).setFill(TOKEN_COLOR);
          current_mss++;
        }
      }
      // If request list is empty, pass the token to next mss
      else
      {
        vmss.get(current_mss).setFill(MSS_COLOR);
        if (current_mss == vmss.size() - 1)
        {
          vmss.get(0).setFill(TOKEN_COLOR);
          current_mss = -1;
          // Color all REQUEST_FILLED tokens back to normal and all unfillable requests to normal requests
          for (Circle node : mh)
          {
            if (node.getFill().equals(REQUEST_FILLED))
            {
              node.setFill(MH_COLOR);
            }
            else if (node.getFill().equals(REQUEST_UNFILLABLE))
            {
              node.setFill(REQUEST_COLOR);
            }
          }
        }
        else vmss.get(current_mss + 1).setFill(TOKEN_COLOR);
        current_mss++;
      }
    }

    // If the current_mss doesn't have the token
    else
    {
      for (int j = 0; j < rmss.get(current_mss).size(); j++)
      {
        // Find the MH that does and return it
        if (rmss.get(current_mss).get(j).getFill().equals(TOKEN_COLOR))
        {
          // Return token
          rmss.get(current_mss).get(j).setFill(REQUEST_FILLED);
          vmss.get(current_mss).setFill(TOKEN_COLOR);

          // Remove from request lists
          for (int k = 0; k < rmss.size(); k++)
          {
            rmss.get(k).remove(j);
          }
          break;
        }
      }
    }
  }
}
