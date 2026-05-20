package Chat_App;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;

public class Client {

    JFrame frame;
    JTextPane chatPane;
    JTextField input;

    DataInputStream dis;
    DataOutputStream dos;

    String username;

    public Client() {

        username = JOptionPane.showInputDialog("Enter username");

        buildGUI();

        try {

            Socket socket = new Socket("localhost", 5000);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            dos.writeUTF(username);
            dos.flush();

            new Thread(this::receive).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void buildGUI() {

        frame = new JFrame(username);

        chatPane = new JTextPane();
        chatPane.setEditable(false);

        input = new JTextField();

        JButton send = new JButton("Send");
        JButton img = new JButton("Image");

        send.addActionListener(e -> sendText());
        img.addActionListener(e -> sendImage());

        JPanel bottom = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel();
        buttons.add(send);
        buttons.add(img);

        bottom.add(input, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.EAST);

        frame.add(new JScrollPane(chatPane), BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void sendText() {

        try {

            String msg = username + ": " + input.getText();

            dos.writeUTF("TEXT");
            dos.writeUTF(msg);
            dos.flush();

            input.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendImage() {

        try {

            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {

                File file = chooser.getSelectedFile();

                FileInputStream fis = new FileInputStream(file);

                byte[] data = fis.readAllBytes();

                dos.writeUTF("IMAGE");
                dos.writeUTF(file.getName());
                dos.writeInt(data.length);
                dos.write(data);
                dos.flush();

                fis.close();

                appendText("You sent:");
                appendImage(file.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void receive() {

        while (true) {

            try {

                String type = dis.readUTF();

                if (type.equals("TEXT")) {

                    String msg = dis.readUTF();
                    appendText(msg);
                }

                else if (type.equals("IMAGE")) {

                    String name = dis.readUTF();
                    int size = dis.readInt();

                    byte[] data = new byte[size];
                    dis.readFully(data);

                    File folder = new File("received");
                    if (!folder.exists()) folder.mkdir();

                    File file = new File(folder, name);

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.close();

                    appendText("Image received:");
                    appendImage(file.getAbsolutePath());
                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    void appendText(String text) {

        try {
            Document doc = chatPane.getDocument();
            doc.insertString(doc.getLength(), text + "\n", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void appendImage(String path) {

    try {

        // Move cursor to end
        chatPane.setCaretPosition(
                chatPane.getDocument().getLength()
        );

        ImageIcon icon = new ImageIcon(path);

        Image img = icon.getImage().getScaledInstance(
                200,
                200,
                Image.SCALE_SMOOTH
        );

        icon = new ImageIcon(img);

        chatPane.insertIcon(icon);

        Document doc = chatPane.getDocument();

        doc.insertString(
                doc.getLength(),
                "\n",
                null
        );

    } catch (Exception e) {

        e.printStackTrace();
    }
}

    public static void main(String[] args) {
        new Client();
    }
}