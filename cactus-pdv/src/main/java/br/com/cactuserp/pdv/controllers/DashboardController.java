package br.com.cactuserp.pdv.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Text user_name_lbl;
    public Label login_date_lbl;
    public Label checking_bal_lbl;
    public Label checking_acc_num_lbl;
    public Label savings_bal_lbl;
    public Label savings_acc_num_lbl;
    public Label income_lbl;
    public Label expenses_lbl;
    public ListView transaction_listview;
    public TextField payee_fld;
    public TextField amount_fld;
    public TextArea message_fld;
    public Button send_money_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
