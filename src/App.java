import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        //Board to play
        int boardWidth = 360;
        int boardHeight = 640;

        //Title for our window
        JFrame frame = new JFrame("Flappy Bird");

        //set the size
        frame.setSize(boardWidth, boardHeight);

        //place the window at the center
        frame.setLocationRelativeTo(null);

        //the user won't be able to resize the window(change the size)
        frame.setResizable(false);

        //when the user click the Close the program comes to an end
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //instance of flappyBird
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        
        //to not take into account the border of the window(the top part)
        frame.pack();

        //make sure its focus
        flappyBird.requestFocus();

        //make the frame visible
        frame.setVisible(true);
    }
}
