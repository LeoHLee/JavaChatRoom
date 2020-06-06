package View;

import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import java.io.IOException;
import java.util.Vector;

public class searchList
{
    private Alert1 alert1;
    private Apply apply;
    private Button head;
    private Button add;
    private Label information;
    private Pane pane;

    searchList(int h_id, int id, String nickname) throws IOException
    {
        alert1 = new Alert1();

        head = new Button();
        head.setPrefSize(56, 56);
        head.setLayoutX(2);
        head.setLayoutY(2);
        head.getStyleClass().add("head");
        MainWindow.setHead(head, h_id);

        add = new Button();
        add.setPrefSize(32, 32);
        add.setLayoutX(400);
        add.setLayoutY(14);
        add.getStyleClass().add("add");
        add.setTooltip(new Tooltip("添加"));

        information = new Label();
        information.setPrefSize(200, 32);
        information.setLayoutX(65);
        information.setLayoutY(5);
        information.setText(nickname);
        information.getStyleClass().add("information");

        pane = new Pane();
        pane.setPrefSize(470, 60);
        pane.getStyleClass().add("ListItem");

        pane.getChildren().add(head);
        pane.getChildren().add(information);
        pane.getChildren().add(add);

        apply = new Apply(h_id, id, nickname);
        apply.setModality(Control.searchnew);
        sendAdd(id);
    }

    //set & get for private member
    public void setText(String Text){
        information.setText(Text);
    }
    public String getText(){ return information.getText();}
    public Pane getPane(){ return pane;}

    //add action
    public void sendAdd(int id)
    {
        add.setOnAction(event -> {
            if(id==Control.usrInfo.id)
            {
                alert1.exec(Control.searchnew, "不能添加自己为好友！");
            }
            else
            {
                Control.searchnew.loading.exec(Control.searchnew, "请稍候...");
                Control.searchnew.loading.close();
                apply.show();
            }
        });
    }

}
