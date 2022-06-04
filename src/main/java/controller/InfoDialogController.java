/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.google.common.io.Resources;
import java.awt.MouseInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class InfoDialogController implements Initializable {

    @FXML
    private TextField infoSearchField;
    @FXML
    private TextArea infoTextArea;
    @FXML
    private ListView<String> questionsListView;
    private InfoParser ip;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            ip = new InfoParser();
        } catch (IOException ex) {
            Logger.getLogger(InfoDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
        questionsListView.getItems().addAll(ip.getAllInfo().keySet());
        Collections.sort(questionsListView.getItems());
    }

    @FXML
    public void updateInfoTextArea(MouseEvent e) throws Exception {
        if (questionsListView.getSelectionModel().getSelectedIndex() != -1) {
            infoTextArea.setText(ip.getAllInfo().get(questionsListView.getSelectionModel().getSelectedItem()));
        }
    }

    @FXML
    private void searchTextFieldWhenTyped(KeyEvent e) {
        //Search everytime the text input is changed
        questionsListView.getItems().clear();
        questionsListView.getItems().addAll(ip.getValidSearchStrings(infoSearchField.getText()));
    }

    //I heard you're supposed to use a private class if it only has a very specific purpose
    private static class InfoParser {

        final private String DELIMITER = ":::::::::::::::::::::::::::::::::::";
        private HashMap<String, String> questionsAndAnswers;

        public InfoParser() throws IOException {
            questionsAndAnswers = parseInfo();
        }

        public HashMap<String, String> getAllInfo() {
            return questionsAndAnswers;
        }

        public Set<String> getValidSearchStrings(String searchQuery) {
            searchQuery.toLowerCase();
            Set<String> validSearchStrings = new HashSet<String>();
            for (String s : questionsAndAnswers.keySet()) {
                if (s.toLowerCase().contains(searchQuery)) {
                    validSearchStrings.add(s);
                }
            }
            return validSearchStrings;
        }

        private HashMap<String, String> parseInfo() throws FileNotFoundException, IOException {
            HashMap<String, String> questionsAndAnswers = new HashMap<>();
            BufferedReader txtReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/txt/info.txt"), "UTF-8"));
            String info = "";
            for (String line; (line = txtReader.readLine()) != null;) {
                info += line + "\n";
            }
            System.out.println(info);
            Scanner sc = new Scanner(info);
            sc.useDelimiter(DELIMITER);
            while (sc.hasNext()) {
                questionsAndAnswers.put(sc.next().trim(), sc.next().trim());
            }
            return questionsAndAnswers;

        }
    }

}
