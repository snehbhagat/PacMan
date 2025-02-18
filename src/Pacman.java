import java.awt.*;
import java.awt.event.*;
import java.sql.Time;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel implements ActionListener , KeyListener {
    class Block{
        int x;
        int y;
        int width;
        int  height;
        Image image;

        int startX;
        int startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image , int x ,int y , int width , int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
        void updateDirection(char direction){
            char previousDireaction = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for(Block wall : walls){
                if(collision(this , wall)){
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = previousDireaction;
                    updateVelocity();
                }
            }
        }
        void updateVelocity(){
            if(this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -tileSize / 4;
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = tileSize / 4;
            }
            else if (this.direction == 'L') {
                this.velocityY = 0;
                this.velocityX = -tileSize / 4;
            }
            else if (this.direction == 'R') {
                this.velocityY = 0;
                this.velocityX = tileSize / 4;
            }
        }

    }

    private int rowCount = 21;
    private int colCount = 19;
    private int tileSize = 32;
    private int boardWidth = colCount * tileSize;
    private int boardHeight = rowCount * tileSize;
    private Image wallImage;
    private Image blueGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image orangeGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;
    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block>foods;
    HashSet<Block>walls;
    HashSet<Block>ghosts;
    Block pacman;

    Timer gameLoop;

    char [] directions = {'R' , 'U' , 'D' , 'L'};
    Random random = new Random();

    Pacman(){
        setPreferredSize(new Dimension(boardWidth , boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();

        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();

        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for(Block ghost : ghosts){
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        //how long it takes to start the timer , milliseconds gone between the frames
        gameLoop = new Timer(50 , this); // 20fps (1000/50)
        gameLoop.start();

        System.out.println(walls.size());
        System.out.println(foods.size());
        System.out.println(ghosts.size());
    }
    public void loadMap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for(int r = 0 ; r < rowCount ; r++){
            for(int c = 0 ; c < colCount ; c++){
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                if(tileMapChar == 'X'){
                    Block wall = new Block(wallImage , x , y , tileSize , tileSize);
                    walls.add(wall);
                }
                else if(tileMapChar == 'b'){
                    Block ghost = new Block(blueGhostImage , x , y , tileSize , tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'o'){
                    Block ghost = new Block(orangeGhostImage , x , y , tileSize , tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'p'){
                    Block ghost = new Block(pinkGhostImage , x , y , tileSize , tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'r'){
                    Block ghost = new Block(redGhostImage , x , y , tileSize , tileSize);
                    ghosts.add(ghost);
                }
                else if(tileMapChar == 'P'){
                    pacman = new Block(pacmanRightImage , x , y , tileSize , tileSize);
                }
                else if(tileMapChar == ' '){
                    Block food = new Block(null , x + 14 , y + 14 , 4 , 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(pacman.image , pacman.x, pacman.y, pacman.width , pacman.height, null);

        for(Block ghost : ghosts){
            g.drawImage(ghost.image , ghost.x , ghost.y , ghost.width , ghost.height , null);
        }

        for(Block wall : walls){
            g.drawImage(wall.image , wall.x , wall.y , wall.width , wall.height , null);
        }
        g.setColor(Color.WHITE);
        for(Block food : foods){
            g.fillRect(food.x , food.y , food.width , food.height);
        }
    }
    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;
        //check for wall collision
        for(Block wall : walls){
            if(collision(pacman , wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
            }
        }

        for(Block ghost : ghosts){
            if(ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            //check for ghost collision
            for(Block wall : walls){
                if(collision(wall , ghost) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }
    }
    public boolean collision(Block a , Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyPressed(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("Key Event : " + e.getKeyCode());
        if(e.getKeyCode() == KeyEvent.VK_UP){
            pacman.updateDirection('U');
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirection('D');
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            pacman.updateDirection('L');
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            pacman.updateDirection('R');
        }
        if(pacman.direction == 'U'){
            pacman.image = pacmanUpImage;
        }
        else if(pacman.direction == 'D'){
            pacman.image = pacmanDownImage;
        }
        else if(pacman.direction == 'L'){
            pacman.image = pacmanLeftImage;
        }
        else if(pacman.direction == 'R'){
            pacman.image = pacmanRightImage;
        }
    }

}
