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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import model.Accounts;
import model.AlarmClock;
import model.SleepAlarm;

/**
 * FXML Controller class
 *
 * @author 1100007967
 */
public class SleepAlarmDialogEditorController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private SleepAlarm sleepAlarm;
    @FXML
    private ComboBox<Integer> hourComboBox;
    @FXML
    private ComboBox<Integer> minuteComboBox;
    @FXML
    private RadioButton enableAlarmRadioButton;
    @FXML
    private Text sleepAlarmStopTimeText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SleepAlarm accSleepAlarm = Accounts.getLoggedInAccount().getSettingsObject().getSleepAlarm();
        sleepAlarm = new SleepAlarm(accSleepAlarm.getHour(), accSleepAlarm.getMinute());
        sleepAlarm.setEnableAlarm(accSleepAlarm.getEnableAlarm());
        AlarmClock.setAlarmCurrentlyUsing(sleepAlarm);
        hourComboBox.setVisible(false);
        hourComboBox.setMaxWidth(74);
        hourComboBox.setVisibleRowCount(3);
        minuteComboBox.setVisible(false);
        minuteComboBox.setMaxWidth(74);
        minuteComboBox.setVisibleRowCount(3);
        for (int i = 1; i <= 12; i++) {
            hourComboBox.getItems().add(i);
        }
        for (int i = 0; i <= 59; i++) {
            minuteComboBox.getItems().add(i);
        }
        updateAlarmTimeText();

    }

    private void updateAlarmTimeText() {
        String minuteString = sleepAlarm.getMinute() + "";
        String hour = " hr";
        String min = " min";
        if (sleepAlarm.getMinute() / 10 == 0) {
            minuteString = 0 + "" + sleepAlarm.getMinute();
        }
        if (sleepAlarm.getMinute() != 1) {
            min += "s";
        }
        if (sleepAlarm.getHour() != 1) {
            hour += "s";
        }
        sleepAlarmStopTimeText.setText(sleepAlarm.getHour() + hour + " " + minuteString + min);
    }

    @FXML
    private void onEnableAlarmRadioButtonPressed() throws ParseException {
        AlarmClock.getAlarmCurrentlyUsing().setEnableAlarm(enableAlarmRadioButton.isSelected());
    }

    @FXML
    private void onHourComboBoxPressed() {
        sleepAlarm.setHour(hourComboBox.getValue());
        updateAlarmTimeText();
    }

    @FXML
    private void onMinuteComboBoxPressed() {
        sleepAlarm.setMinute(minuteComboBox.getValue());
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
