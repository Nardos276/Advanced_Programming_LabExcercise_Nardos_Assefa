package NotePad;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;

import java.awt.event.*;

public class NotePad implements ActionListener {

    JFrame window;
    JTextArea textarea;
    JScrollPane scroll;

    JMenuBar menuBar;
    JMenu menuFile, menuEdit;

    JMenuItem menuFileNew, menuFileOpen, menuFileSave,
            menuFileSaveAs, menuFileClose,
            menuEditUndo, menuEditRedo;

    UndoManager undoManager = new UndoManager();

    Function_file file = new Function_file(this);

    public static void main(String[] args) {
        new NotePad();
    }

    public NotePad() {
        creatingWindow();
        setTextArea();
        setMenuBar();
        window.setVisible(true);
    }

    public void creatingWindow() {
        window = new JFrame("Notepad");
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setTextArea() {

        textarea = new JTextArea();

        // TRACK CHANGES FOR UNDO/REDO
        textarea.getDocument().addUndoableEditListener(
                new UndoableEditListener() {
                    public void undoableEditHappened(UndoableEditEvent e) {
                        undoManager.addEdit(e.getEdit());
                    }
                });

        scroll = new JScrollPane(
                textarea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        window.add(scroll);
    }

    public void setMenuBar() {

        menuBar = new JMenuBar();
        window.setJMenuBar(menuBar);

        // FILE MENU
        menuFile = new JMenu("File");
        menuBar.add(menuFile);

        // EDIT MENU
        menuEdit = new JMenu("Edit");
        menuBar.add(menuEdit);

        // NEW
        menuFileNew = new JMenuItem("New");
        menuFileNew.addActionListener(this);
        menuFileNew.setActionCommand("New");
        menuFile.add(menuFileNew);

        // OPEN
        menuFileOpen = new JMenuItem("Open");
        menuFileOpen.addActionListener(this);
        menuFileOpen.setActionCommand("Open");
        menuFile.add(menuFileOpen);

       // SAVE
menuFileSave = new JMenuItem("Save");
menuFileSave.addActionListener(this);
menuFileSave.setActionCommand("Save");
menuFile.add(menuFileSave);

// SAVE AS
menuFileSaveAs = new JMenuItem("Save As");
menuFileSaveAs.addActionListener(this);
menuFileSaveAs.setActionCommand("SaveAs");
menuFile.add(menuFileSaveAs);

        // CLOSE
        menuFileClose = new JMenuItem("Close");
        menuFileClose.addActionListener(this);
        menuFileClose.setActionCommand("Close");
        menuFile.add(menuFileClose);

        // UNDO
        menuEditUndo = new JMenuItem("Undo");
        menuEditUndo.addActionListener(this);
        menuEditUndo.setActionCommand("Undo");
        menuEdit.add(menuEditUndo);

        // REDO
        menuEditRedo = new JMenuItem("Redo");
        menuEditRedo.addActionListener(this);
        menuEditRedo.setActionCommand("Redo");
        menuEdit.add(menuEditRedo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        switch (command) {

            case "New":
                file.newFile();
                break;

            case "Open":
                file.openFile();
                break;

            case "Save":
                file.saveFile();
                break;

            case "SaveAs":
                file.saveAsFile();
                break;

            case "Close":
                file.closeFile();
                break;

            case "Undo":
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
                break;

            case "Redo":
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
                break;
        }
    }
}