import javax.swing.JFrame;
public class GUI {
    JFrame window;
    public static void main(String[] args) {
        new GUI();
    }
    public GUI(){
creatingWindow();
    }
    public void creatingWindow() {
        window= new JFrame("Notepad");
        window.setSize(400,300);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

    }
}
