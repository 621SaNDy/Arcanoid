import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Arkanoid");
//        GamePanel gamePanel = new GamePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}