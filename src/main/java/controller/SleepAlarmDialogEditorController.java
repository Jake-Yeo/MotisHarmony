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
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;
import model.Accounts;
import model.AlarmClock;
import model.SleepTimer;

/**
 * FXML Controller class
 *
 * @author 1100007967
 */
public class SleepAlarmDialogEditorController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private SleepTimer sleepTimer;
    @FXML
    private ComboBox<Integer> hourComboBox;
    @FXML
    private ComboBox<Integer> minuteComboBox;
    private RadioButton enableAlarmRadioButton;
    @FXML
    private Text sleepTimerStopTimeText;
    @FXML
    private Button showHourChoiceBox;
    @FXML
    private Button showMinuteChoiceBox;
    @FXML
    private ToggleButton toggleTimerButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SleepTimer accSleepTimer = Accounts.getLoggedInAccount().getSettingsObject().getSleepTimer();
        sleepTimer = new SleepTimer(accSleepTimer.getHour(), accSleepTimer.getMinute());
        sleepTimer.setEnableTimer(accSleepTimer.getEnableTimer());
        SleepTimer.setTimerCurrentlyUsing(sleepTimer);
        hourComboBox.setVisible(false);
        hourComboBox.setMaxWidth(74);
        hourComboBox.setVisibleRowCount(3);
        minuteComboBox.setVisible(false);
        minuteComboBox.setMaxWidth(74);
        minuteComboBox.setVisibleRowCount(3);
        for (int i = 0; i <= 12; i++) {
            hourComboBox.getItems().add(i);
        }
        for (int i = 0; i <= 59; i++) {
            minuteComboBox.getItems().add(i);
        }
        toggleTimerButton.setSelected(sleepTimer.getEnableTimer());
        if (sleepTimer.getEnableTimer()) {
            toggleTimerButton.setText("Stop Timer");
        } else {
            toggleTimerButton.setText("Start Timer");
        }
        updateAlarmTimeText();

    }

    private void updateAlarmTimeText() {
        String minuteString = sleepTimer.getMinute() + "";
        String hour = " hr";
        String min = " min";
        if (sleepTimer.getMinute() / 10 == 0) {
            minuteString = 0 + "" + sleepTimer.getMinute();
        }
        if (sleepTimer.getMinute() != 1) {
            min += "s";
        }
        if (sleepTimer.getHour() != 1) {
            hour += "s";
        }
        sleepTimerStopTimeText.setText(sleepTimer.getHour() + hour + " " + minuteString + min);
    }

    private void onEnableAlarmRadioButtonPressed() throws ParseException {
        AlarmClock.getAlarmCurrentlyUsing().setEnableAlarm(enableAlarmRadioButton.isSelected());
    }

    @FXML
    private void onToggleButtonPressed() {
        sleepTimer.setEnableTimer(!sleepTimer.getEnableTimer());
        if (sleepTimer.getEnableTimer()) {
            toggleTimerButton.setText("Stop Timer");
        } else {
            toggleTimerButton.setText("Start Timer");
        }
    }

    @FXML
    private void onHourComboBoxPressed() {
        sleepTimer.setHour(hourComboBox.getValue());
        updateAlarmTimeText();
    }

    @FXML
    private void onMinuteComboBoxPressed() {
        sleepTimer.setMinute(minuteComboBox.getValue());
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

}
