import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{
    static final int SCREEN_WIDTH=512;
    static final int SCREEN_HEIGHT=512;
    static final int UNIT_SIZE=32;
    static final int GAME_UNITS=(SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY=128;
    final int[] x = new int[ GAME_UNITS];
    final int[] y = new int[ GAME_UNITS];
    int bodyParts=3;
    int foodEaten;
    int foodX;
    int foodY;
    char direction='R';
    boolean running=false;
    int highScore;

    Font dpcomic_P_24 = new Font("dpcomic", Font.PLAIN, 24);
    Font dpcomic_B_64 = new Font("dpcomic", Font.BOLD, 64);
    Font dpcomic_P_32 = new Font("dpcomic", Font.PLAIN, 32);

    private enum STATE{
        Game,
        Menu,
        TopScore,
    }
    private STATE State=STATE.Menu;
    Timer timer;
    Random random;

    public GamePanel() {
        loadHighScore();
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH + 1, SCREEN_HEIGHT + 25));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame(){
        if(State==STATE.Game){
            newFood();
            running = true;
            timer = new Timer(DELAY, this);
            timer.start();
        }
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        if (State==STATE.Menu) {
            mainMenu(g);
        } else if (State==STATE.TopScore) {
            topScore(g);
        } else if(!running) {
            gameOver(g);
        }
        else {
            for (int i = 0; i <= SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(9, 78, 9));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(81, 171, 81));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            String Score = "Score : " + (foodEaten * 100)+"   HighScore: "+highScore;
            g.setFont(dpcomic_P_24);
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(Score, (SCREEN_WIDTH - metrics.stringWidth(Score)) / 2, SCREEN_HEIGHT + 20);
        }
    }
    public void newFood(){
        foodX=random.nextInt((SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        foodY=random.nextInt((SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }
    public void move(){
        for(int i=bodyParts;i>0;i--){
            x[i]=x[i-1];
            y[i]=y[i-1];
        }
        switch (direction){
            case 'U':
                y[0]=y[0]-UNIT_SIZE;
                break;
            case 'D':
                y[0]=y[0]+UNIT_SIZE;
                break;
            case 'L':
                x[0]=x[0]-UNIT_SIZE;
                break;
            case 'R':
                x[0]=x[0]+UNIT_SIZE;
                break;
        }
    }
    public void checkFood(){
        if((x[0]==foodX)&&(y[0]==foodY)){
            bodyParts++;
            foodEaten++;
            newFood();
        }
    }
    public void checkCollisions(){

        for(int i=bodyParts;i>0;i--){
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        if(x[0]<0){
            x[0]=SCREEN_WIDTH-UNIT_SIZE;
        }
        if(x[0]>=SCREEN_WIDTH){
            x[0]=0;
        }
        if(y[0]<0){
            y[0]=SCREEN_HEIGHT-UNIT_SIZE;
        }
        if(y[0]>=SCREEN_HEIGHT){
            y[0]=0;
        }

    }
    public void gameOver(Graphics g){
        String over = "Game Over";
        String score = "Score : " + (foodEaten * 100);
        String button1 = "Push R to Reset";
        String button2 = "Push M for Menu";
        this.setBackground(Color.BLACK);
        g.setColor(Color.red);
        g.setFont(dpcomic_B_64);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(over, (SCREEN_WIDTH - metrics.stringWidth(over)) / 2, SCREEN_HEIGHT / 2 - 3 * UNIT_SIZE);
        g.drawString(score, (SCREEN_WIDTH - metrics.stringWidth(score)) / 2, SCREEN_HEIGHT / 2);
        g.setFont(dpcomic_P_32);
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString(button1, (SCREEN_WIDTH - metrics1.stringWidth(button1)) / 2, SCREEN_HEIGHT / 2 + 2 * UNIT_SIZE);
        g.drawString(button2, (SCREEN_WIDTH - metrics1.stringWidth(button2)) / 2, SCREEN_HEIGHT / 2 + 4 * UNIT_SIZE);
    }
    public void mainMenu(Graphics g){
        String menu = "Snake Game";
        String button1 = "Push R to Play the Game";
        String button3 = "Push T to view Top Score";
        this.setBackground(Color.BLACK);
        g.setColor(Color.red);
        g.setFont(dpcomic_B_64);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(menu, (SCREEN_WIDTH - metrics.stringWidth(menu)) / 2, SCREEN_HEIGHT / 2 - 2 * UNIT_SIZE);
        g.setFont(dpcomic_P_32);
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString(button1, (SCREEN_WIDTH - metrics1.stringWidth(button1)) / 2, SCREEN_HEIGHT / 2 + 2 * UNIT_SIZE);
        g.drawString(button3, (SCREEN_WIDTH - metrics1.stringWidth(button3)) / 2, SCREEN_HEIGHT / 2 + 4 * UNIT_SIZE);
    }
    public void topScore(Graphics g){
        g.setFont(dpcomic_B_64);
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.setColor(Color.red);
        g.drawString("Top Score " + highScore, (SCREEN_WIDTH - metrics.stringWidth("Top Score " + highScore)) / 2, SCREEN_HEIGHT / 2 - 2 * UNIT_SIZE);
    }
    public void restartGame(){
        x[0]=0;
        y[0]=0;
        this.direction='R';
        this.foodEaten=0;
        this.bodyParts=3;
        if(this.timer!=null){
            this.timer.stop();
        }
    }
    public void saveNewScore() {
        int score=foodEaten*100;
        if (score>highScore){
            highScore=score;
            System.out.println(highScore);
            FileWriter fw;
            try {
                File file = new File("C:\\Users\\Kamil\\IdeaProjects\\Snake\\src\\score\\highscore.txt");
                fw = new FileWriter(file);
                fw.write(""+highScore);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void loadHighScore(){
        try {

            InputStream is = getClass().getResourceAsStream("/score/highscore.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            br.close();
            highScore=Integer.parseInt(line);

        }catch (Exception e){highScore=0;}
    }
    @Override
    public void actionPerformed(ActionEvent e) {
       if (running){
           move();
           checkFood();
           checkCollisions();
           saveNewScore();
       }
       repaint();
    }
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction !='R'){
                        direction='L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction !='L'){
                        direction='R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction !='D'){
                        direction='U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction !='U'){
                        direction='D';
                    }
                    break;
                case KeyEvent.VK_R:
                    if(!running){
                        State=STATE.Game;
                        restartGame();
                        startGame();
                    }
                    break;
                case KeyEvent.VK_M:
                    if(!running){
                        State=STATE.Menu;
                    }
                    break;
                case KeyEvent.VK_T:
                    if(!running){
                        State=STATE.TopScore;
                    }
                    break;
            }
        }
    }
}
