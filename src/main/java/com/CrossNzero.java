import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;

public class CrossNzero extends Application {
    Crestik[][] crst = new Crestik[3][3];
    private static final IntegerProperty I_MOVED = new SimpleIntegerProperty(0);
    static int[] Steps = new int[1];
    static int[] I_Win = {1};
    static RadioMenuItem menuItemMedium;

    public void start(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane, 420.0, 420.0, Color.rgb(235, 235, 235, 0.7));
        HBox hbox1 = new HBox(10.0);
        HBox hbox2 = new HBox(10.0);
        hbox1.prefWidthProperty().bind(primaryStage.widthProperty());
        hbox2.prefWidthProperty().bind(primaryStage.widthProperty());
        VBox vbox1 = new VBox(4.0);
        vbox1.getChildren().addAll(hbox1);
        vbox1.setTranslateX(42.0);
        vbox1.setTranslateY(10.0);
        pane.setCenter(vbox1);
        pane.setBottom(hbox2);
        pane.setMargin(hbox2, new Insets(5, 5, 5, 5));
        primaryStage.setTitle("Обыграй меня!");

        InstGrids(hbox1, pane);
        InstButtons(hbox2);
        menuItemMedium = MakeMenu.show(pane);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        CrossNzero.launch((String[]) args);
    }

    void InstGrids(HBox hbox, BorderPane pane) {
        Rectangle r;
        hbox.setPadding(new Insets(6.0, 6.0, 6.0, 6.0));
        Group gr = new Group();
        int oh = 0;
        int n = 3;
        int n2 = 0;
        while (n2 < n) {
            r = new Rectangle(300.0, 100.0);
            r.setY((double) (100 * oh++));
            r.setX(0.0);
            r.setStroke(Color.rgb(14, 20, 70, 0.6));
            r.setStrokeWidth(6.0);
            r.setFill(Color.WHITE);
            r.setCursor(Cursor.HAND);
            r.setOnMouseClicked(e -> {
                boxCheck(e);
            });
            gr.getChildren().add(r);
            ++n2;
        }
        oh = 0;
        n = 3;
        n2 = 0;
        while (n2 < n) {
            r = new Rectangle(100.0, 300.0);
            r.setX((double) (100 * oh++));
            r.setY(0.0);
            r.setStroke(Color.rgb(14, 20, 170, 0.6));
            r.setStrokeWidth(6.0);
            r.setFill(Color.rgb(255, 255, 255, 0.0));
            r.setCursor(Cursor.HAND);
            r.setOnMouseClicked(e -> {
                boxCheck(e);
            });
            gr.getChildren().add(r);
            ++n2;
        }

        hbox.getChildren().add(gr);
        Pole[] pp = Pole.values();
        int i = 0;
        while (i < 3) {
            int j = 0;
            while (j < 3) {
                crst[i][j] = new Crestik(i * 100, j * 100, gr);
                int n3 = pp.length;
                int n4 = 0;
                while (n4 < n3) {
                    Pole p = pp[n4];
                    if (i * 3 + j == p.ordinal()) {
                        crst[i][j].name = p;
                    }
                    ++n4;
                }
                ++j;
            }
            ++i;
        }
    }

    void InstButtons(HBox hbox) {
        Button button1 = new Button("Начать");
        final Button button2 = new Button("Первый ход за компьютером");
        Button button3 = new Button("Закончить");
        hbox.setPadding(new Insets(20.0, 8.0, 20.0, 8.0));
        hbox.getChildren().addAll(button1, button2, button3);


        button3.setOnAction(e -> {

                    if (ConfirmationBox.show("Уже уходите?", "Что случилось?", "Увы", "Неа")) Platform.exit();
                }
        );
        button1.setOnAction(e -> {
                    Steps[0] = 0;
                    I_Win[0] = 1;
                    Redrow();
                }
        );
        button2.setOnAction(e -> {
                    Steps[0] = 0;
                    I_Win[0] = 1;
                    Redrow();
                    I_MOVED.set(1);
                }
        );
        I_MOVED.addListener(e -> {
                    Play();
                }
        );
    }

    void InformMe(String s, String k) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(s);
        alert.setHeaderText(null);
        alert.setContentText(k);
        alert.showAndWait();
    }

    void Play() {
        System.out.println("Ход :" + Steps[0]);
        if (I_MOVED.get() == 0) {
            return;
        }
        Pole a = CompPlay.playMove(crst);
        if (a != null) {
            this.crst[a.i][a.j].nolik = false;
            this.crst[a.i][a.j].setVis(true);
            int[] arrn = Steps;
            arrn[0] = arrn[0] + 1;
            if (Steps[0] > 8) {
                if (!(I_Win[0] > 1)) InformMe("Доигрались!", "Ничья! Победила дружба!");
            }
        } else {
            if (!(I_Win[0] > 1)) InformMe("Доигрались!", "Ничья! Победила дружба!");
        }
    }

    void Redrow() {
        int i = 0;
        while (i < 3) {
            int j = 0;
            while (j < 3) {
                this.crst[i][j].setVis(false);
                I_MOVED.set(0);
                ++j;
            }
            ++i;
        }
    }

    void boxCheck(MouseEvent e) {
        int i = (int) (e.getX() / 100.0);
        int j = (int) (e.getY() / 100.0);
        if (Steps[0] <= 8 && !this.crst[i][j].checked) {
            crst[i][j].nolik = true;
            crst[i][j].setVis(true);
            int[] arrn = Steps;
            arrn[0] = arrn[0] + 1;
            I_MOVED.set(I_MOVED.add(1).get());
        }
    }

    Stream<Crestik> crest2Str(Crestik[][] cr) {
        Crestik[] cr1 = new Crestik[9];
        System.arraycopy(cr[0], 0, cr1, 0, 3);
        System.arraycopy(cr[1], 0, cr1, 3, 3);
        System.arraycopy(cr[2], 0, cr1, 6, 3);
        return Arrays.stream(cr1);
    }

    Stream<Crestik> crest21Str(Crestik[][] cr) {
        Crestik[] cr1 = new Crestik[]{cr[0][0], cr[1][1], cr[2][2]};
        return Arrays.stream(cr1);
    }

    Stream<Crestik> crest22Str(Crestik[][] cr) {
        Crestik[] cr1 = new Crestik[]{cr[2][0], cr[1][1], cr[0][2]};
        return Arrays.stream(cr1);
    }

    boolean proCheck(Crestik[][] crst, boolean cr) {
        Stream<Crestik> ck;
        int[] priz = new int[1];
        int k = 1;
        while (k < 7) {
            ck = this.crest2Str(crst);
            int j = k++;
            priz[0] = 0;
            ck.forEach(p -> {
                        if ((p.nolik == cr) && p.name.toString().contains("ABC123".substring(j - 1, j)) && p.checked) {
                            priz[0] = priz[0] + 1;
                        }
                    }
            );
            if (priz[0] <= 2) continue;
            return true;
        }
        ck = this.crest21Str(crst);
        long k2 = ck.filter(p -> (p.nolik == cr) && p.checked).count();
        if (k2 > 2) {
            return true;
        }
        ck = this.crest22Str(crst);
        k2 = ck.filter(p -> (p.nolik == cr) && p.checked).count();
        return (k2 > 2);
    }

    boolean checkWin() {
        if (proCheck(crst, false)) {
            //InformMe("Протупили...", "Выиграл Нолик!");
            MessageBox.show("Выиграл Нолик!", "Протупили...!", true);
            Steps[0] = 100;
            I_Win[0] = 2;
            return true;
        }
        if (proCheck(crst, true)) {
            //InformMe("Поздравляю!", "Выиграл Крестик!");
            MessageBox.show("Выиграл Крестик!", "Поздравляю!", false);
            Steps[0] = 100;
            I_Win[0] = 2;
            return true;
        }
        return false;
    }


    static class CompPlay
            implements Player {
        CompPlay() {
        }

        public static Pole playMove(Crestik[][] cr) {
            Pole p = null;
            if (Steps[0] > 8) return null;
            Random rn = new Random();
            int i, j;
            do {
                i = rn.nextInt(3);
                j = rn.nextInt(3);
            } while (cr[i][j].checked);
            p = CompPlay.CheckIfIWin(cr);
            if (p == null) p = cr[i][j].name;
            return p;
        }



        public static Pole CheckIfIWin (Crestik [][] cr) {
            class Z {
                int i,j,k;
                String S;
                Z(int i, int j, String S, int k){this.i=i; this.j=j; this.S=S; this.k=k;}
                @Override  public String toString (){
                    return " "+Integer.toString(i)+","+Integer.toString(j)+"; ";
                }
            }

            String [] who ={"diagonal1", "diagonal2", "line1", "line2", "line3", "row1", "row2", "row3"};

            List<Z> X = new LinkedList<>();
            for (int i=1; i<4; i++){
                int k=0;
                X.add(new Z(i,i, who[k], k));
                X.add(new Z(i,4-i, who[++k], k));
                X.add(new Z(i,1, who[++k], k));
                X.add(new Z(i,2, who[++k], k));
                X.add(new Z(i,3, who[++k], k));
                X.add(new Z(1,i, who[++k], k));
                X.add(new Z(2,i, who[++k], k));
                X.add(new Z(3,i, who[++k], k));
            }

            if (menuItemMedium.isSelected())    {
                if (Steps[0]==0) return cr[1][1].name;
                Z RekHod=null, RekHod1=null, RekHod2=null;
                for (int i=0; i<8; i++) {
                    List<Z> X1 = new LinkedList<>();
                    for (Z Z1 : X) {
                        if (Z1.k == i) {
                            X1.add(Z1);
                        }
                    }

                    if (X1.stream().filter((p) -> (cr[p.i-1][p.j-1].checked)).count()==2) {
                        RekHod=X1.stream().filter((p) -> (!cr[p.i-1][p.j-1].checked)).findFirst().get();
                        if (X1.stream().filter((p) -> ((cr[p.i-1][p.j-1].checked)&&(!cr[p.i-1][p.j-1].nolik))).count()==2)
                            RekHod1=RekHod;
                        if (X1.stream().filter((p) -> ((cr[p.i-1][p.j-1].checked)&&(cr[p.i-1][p.j-1].nolik))).count()==2)
                            RekHod2=RekHod;
                    }
                }
                RekHod=(RekHod1==null) ? RekHod2 : RekHod1;
                if (!(RekHod==null)) return cr[RekHod.i-1][RekHod.j-1].name;
            }
            return null;

        }

    }



    class Crestik {
        SVGPath deniedIcon;
        Ellipse smallCircle;
        boolean checked;
        Pole name;
        boolean nolik;

        Crestik(int n, int k, Group group) {
            this.checked = false;
            this.nolik = false;
            this.name = name;
            this.deniedIcon = new SVGPath();
            this.deniedIcon.setFill(Color.rgb(250, 0, 150, 0.9));
            this.deniedIcon.setStroke(Color.WHITE);
            this.deniedIcon.setContent("M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 16.447,13.087,10.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10.946,24.248 16.447,18.746 21.948,24.248z");
            this.deniedIcon.setVisible(false);
            this.deniedIcon.setScaleX(2.0);
            this.deniedIcon.setScaleY(2.0);
            this.deniedIcon.setLayoutX((double) (n + 30));
            this.deniedIcon.setLayoutY((double) (k + 30));
            group.getChildren().add(deniedIcon);
            this.smallCircle = new Ellipse((double) (50 + n), (50 + k), 14.0, 20.0);
            this.smallCircle.setVisible(false);
            this.smallCircle.setFill(Color.WHITE);
            this.smallCircle.setStroke(Color.rgb(30, 40, 255, 0.9));
            this.smallCircle.setStrokeWidth(5.0);
            group.getChildren().add(smallCircle);
        }

        void setVis(boolean bol1) {
            if (bol1) {
                if (this.nolik) {
                    this.deniedIcon.setVisible(true);
                    this.checked = true;
                } else {
                    this.smallCircle.setVisible(true);
                    this.checked = true;
                }
                if (checkWin()) {
                    return;
                }
            } else {
                this.deniedIcon.setVisible(false);
                this.smallCircle.setVisible(false);
                this.checked = false;
                this.nolik = false;
            }
        }

        boolean checked() {
            return this.checked;
        }
    }

    interface Player {
        static Pole playMove(Crestik[][] cr) {
            return null;
        }
    }

    enum Pole {
        A1(0, 0), A2(0, 1), A3(0, 2), B1(1, 0), B2(1, 1), B3(1, 2), C1(2, 0), C2(2, 1), C3(2, 2);

        int i = 0;
        int j = 0;

        Pole(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }
}



class MessageBox
{
    public static void show(String message, String title, boolean t)
    {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle(title);
        stage.setResizable(false);
        Label lbl = new Label();
        lbl.setText(message);
        lbl.setMaxWidth(10000);
        lbl.setAlignment(Pos.CENTER);
        lbl.setWrapText(true);
        Button btnOK = new Button();
        btnOK.setText("OK");
        btnOK.setOnAction(e -> stage.close());

        VBox pane2 = new VBox(20);
        ImageView imgv = null;
        if (t) {imgv=new ImageView(new Image("crsnzer.gif"));}
            else {imgv=new ImageView(new Image("cr1.gif"));}

        imgv.setTranslateX(15); imgv.setTranslateY(15);

        HBox pane1 = new HBox(30);
        pane1.getChildren().addAll(imgv,pane2);
        pane2.getChildren().addAll(lbl, btnOK);
        pane2.setAlignment(Pos.CENTER_RIGHT);
        btnOK.setTranslateX(-15);
        lbl.setTranslateX(-15);
        HBox.setHgrow(pane2, Priority.ALWAYS);
        Scene scene = new Scene(pane1, 300,95);
        scene.setFill(Color.rgb(50,70,220));
        stage.setScene(scene);
        stage.showAndWait();
    } }



class MakeMenu {
    public static RadioMenuItem show(BorderPane pane)
    {
        MenuBar menu1 = new MenuBar();
        Menu menu2 = new Menu("Выбери сложность");
        menu1.getMenus().add(menu2);
        RadioMenuItem menuItemEasy = new RadioMenuItem("Дурак");
        RadioMenuItem menuItemMedium = new RadioMenuItem("Такой себе");
        RadioMenuItem menuItemHard = new RadioMenuItem("Умник");
        ToggleGroup groupDifficulty = new ToggleGroup();
        menuItemEasy.setToggleGroup(groupDifficulty);
        menuItemEasy.setSelected(true);
        menuItemMedium.setToggleGroup(groupDifficulty);
        menuItemHard.setOnAction(event -> {MessageBox.show("Алгоритм еще не разработан","Опа....!", true); menuItemEasy.setSelected(true);});
        menuItemHard.setToggleGroup(groupDifficulty);
        menu2.getItems().add(menuItemEasy);
        menu2.getItems().add(menuItemMedium);
        menu2.getItems().add(menuItemHard);
        pane.setTop(menu1);
        return menuItemMedium;

    }

}


class ConfirmationBox  {
    static Stage stage;
    static boolean btnYesClicked;
    public static boolean show(String message, String title, String textYes, String textNo)
    {
        btnYesClicked = false;
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle(title);
        stage.setResizable(false);
        Label lbl = new Label();
        lbl.setText(message);
        Button btnYes = new Button(); btnYes.setText(textYes);
        btnYes.setOnAction(e -> btnYes_Clicked() );
        Button btnNo = new Button();
        btnNo.setText(textNo);
        btnNo.setOnAction(e -> btnNo_Clicked() );
        HBox paneBtn = new HBox(20);
        paneBtn.setAlignment(Pos.CENTER);
        paneBtn.getChildren().addAll(btnYes, btnNo);
        VBox pane = new VBox(20);
        pane.getChildren().addAll(lbl, paneBtn);
        pane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(pane, 300, 95);
        scene.setFill(Color.rgb(50,70,220));
        stage.setScene(scene);
        stage.showAndWait();
        return btnYesClicked;
    }
    private static void btnYes_Clicked()  {
        stage.close();
        btnYesClicked = true; }
    private static void btnNo_Clicked()  {
        stage.close();
        btnYesClicked = false; }
}