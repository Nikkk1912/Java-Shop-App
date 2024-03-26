package org.example.courseprojgui.fxControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.example.courseprojgui.hibernate.GenericHibernate;
import org.example.courseprojgui.model.Manager;
import org.example.courseprojgui.model.User;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


@Getter
@Setter
public class UsersTabController implements Initializable {
    public Text userEditingStatusText;
    public AnchorPane userCreationAnchorPaneBase;
    public Button createNewUserButton;
    public TableView<ManagerTableParameters> managerTable;
    public TableColumn<ManagerTableParameters, Integer> managerTableColumnId;
    public TableColumn<ManagerTableParameters, String> managerTableColumnLogin;
    public Text userStatusText;
    public TextField loginTextField;
    public TextField passwordTextField1;
    public Button submitEnterInfoButton;
    public Button logOffButton;
    public Button editButton;
    public Button saveChangesButton;
    public TableColumn managerTableColumnName;
    public TableColumn managerTableColumnSurname;
    @Getter
    private MainController mainController;
    @Getter
    private static UsersTabController instance;
    private boolean isAdminNow = false;
    private GenericHibernate genericHibernate;
    private ShopTabController shopTabController;
    private ObservableList<ManagerTableParameters> data = FXCollections.observableArrayList();

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
        userEditingStatusText.setVisible(false);
        managerTable.setEditable(true);
        managerTableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        managerTableColumnLogin.setCellValueFactory(new PropertyValueFactory<>("Login"));

    }

    private void fillManagerTable() {
        List<Manager> managerList = genericHibernate.getAllRecords(Manager.class);
        for (Manager m:managerList) {
            ManagerTableParameters managerTableParameters = new ManagerTableParameters();
            managerTableParameters.setId(m.getId());
            managerTableParameters.setLogin(m.getLogin());
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
}

