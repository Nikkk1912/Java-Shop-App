package org.example.courseprojgui.fxControllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.example.courseprojgui.HelloApplication;
import org.example.courseprojgui.enums.OrderStatus;
import org.example.courseprojgui.hibernate.GenericHibernate;
import org.example.courseprojgui.hibernate.HibernateShop;
import org.example.courseprojgui.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrdersTabController implements Initializable {


    public Text myCartsText;
    public ListView<Cart> cartsList;
    public ListView<Product> productList;
    public TextField ownerField;
    public TextField managerField;
    public TextField idField;
    public Button deleteButton;
    public Button refreshButton;
    public TreeView<Comment> commentsTreeList;
    public TextField statusField;
    public Button changeOrderStatusButton;
    private HibernateShop hibernateShop;
    private GenericHibernate genericHibernate;
    private UsersTabController usersTabController;
    private ShopTabController shopTabController;
    @Getter
    private static OrdersTabController instance;

    public OrdersTabController() {
        instance = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MainController mainController = MainController.getInstance();
        hibernateShop = new HibernateShop(mainController.getEntityManagerFactory());
        genericHibernate = new GenericHibernate(mainController.getEntityManagerFactory());
        usersTabController = UsersTabController.getInstance();
        shopTabController = ShopTabController.getInstance();

        setCellFactories();

        ownerField.setEditable(false);
        managerField.setEditable(false);
        idField.setEditable(false);
        statusField.setEditable(false);


    }

    public void loadCartData() {
        User currentUser = genericHibernate.getEntityById(usersTabController.getCurrentUser().getClass(), usersTabController.getCurrentUser().getId());
        Cart cart = cartsList.getSelectionModel().getSelectedItem();

        myCartsText.setText("Orders : " + usersTabController.getCurrentUser().getName() + " " + usersTabController.getCurrentUser().getSurname());
        ownerField.setText(currentUser.getName() + " " + currentUser.getSurname());
        if(cart != null) {
            managerField.setText(cart.getManager().getName() + " " + cart.getManager().getSurname());
            idField.setText(String.valueOf(cart.getId()));
            statusField.setText(String.valueOf(cart.getOrderStatus()));
            loadProductData(cart);
        }
    }

    private void loadProductData(Cart cart) {
        productList.getItems().setAll(cart.getItemsToBuy());
        loadComments();

    }

    private void setCellFactories() {
        cartsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Cart cart, boolean empty) {
                super.updateItem(cart, empty);
                if (empty || cart == null) {
                    setText(null);
                } else {
                    setFont(Font.font(16));
                    setText("Cart " + cart.getId() + " | Manager: " + cart.getManager().getName() + " | " + cart.getManager().getSurname());
                }
            }
        });

        productList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    setFont(Font.font(16));
                    setText("Id: " + product.getId() + " | " + product.getClass().getSimpleName() + " | " + product.getTitle() + " : " + product.getQuantity());
                }
            }
        });

        commentsTreeList.setCellFactory(param -> new TreeCell<>() {
            @Override
            protected void updateItem(Comment comment, boolean empty) {
                super.updateItem(comment, empty);
                if (empty || comment == null) {
                    setText(null);
                } else {
                    setText("- " + comment.getCommentOwner().getClass().getSimpleName() + " | " + comment.getCommentBody());
                }
            }
        });
    }

    public void deleteCart() {
        User currentUser = usersTabController.getCurrentUser();
        if(currentUser instanceof Manager) {
            Cart cart = cartsList.getSelectionModel().getSelectedItem();
            if (cart.getOrderStatus() != OrderStatus.Payment_received || cart.getOrderStatus() != OrderStatus.shipping || cart.getOrderStatus() != OrderStatus.delivered) {
                hibernateShop.deleteCart(cart.getId());
                cartsList.getItems().remove(cart);
                productList.getItems().clear();
                shopTabController.updateProductList();
                idField.clear();
                managerField.clear();
                ownerField.clear();
            }
        }
    }

    public void refreshCartsList() {
        User currentUser = genericHibernate.getEntityById(usersTabController.getCurrentUser().getClass(), usersTabController.getCurrentUser().getId());
        List<Cart> carts = new ArrayList<>();
        if(currentUser instanceof Customer) {
            carts = hibernateShop.getCartsByCustomerId(currentUser.getId());
        } else if (currentUser instanceof Manager) {
            carts = hibernateShop.getCartsByManagerId(currentUser.getId());
        }
        cartsList.getItems().setAll(carts);
    }

    public void addReview() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("commentCreationWindow.fxml"));
        Parent parent = fxmlLoader.load();
        CommentCreationWindowController commentForm = fxmlLoader.getController();
        commentForm.setData(usersTabController.getCurrentUser(), cartsList.getSelectionModel().getSelectedItem());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Create comment");
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public void loadComments() {
        Cart currentCart = genericHibernate.getEntityById(Cart.class, cartsList.getSelectionModel().getSelectedItem().getId());
        commentsTreeList.setRoot(new TreeItem<>());
        commentsTreeList.setShowRoot(false);
        commentsTreeList.getRoot().setExpanded(true);
        currentCart.getChat().forEach(comment -> addTreeItem(comment, commentsTreeList.getRoot()));

    }

    private void addTreeItem(Comment comment, TreeItem<Comment> parentComment) {
        TreeItem<Comment> treeItem = new TreeItem<>(comment);
        parentComment.getChildren().add(treeItem);
        comment.getReplies().forEach(sub -> addTreeItem(sub, treeItem));
    }

    private static void generateAlert(Alert.AlertType alertType, String header, String text){
        Alert alert = new Alert(alertType);
        alert.setTitle("System message");
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }

    public void deleteComment() {
        User currentUser = usersTabController.getCurrentUser();
        if (currentUser == commentsTreeList.getSelectionModel().getSelectedItem().getValue().getCommentOwner() || currentUser instanceof Manager) {
            Comment comment = commentsTreeList.getSelectionModel().getSelectedItem().getValue();
            if (comment != null) {
                hibernateShop.deleteMessageFromChat(comment.getId());
                genericHibernate.delete(Comment.class, comment.getId());
                loadComments();
            }
        }
    }

    public void previewComment() {
        Comment comment = genericHibernate.getEntityById(Comment.class, commentsTreeList.getSelectionModel().getSelectedItem().getValue().getId());
        generateAlert(Alert.AlertType.INFORMATION,"Title: " + comment.getCommentTitle(), comment.genText());
    }

    public void changeOrderStatus() {
        User user = usersTabController.getCurrentUser();
        if(user instanceof Manager) {
            Cart cart = cartsList.getSelectionModel().getSelectedItem();
            if (cart != null) {
                if (cart.getOrderStatus() != null) {
                    OrderStatus nextStatus = cart.getOrderStatus().getNextStatus();
                    if (nextStatus != null) {
                        cart.setOrderStatus(nextStatus);
                        genericHibernate.update(cart);
                    } else {
                        cart.setOrderStatus(OrderStatus.Received);
                        genericHibernate.update(cart);
                    }
                } else {
                    cart.setOrderStatus(OrderStatus.Received);
                    genericHibernate.update(cart);
                }
                statusField.setText(String.valueOf(cart.getOrderStatus()));
            }
        }
    }
}
