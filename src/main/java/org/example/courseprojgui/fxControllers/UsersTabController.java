package org.example.courseprojgui.fxControllers;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.example.courseprojgui.hibernate.GenericHibernate;
import org.example.courseprojgui.hibernate.HibernateShop;
import org.example.courseprojgui.model.Customer;
import org.example.courseprojgui.model.Manager;
import org.example.courseprojgui.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


@Getter
@Setter
public class UsersTabController implements Initializable {

    public Text userNameText;
    public Text userSurnameText;
    public Text userShipText;
    public Text userBillText;
    public Text userCardText;
    public Text userBirthText;
    public TextField userNameField;
    public TextField userSurnameField;
    public TextField userShippingAdrField;
    public TextField userBillingAdrField;
    public TextField userCardNumField;
    public TextField userBirthDateField;
    private ObservableList<ManagerTableParameters> data = FXCollections.observableArrayList();
    public TableView<ManagerTableParameters> managerTable;
    public TableColumn<ManagerTableParameters, Integer> managerTableColumnId;
    public TableColumn<ManagerTableParameters, String> managerTableColumnLogin;
    public TableColumn<ManagerTableParameters, String> managerTableColumnName;
    public TableColumn<ManagerTableParameters, String> managerTableColumnSurname;

    public Text userEditingStatusText;
    public AnchorPane userCreationAnchorPaneBase;
    public Button createNewUserButton;
    public Text userStatusText;
    public TextField loginTextField;
    public PasswordField passwordTextField;
    public Button submitEnterInfoButton;
    public Button logOffButton;
    public Button editButton;
    public Button saveChangesButton;

    @Getter
    private MainController mainController;
    @Getter
    private static UsersTabController instance;
    private boolean isAdminNow = false;
    private GenericHibernate genericHibernate;
    private ShopTabController shopTabController;
    private User currentUser;


    public void addNewUserToList (User user) {
       genericHibernate.create(user);
    }

    public UsersTabController() {
        instance = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainController = MainController.getInstance();
        shopTabController = ShopTabController.getInstance();
        genericHibernate = new GenericHibernate(mainController.getEntityManagerFactory());

        isEditable(false);
        openLoginFields(true);

        managerTable.setEditable(true);
        managerTableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        managerTableColumnLogin.setCellValueFactory(new PropertyValueFactory<>("Login"));

    }

    @FXML
    private void fillManagerTable() {
        List<Manager> managerList = genericHibernate.getAllRecords(Manager.class);
        data.clear();
        for (Manager m:managerList) {
            ManagerTableParameters managerTableParameters = new ManagerTableParameters();
            managerTableParameters.setId(m.getId());
            managerTableParameters.setLogin(m.getLogin());
            managerTableParameters.setName(m.getName());
            managerTableParameters.setSurname(m.getSurname());
            data.add(managerTableParameters);
        }
        managerTable.setItems(data);
    }

    @FXML private void openCreateNewUserMenu() {
        try {

            FXMLLoader loader = new FXMLLoader(ErrorNotFilledPopUp.class.getResource("/org/example/courseprojgui/userCreationWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Customer");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    private void openManagerField(boolean val) {
        userStatusText.setText("Manager");

        editButton.setVisible(!val);
        saveChangesButton.setVisible(!val);
        createNewUserButton.setVisible(!val);
        loginTextField.setVisible(!val);
        passwordTextField.setVisible(!val);
        submitEnterInfoButton.setVisible(!val);
        userEditingStatusText.setVisible(!val);

        managerTable.setVisible(val);
        logOffButton.setVisible(val);
    }

    private void openCustomerField(boolean val) {
        userStatusText.setText("Customer");

        createNewUserButton.setVisible(!val);
        loginTextField.setVisible(!val);
        passwordTextField.setVisible(!val);
        submitEnterInfoButton.setVisible(!val);
        managerTable.setVisible(!val);
        userEditingStatusText.setVisible(!val);

        editButton.setVisible(val);
        saveChangesButton.setVisible(val);
        logOffButton.setVisible(val);
        userNameText.setVisible(val);
        userSurnameText.setVisible(val);
        userShipText.setVisible(val);
        userBillText.setVisible(val);
        userCardText.setVisible(val);
        userBirthText.setVisible(val);
        userNameField.setVisible(val);
        userSurnameField.setVisible(val);
        userShippingAdrField.setVisible(val);
        userBillingAdrField.setVisible(val);
        userCardNumField.setVisible(val);
        userBirthDateField.setVisible(val);

        userNameField.setText(currentUser.getName());
        userSurnameField.setText(currentUser.getSurname());
        userShippingAdrField.setText(((Customer) currentUser).getShippingAddress());
        userBillingAdrField.setText(((Customer) currentUser).getBillingAddress());
        userCardNumField.setText(((Customer) currentUser).getCardNumber());
        userBirthDateField.setText(String.valueOf(((Customer) currentUser).getBirthDate()));
    }

    private void openLoginFields(boolean val) {
        userStatusText.setText("Login");

        createNewUserButton.setVisible(val);
        loginTextField.setVisible(val);
        passwordTextField.setVisible(val);
        submitEnterInfoButton.setVisible(val);

        logOffButton.setVisible(!val);
        editButton.setVisible(!val);
        saveChangesButton.setVisible(!val);
        userEditingStatusText.setVisible(!val);
        managerTable.setVisible(!val);

        userNameText.setVisible(!val);
        userSurnameText.setVisible(!val);
        userShipText.setVisible(!val);
        userBillText.setVisible(!val);
        userCardText.setVisible(!val);
        userBirthText.setVisible(!val);
        userNameField.setVisible(!val);
        userSurnameField.setVisible(!val);
        userShippingAdrField.setVisible(!val);
        userBillingAdrField.setVisible(!val);
        userCardNumField.setVisible(!val);
        userBirthDateField.setVisible(!val);
    }

    public void checkAndLogin() throws IOException {
        HibernateShop hibernateShop = new HibernateShop(mainController.getEntityManagerFactory());
        User user = hibernateShop.getUserByCredentials(loginTextField.getText(), passwordTextField.getText());
        currentUser = user;
        if(user != null) {
            if (user instanceof Manager) {
                openManagerField(true);
                mainController.openAllTabs(true);
            }else if (user instanceof Customer) {
                openCustomerField(true);
                mainController.openAllTabs(false);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("System message");
            alert.setHeaderText("Error during login");
            alert.setContentText("No such user");
            alert.showAndWait();
        }
    }

    public void logOff() {
        mainController.closeAllTabs();
        openLoginFields(true);
        currentUser=null;
        loginTextField.clear();
        passwordTextField.clear();
    }

    private void isEditable(boolean val) {
        userNameField.setEditable(val);
        userSurnameField.setEditable(val);
        userShippingAdrField.setEditable(val);
        userBillingAdrField.setEditable(val);
        userCardNumField.setEditable(val);
        userBirthDateField.setEditable(val);
    }

    @FXML private void startEditInfo() {
        userEditingStatusText.setVisible(true);
        userEditingStatusText.setText("You can change user info");
        isEditable(true);
    }

    @FXML private void saveEditInfo() {

            isEditable(false);
            currentUser.setName(userNameField.getText());
            currentUser.setSurname(userSurnameField.getText());
            ((Customer) currentUser).setBillingAddress(userBillingAdrField.getText());
            ((Customer) currentUser).setShippingAddress(userShippingAdrField.getText());
            ((Customer) currentUser).setCardNumber(userCardNumField.getText());
            ((Customer) currentUser).setBirthDate(userBirthDateField.getText());
            genericHibernate.update(currentUser);

        userEditingStatusText.setText("Saved");
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> userEditingStatusText.setVisible(false));
        pause.play();
    }


}

