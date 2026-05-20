package NotePad;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class Function_file {
    NotePad gui;
    String fileName;
    String filePath;
    public Function_file(NotePad gui) {
        this.gui = gui;
    }
    public void newFile() {
        gui.textarea.setText("");
        gui.window.setTitle("New");
        fileName = null;
        filePath = null;
    }
    public void openFile() {

    FileDialog fd = new FileDialog(gui.window, "Open", FileDialog.LOAD);
    fd.setVisible(true);

    if (fd.getFile() != null) {

        fileName = fd.getFile();
        filePath = fd.getDirectory();

        gui.window.setTitle(fileName);

        try {

            BufferedReader br =
                    new BufferedReader(
                            new FileReader(filePath + fileName));

            gui.textarea.setText("");

            String line;

            while ((line = br.readLine()) != null) {

                gui.textarea.append(line + "\n");
            }

            br.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
    public void saveFile() {

        if (fileName == null) {
            saveAsFile();
        } else {
            try {
                FileWriter fw = new FileWriter(filePath + "/" + fileName);
                fw.write(gui.textarea.getText());
                gui.window.setTitle(fileName);
                fw.close();
            }
           catch (Exception e) {
            System.out.println("Error");
        } }
    }
    public void saveAsFile() {
        FileDialog fd = new FileDialog(gui.window, "Save", FileDialog.SAVE);
        fd.setVisible(true);

        if (fd.getFile() != null) {
            fileName = fd.getFile();
            filePath = fd.getDirectory();
            gui.window.setTitle(fileName);
        }
        try {
            FileWriter fw = new FileWriter(filePath +"/" + fileName);
            fw.write(gui.textarea.getText());
            fw.close();
        } catch(Exception e){
            System.out.println("Error");
        }
    }
    public void closeFile() {
        System.exit(0);
    }


}
