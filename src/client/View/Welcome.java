package View;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import java.io.IOException;


/*
 * Welcome Page.
 */
public class Welcome extends Window 
{
	public Loading loading;
	public Alert alert;
	public Alert1 alert1;
	public Welcome() throws IOException
	{
		//scene init
		root = FXMLLoader.load(getClass().getResource("Fxml/Wel.fxml"));
		Scene scene = new Scene(root, 480, 360);
		initStyle(StageStyle.TRANSPARENT);
		setScene(scene);
		Move();
		setTitle("Welcome");
		//tool init
		setClose();
		((Button) search("Logging")).setVisible(false);
		((Button) search("Logging")).setManaged(false);
		((Button) search("AutoLogin1")).setVisible(false);
		((Button) search("AutoLogin1")).setManaged(false);
		((Button) search("Remember1")).setVisible(false);
		((Button) search("Remember1")).setManaged(false);
		setAutoLogin_Remember();
		((Label) search("Icon")).requestFocus();//set initial focus
		loading = new Loading();
		loading.setModality(this);
		alert = new Alert();
		alert.setModality(this);
		alert1 = new Alert1();
		alert1.setModality(this);
	}
	//set the actions and tips of Min button and close button
	public void setClose()
	{
		((Button) search("Close")).setTooltip(new Tooltip("关闭"));
		((Button) search("Min")).setTooltip(new Tooltip("最小化"));
		((Button) search("Close")).setOnAction(event ->{
			this.close();
			this.clear();
			System.exit(0);
		});
		((Button) search("Min")).setOnAction(event ->{
			setIconified(true);
		});
	}
	//set the action of AutoLogin(1) and Remember(1)
	public void setAutoLogin_Remember()
	{
		Button sel = ((Button) search("AutoLogin1"));
		Button unsel = ((Button) search("AutoLogin"));
		Button rem = ((Button) search("Remember1"));
		Button unrem = ((Button) search("Remember"));
		sel.setOnAction(event ->{
			sel.setVisible(false);
			sel.setManaged(false);
			unsel.setVisible(true);
			unsel.setManaged(true);
			rem.setManaged(false);
			rem.setVisible(false);
			unrem.setManaged(true);
			unrem.setVisible(true);
		});
		unsel.setOnAction(event ->{
			unsel.setManaged(false);
			unsel.setVisible(false);
			sel.setManaged(true);
			sel.setVisible(true);
			rem.setManaged(true);
			rem.setVisible(true);
			unrem.setManaged(false);
			unrem.setVisible(false);

		});
		rem.setOnAction(event ->{
			rem.setVisible(false);
			rem.setManaged(false);
			unrem.setVisible(true);
			unrem.setManaged(true);
		});
		unrem.setOnAction(event ->{
			unrem.setVisible(false);
			unrem.setManaged(false);
			rem.setVisible(true);
			rem.setManaged(true);
		});
	}

	//set Error Tip for input
	public void setErrTip(String id, String text)
	{
		((Label) search(id)).setText(text);
	}
	//clear Error Tip
	public void ClrErrTip()
	{
		setErrTip("ACCErr","");
		setErrTip("PWErr","");
	}
	//clear the contents in UserName & Password
	public void clear()
	{
		((TextField) search("UserName")).clear();
		((PasswordField) search("Password")).clear();
		((Label) search("Icon")).requestFocus();//set initial focus
		ClrErrTip();
	}
}
