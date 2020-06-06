package View;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/*
 * the father of all windows
 */
public class Window extends Stage
{
	Parent root;
	private double xOffset;
	private double yOffset;
	Window()
	{
		Icon();
	}

	//set icon
	public void Icon()
	{
		getIcons().add(new Image(getClass().getResourceAsStream("/View/Fxml/CSS/Pic/Icons/chat.jpg")));
	}
	//dragging the window
	public void Move()
	{
		root.setOnMousePressed(event->
		{
			xOffset = event.getSceneX() ;
			yOffset = event.getSceneY() ;
			root.setCursor(Cursor.MOVE);//Macro CLOSED_HAND is always used for grabbing.
		});
		root.setOnMouseDragged(event->
		{
			setX(event.getScreenX() - xOffset);
			setY(event.getScreenY() - yOffset);
		});
		root.setOnMouseReleased(event ->{
			root.setCursor(Cursor.DEFAULT);//After dragging, set the shape of cursor to origin style.
		});
	}
	//choose element
	public Object search(String id)
	{
		return (Object) root.lookup("#"+id);
	}
	
}
