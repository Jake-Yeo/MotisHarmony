/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import model.Accounts;
import model.AlarmClock;

/**
 * FXML Controller class
 *
 * @author Jake Yeo
 */
public class AlarmClockDialogEditorController implements Initializable {

    private AlarmClock alarmClock;
    @FXML
    private ComboBox<Integer> hourComboBox;
    @FXML
    private ComboBox<Integer> minuteComboBox;
    @FXML
    private ChoiceBox<String> amOrPmChoiceBox;
    @FXML
    private RadioButton enableAlarmRadioButton;
    @FXML
    private Text alarmClockRingTimeText;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private Button showHourChoiceBox;
    @FXML
    private Button showMinuteChoiceBox;
    @FXML
    private Button showAmOrPmComboFBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        hourComboBox.getStylesheets().add("/css/comboBox.css");
        minuteComboBox.getStylesheets().add("/css/comboBox.css");
        amOrPmChoiceBox.getStylesheets().add("/css/choiceBox.css");
        AlarmClock accAlarmClock = Accounts.getLoggedInAccount().getSettingsObject().getAlarmClock();
        alarmClock = new AlarmClock(accAlarmClock.getHour(), accAlarmClock.getMinute(), accAlarmClock.getAmOrPm());
        alarmClock.setEnableAlarm(accAlarmClock.getEnableAlarm());
        AlarmClock.setAlarmCurrentlyUsing(alarmClock);
        hourComboBox.setVisible(false);
        hourComboBox.setMaxWidth(74);
        hourComboBox.setVisibleRowCount(3);
        minuteComboBox.setVisible(false);
        minuteComboBox.setMaxWidth(74);
        minuteComboBox.setVisibleRowCount(3);
        amOrPmChoiceBox.setVisible(false);
        amOrPmChoiceBox.setMaxWidth(74);
        for (int i = 1; i <= 12; i++) {
            hourComboBox.getItems().add(i);
        }
        for (int i = 0; i <= 59; i++) {
            minuteComboBox.getItems().add(i);
        }
        amOrPmChoiceBox.setOnAction(e -> onAmOrPmChoiceBoxPressed());
        amOrPmChoiceBox.getItems().addAll("AM", "PM");
        String alarmClockMinuteString = alarmClock.getMinute() + "";
        if (alarmClockMinuteString.length() == 1) {
            alarmClockMinuteString = "0" + alarmClockMinuteString;
        }
        enableAlarmRadioButton.setSelected(alarmClock.getEnableAlarm());
        System.out.println(Accounts.getLoggedInAccount().getSettingsObject().getAlarmClock().getAmOrPm());
        alarmClockRingTimeText.setText(alarmClock.getHour() + ":" + alarmClockMinuteString + " " + alarmClock.getAmOrPm());

        //Select the correct options which are saved in the settings. I'm unable to find a way to do this with comboboxes however.
        amOrPmChoiceBox.getSelectionModel().select(accAlarmClock.getAmOrPm());
    }

    private void updateAlarmTimeText() {
        String alarmClockMinuteString = alarmClock.getMinute() + "";
        if (alarmClockMinuteString.length() == 1) {
            alarmClockMinuteString = "0" + alarmClockMinuteString;
        }
        alarmClockRingTimeText.setText(alarmClock.getHour() + ":" + alarmClockMinuteString + " " + alarmClock.getAmOrPm());
    }

    @FXML
    private void onEnableAlarmRadioButtonPressed() throws ParseException {
        AlarmClock.getAlarmCurrentlyUsing().setEnableAlarm(enableAlarmRadioButton.isSelected());
    }

    @FXML
    private void onHourComboBoxPressed() {
        alarmClock.setHour(hourComboBox.getValue());
        updateAlarmTimeText();
    }

    @FXML
    private void onMinuteComboBoxPressed() {
        alarmClock.setMinute(minuteComboBox.getValue());
        updateAlarmTimeText();
    }

    @FXML
    private void onAmOrPmChoiceBoxPressed() {
        alarmClock.setAmOrPm(amOrPmChoiceBox.getValue());
        updateAlarmTimeText();
    }

    @FXML
    private void showHourComboBox() {
        hourComboBox.show();
    }

    @FXML
    private void showMinuteComboBox() {
        minuteComboBox.show();
    }

    @FXML
    private void showAmOrPmChoiceFBox() {
        amOrPmChoiceBox.show();
    }

}
