package View;

import com.uz.emojione.Emoji;
import com.uz.emojione.EmojiOne;
import com.uz.emojione.fx.ImageCache;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import java.util.Queue;
import java.util.Vector;

public class ChatList
{
    private Pane pane;
     Button head;
    private TextFlow text;
    private Pane left;
    private Pane right;
    private Button p1;
    private Button p3;
    private Button p5;
    private Button p7;
    private Button p2;
    private Button p4;
    private Line l1;
    private Line l2;
    Vector<MenuItem> items;
    public Button send;

    private Button arrow;
    private String context;
    private int mid;

    public ChatList()
    {
        l1 = new Line();
        l2 = new Line();
        p1 = new Button();
        p3 = new Button();
        p5 = new Button();
        p7 = new Button();
        p2 = new Button();
        p4 = new Button();
        send = new Button();

        send.setLayoutX(0);send.setLayoutY(0);
        send.setPrefSize(600, 70);
        send.setStyle("-fx-background-color: transparent");

        head = new Button();
        head.setPrefSize(50, 50);
        head.getStyleClass().add("head");

        text = new TextFlow();
        text.setPrefSize(300, 50);


        left = new Pane();
        left.setPrefSize(400, 60);
        left.getStyleClass().add("pane");

        right = new Pane();
        right.setPrefSize(400, 60);
        right.getStyleClass().add("pane");

        pane = new Pane();
        pane.setPrefSize(600, 70);
        pane.getStyleClass().add("pane");

        arrow = new Button();
        arrow.setPrefSize(32,32);
        arrow.setDisable(false);

        context = "";

        items = new Vector<>();
        items.add(new MenuItem("复制"));
        items.add(new MenuItem("删除"));
        setMenu();
        copy();

    }

    public Pane getPane()
    {
        return pane;
    }

    // set right-click menu
    public void setMenu()
    {
        ContextMenu menu = new ContextMenu();
        for(int i=0; i<items.size(); ++i)
        {
            menu.getItems().add(items.get(i));
        }
        send.setContextMenu(menu);
    }
    public void copy()
    {
        items.get(0).setOnAction(event -> {
            StringSelection selection = new StringSelection(context);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        });
    }

    private String getEmojiImagePath(String hexStr) {
        return getClass().getResource("Fxml/CSS/Pic/png_40/" + hexStr + ".png").toExternalForm();
    }
    private Node createEmojiNode(Emoji emoji) {
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(3));
        ImageView imageView = new ImageView();
        imageView.setFitWidth(15);
        imageView.setFitHeight(15);
        imageView.setImage(ImageCache.getInstance().getImage(getEmojiImagePath(emoji.getHex())));
        stackPane.getChildren().add(imageView);
        return stackPane;
    }
    private void addText(String context) {
        Text textNode = new Text(context);
        textNode.setFont(Font.font(16));
        textNode.setTextAlignment(TextAlignment.CENTER);
        text.getChildren().add(textNode);
    }
    public void setEmoji(String context)
    {
        Queue<Object> obs = EmojiOne.getInstance().toEmojiAndText(context);
        while(!obs.isEmpty()) {
            Object ob = obs.poll();
            if(ob instanceof String) {
                addText((String)ob);
            }
            else if(ob instanceof Emoji) {
                Emoji emoji = (Emoji) ob;
                text.getChildren().add(createEmojiNode(emoji));
            }
        }
    }


    //friend message setting
    public Pane setLeft(int hd,String fText, double width, double height, int mid)
    {
        this.mid = mid;
        context = fText;

        text.getStyleClass().add("leftText");
        arrow.getStyleClass().add("leftArrow");

        pane.setPrefHeight(30+height);
        left.setPrefHeight(30+height);
        text.setPrefSize(width, height+14);
        head.setLayoutY(5);
        head.setLayoutX(10);
        MainWindow.setHead(head, hd);

        text.setLayoutX(90);
        text.setLayoutY(10);
        setEmoji(fText);

        l1.setStartX(92);l1.setStartY(10.2);
        l1.setEndX(81+width);l1.setEndY(10.2);
        l1.getStyleClass().add("leftLine");
        l2.setStartX(92.5);l2.setStartY(25.2+height);
        l2.setEndX(76.5+width);l2.setEndY(26.4+height);
        l2.getStyleClass().add("leftLine");

        p2.setPrefSize(42,height-20);
        p4.setPrefSize(42,height-20);
        p1.setPrefSize(42,27);
        p7.setPrefSize(48,26);
        p3.setPrefSize(43,37);
        p5.setPrefSize(42,24);

        p1.setLayoutX(65);
        p1.setLayoutY(3);
        p2.setLayoutX(65);
        p2.setLayoutY(27);
        p3.setLayoutX(75+width);
        p3.setLayoutY(0);
        p4.setLayoutX(80.5+width);
        p4.setLayoutY(33);
        p5.setLayoutX(77+width);
        p5.setLayoutY(11+height);
        p7.setLayoutX(66);
        p7.setLayoutY(7+height);
        p1.getStyleClass().add("cell1");
        p2.getStyleClass().add("cell11");
        p4.getStyleClass().add("cell33");
        if(height > 25)
        {
            p2.setStyle(String.format("-fx-background-size: 42px %f"+"px", height));
            p4.setStyle(String.format("-fx-background-size: 42px %f"+"px", height));
        }
        else
        {
            p2.setStyle(String.format("-fx-background-size: 42px %f"+"px", 15.0));
            p4.setStyle(String.format("-fx-background-size: 42px %f"+"px", 15.0));
        }
        p3.getStyleClass().add("cell3");
        p5.getStyleClass().add("cell5");
        p7.getStyleClass().add("cell7");
        arrow.setLayoutX(68);
        arrow.setLayoutY(14);

        left.getChildren().add(p2);
        left.getChildren().add(p4);
        left.getChildren().add(head);
        left.getChildren().add(p1);
        left.getChildren().add(text);
        left.getChildren().add(p3);
        left.getChildren().add(p5);
        left.getChildren().add(p7);

        left.getChildren().add(l1);
        left.getChildren().add(l2);
        pane.getChildren().add(left);
        pane.getChildren().add(send);
        return pane;
    }

    //self message setting
    public Pane setRight(int hd, String iText, double width, double height, int mid)
    {
        width = width-5;
        this.mid = mid;
        context = iText;
        setEmoji(iText);
        text.getStyleClass().add("rightText");
        arrow.getStyleClass().add("rightArrow");
        pane.setPrefHeight(30+height);
        left.setPrefHeight(30+height);
        text.setPrefSize(width, height+14);
        head.setLayoutX(300);
        head.setLayoutY(5);
        MainWindow.setHead(head, hd);

        text.setLayoutX(260 - width);
        text.setLayoutY(50);
        text.setPrefSize(width, height+14);

        l1.setStartX(270 - width);
        l1.setStartY(15);
        l1.setEndX(269);
        l1.setEndY(15.5);
        l2.setStartX(270 - width);
        l2.setStartY(28.2+height);
        l2.setEndX(269);
        l2.setEndY(28.2+height);
        p2.setPrefSize(42,height-20);
        p4.setPrefSize(42,height-20);
        p1.setPrefSize(44,41);
        p7.setPrefSize(38,30);
        p3.setPrefSize(42,41);
        p5.setPrefSize(40,30);
        p1.setLayoutX(233 - width);
        p1.setLayoutY(0);
        p2.setLayoutX(236-width);
        p2.setLayoutY(32);
        p3.setLayoutX(255);
        p3.setLayoutY(0);
        p4.setLayoutX(255.5);
        p4.setLayoutY(32);
        p5.setLayoutX(256.5);
        p5.setLayoutY(2.2+height);
        p7.setLayoutX(233-width);
        p7.setLayoutY(2.2+height);
        p2.getStyleClass().add("cell21");
        p4.getStyleClass().add("cell43");
        if(height > 25)
        {
            p2.setStyle(String.format("-fx-background-size: 42px %f"+"px", height));
            p4.setStyle(String.format("-fx-background-size: 42px %f"+"px", height));
        }
        else
        {
            p2.setStyle(String.format("-fx-background-size: 42px %f"+"px", 10.0));
            p4.setStyle(String.format("-fx-background-size: 42px %f"+"px", 10.0));
        }
        p1.getStyleClass().add("cell2");
        p3.getStyleClass().add("cell6");
        p5.getStyleClass().add("cell8");
        p7.getStyleClass().add("cell4");
        arrow.setLayoutX(300);
        arrow.setLayoutY(14);
        text.setLayoutX(270 - width);
        text.setLayoutY(15);
        right.getChildren().add(p2);
        right.getChildren().add(p4);
        right.getChildren().add(p7);
        right.getChildren().add(p1);
        right.getChildren().add(p3);
        right.getChildren().add(p5);
        right.getChildren().add(text);
        right.getChildren().add(l1);
        right.getChildren().add(l2);
        right.getChildren().add(head);

        right.setLayoutX(242);
        pane.getChildren().add(right);

        pane.getChildren().add(send);
        return pane;
    }
}
