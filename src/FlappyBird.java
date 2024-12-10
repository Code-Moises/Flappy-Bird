import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

            
public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    //Board
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    //the bird is going to move in 1/8 X and 1/2 Y of the screen up and down in the middle of the board 
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;

    //bird scale
    int birdWidth = 34;
    int birdHeight = 24;

    //to hold the values and make them easier to use
    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img){
            this.img = img;
        }
    }

    //pipe
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;     //scaled 1/6 the actual img is 384
    int pipeHeight = 512;

    //same reason as the bird
    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;

        //check wether the bird has passed the pipe
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }

    //game logic for the constructor
    Bird bird;

    //Pipe move
    int velocityX = -4; //move pipes to the left (simulates the bird move to the right)

    //Bird move
    int velocityY = 0; //up-down

    //gravity
    int gravity = 1;

    //store the pipes
    ArrayList<Pipe>pipes;

    //Random for the position of the pipes
    Random random = new Random();

    //Loop for the game imgs
    Timer gameLoop;

    //Loop for the pipes
    Timer placePipesTimer;
    
    //game over
    boolean gameOver = false;

    //score 
    double score = 0;



    //constructor
    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        //to make sure this JPanel(FlappyBird gets the KeyEvents)
        setFocusable(true);

        //check the 3 functions of the KeyPress 
        addKeyListener(this);
        
        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //create the bird and pass the img
        bird = new Bird(birdImg);

        //array list of pipes inside FlappyBird
        pipes = new ArrayList<Pipe>();

        /*place Pipes timer. new Timer(1500(every 1.5 seg is gonna call the function) (function)) 
        so a new ActionListener is needed*/
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes(); //now we have a Timer that we'll call placePipes() every 1500 milisegs
            }
        });
        //start pipes placer Loop
        placePipesTimer.start();

        //game timer        1000ms=1seg; this refers to FlappyBird class
        gameLoop = new Timer(1000/60, this); //1000/60 = 16.6

        //start gameLoop
        gameLoop.start();
    }

    //create and place the Pipes
    public void placePipes(){

        //(0-1) * pipeHeight/2 --> (0-256)
        //0 - 128 - (0-256) ==> 1/4 pipeHeight <--> 3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));

        //space in beetwen pipes
        int openingSpace = boardHeight/4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;    
        pipes.add(bottomPipe);
    }



    //lets draw with JPanel functions
    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, 0, 0, boardHeight, boardHeight, null);

        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white); //color
        g.setFont(new Font("sans", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 68, boardHeight/2);
        }else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void paintComponent(Graphics g){

        //super refers to the inherit class (JPanel)
        super.paintComponent(g);
        draw(g);
    }

    //move function
     public void move(){
        //apply gravity to the bird
        velocityY += gravity;

        //bird move
        bird.y += velocityY;

        //not let the bird go over the top of the window
        bird.y = Math.max(bird.y, 0);

        //pipes move
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            //update velocity
            pipe.x += velocityX;

            //if the bird passes the right size of the pipe
            if (!pipe.passed && bird.x > pipe.x+pipe.width) {
                pipe.passed = true;
                
                //because there are to pipes(top-bottom) 0.5+0.5
                score += 0.5;
            }


            //game over (collision)
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        //game over (fall)
        if (bird.y > boardHeight) {
            gameOver = true;
        }
     }

     //if the bird collides with the pipes
     public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
     }



    @Override
    public void actionPerformed(ActionEvent e) {
        //before repainting i want it to update the bird position
        move();

        //This will be the action performed every 16 ms
        repaint();

        //game over
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    
    @Override
    public void keyPressed(KeyEvent e) {//it could be any key arrow, f5, space (whenever you press on a key)

        //if the key pressed is the space var key 
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            
            //restar game
            if (gameOver) {
                //restart the game by resetting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }
    
    //not using
    @Override
    public void keyTyped(KeyEvent e) {/*just for a key that has a character*/}
    @Override
    public void keyReleased(KeyEvent e) {/*When you press on a key and let go the key backs up */}
}
