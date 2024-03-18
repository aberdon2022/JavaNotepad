import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class NotepadGUI extends JFrame {
    // file explorer
    private JFileChooser fileChooser;

    private JTextArea textArea;
    private File currentFile;

    // Swing's built in library to manage undo and redo functionalities
    private UndoManager undoManager;

    public NotepadGUI() {
        super("Notepad");
        setSize(400,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // file chooser setup
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/assets"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        undoManager = new UndoManager();
        addGuiComponents();
    }

    private void addGuiComponents() {
        addToolbar();

        // Area to type text into
        textArea = new JTextArea();
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                // adds each edit that we do in the text area (either adding or removing text)

                undoManager.addEdit(e.getEdit());
            }
        });
        add(textArea, BorderLayout.CENTER);
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        //Menu bar
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        //add menus
        menuBar.add(addFileMenu());
        menuBar.add(addEditMenu());

        add(toolBar, BorderLayout.NORTH);
    }

    private JMenu addFileMenu() {
        JMenu fileMenu = new JMenu("File");

        // "new" functionality - resets everything
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // reset title header
                setTitle("Notepad");

                // reset text area
                textArea.setText("");

                // reset current file
                currentFile = null;
            }
        });
        fileMenu.add(newMenuItem);

        // "open" functionality - open a text file
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open file explorer
                int res = fileChooser.showOpenDialog(NotepadGUI.this);

                if (res != JFileChooser.APPROVE_OPTION) {
                    return;
                }

               try {
                   // reset notepad
                   newMenuItem.doClick();

                   // get the selected file
                   File selectedFile = fileChooser.getSelectedFile();

                   // update current file
                   currentFile = selectedFile;

                   // update title header
                   setTitle(selectedFile.getName());

                   // read the file
                   FileReader fileReader = new FileReader(selectedFile);
                   BufferedReader bufferedReader = new BufferedReader(fileReader);

                   // Store the text
                   StringBuilder fileText = new StringBuilder();
                   String readText;
                   while ((readText = bufferedReader.readLine()) != null) {
                       fileText.append(readText + "\n");
                   }

                   // update text area guir
                   textArea.setText(fileText.toString());

               }catch (Exception e1) {
                   e1.printStackTrace();
               }
            }
        });
        fileMenu.add(openMenuItem);

        // "save as" functionality - creates a new text file and saves user text
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // open save dialog
               int res =  fileChooser.showSaveDialog(NotepadGUI.this);

               // continue to execute code only if user pressed the save button
               if (res != JFileChooser.APPROVE_OPTION) {
                   return;
               }

                try {
                    File selectedFile = fileChooser.getSelectedFile();

                    // append .txt to the file if it doesn't have the txt extension yet
                    String fileName = selectedFile.getName();

                    if (!fileName.substring(fileName.length() - 4).equalsIgnoreCase(".txt")) {
                        selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");
                    }

                    // create a new file
                    selectedFile.createNewFile();

                    // Write the user's text into the file that we just created
                    FileWriter fileWriter = new FileWriter(selectedFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    fileWriter.close();

                    // update the title header of gui to save text file
                    setTitle(fileName);

                    // update current file
                    currentFile = selectedFile;

                    //show display dialog
                    JOptionPane.showMessageDialog(NotepadGUI.this, "Saved File!");

                }catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        });
        fileMenu.add(saveAsMenuItem);

        // "save" functionality - saves text into current text file
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if the current file is null then perform save as functionality
                if (currentFile == null) {
                    saveAsMenuItem.doClick();
                }

                // if the user cancel a saving file, current file will be null

                if (currentFile == null) {
                    return;
                }

                try {
                    // write to current file
                    FileWriter fileWriter = new FileWriter(currentFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(textArea.getText());
                    bufferedWriter.close();
                    fileWriter.close();
                }catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        fileMenu.add(saveMenuItem);

        // "exit" functionality - ends program process
        JMenuItem exitMenuItem = new JMenuItem("Exit");

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // dispose of this gui
                NotepadGUI.this.dispose();
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu addEditMenu() {
        JMenu editmenu = new JMenu("Edit");

        JMenuItem undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if there are any edits that we can undo, then we undo them
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });
        editmenu.add(undoMenuItem);

        JMenuItem redoMenuItem = new JMenuItem("Redo");
        editmenu.add(redoMenuItem);

        return editmenu;
    }
}
