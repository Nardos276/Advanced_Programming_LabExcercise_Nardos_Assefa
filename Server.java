import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class Server {

    private static final int PORT = 5000;

    static Vector<ClientHandler> clients = new Vector<>();

    static JFrame frame;
    static JTextPane chatArea;
    static JTextField messageField;
    static JButton sendButton;

    public static void main(String[] args) {

        buildGUI();

        // 🔥 FIX 1: Run server in background thread
        new Thread(() -> runServer()).start();
    }

    // ---------------- SERVER LOOP ----------------
    public static void runServer() {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            appendText("Server started...");

            while (true) {

                Socket socket = serverSocket.accept();

                ClientHandler client = new ClientHandler(socket);

                clients.add(client);

                Thread thread = new Thread(client);

                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- GUI ----------------
    public static void buildGUI() {

        frame = new JFrame("Server Chat");

        chatArea = new JTextPane(); // FIXED NAME
        chatArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();

        sendButton = new JButton("Send");

        sendButton.addActionListener(e -> sendServerMessage());

        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // ---------------- SERVER SEND MESSAGE ----------------
    public static void sendServerMessage() {

        String msg = "SERVER: " + messageField.getText();

        broadcastMessage(msg);

        messageField.setText("");
    }

    // ---------------- TEXT BROADCAST ----------------
    public static void broadcastMessage(String message) {

        appendText(message);

        for (ClientHandler client : clients) {

            try {
                client.dos.writeUTF("TEXT");
                client.dos.writeUTF(message);
                client.dos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------- IMAGE BROADCAST ----------------
    public static void broadcastImage(
            String sender,
            String imageName,
            byte[] imageData
    ) {

        appendText(sender + " sent image: " + imageName);

        for (ClientHandler client : clients) {

            try {
                client.dos.writeUTF("IMAGE");
                client.dos.writeUTF(sender + "_" + imageName);
                client.dos.writeInt(imageData.length);
                client.dos.write(imageData);
                client.dos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ---------------- CLIENT HANDLER ----------------
    static class ClientHandler implements Runnable {

        Socket socket;
        DataInputStream dis;
        DataOutputStream dos;
        String username;

        public ClientHandler(Socket socket) {

            this.socket = socket;

            try {

                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                username = dis.readUTF();

                broadcastMessage("🟢 " + username + " joined the chat");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            while (true) {

                try {

                    String type = dis.readUTF();

                    if (type.equals("TEXT")) {

                        String message = dis.readUTF();
                        broadcastMessage(message);
                    }

                    else if (type.equals("IMAGE")) {

                        String imageName = dis.readUTF();

                        int size = dis.readInt();

                        byte[] imageData = new byte[size];

                        dis.readFully(imageData);

                        broadcastImage(username, imageName, imageData);
                    }

                } catch (IOException e) {

                    clients.remove(this);

                    broadcastMessage("🔴 " + username + " left the chat");

                    break;
                }
            }
        }
    }

    // ---------------- GUI TEXT APPEND ----------------
    public static void appendText(String text) {

        try {

            javax.swing.text.Document doc = chatArea.getDocument();

            doc.insertString(doc.getLength(), text + "\n", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}