package isi.shoppingCart.adapters.ui;

import isi.shoppingCart.entities.CartItem;
import isi.shoppingCart.entities.Product;
import isi.shoppingCart.infrastructure.repositories.InMemoryCartRepository;
import isi.shoppingCart.infrastructure.repositories.InMemoryProductRepository;
import isi.shoppingCart.usecases.ports.CartRepository;
import isi.shoppingCart.usecases.ports.ProductRepository;
import isi.shoppingCart.usecases.services.AgregarProductoAlCarritoUseCase;
import isi.shoppingCart.usecases.services.ShoppingCartApp;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainView {
    private static final int CLIENTE_ID = 1; 
    private ShoppingCartApp shoppingCartApp;
    private VBox catalogBox;
    private VBox cartBox;
    private Label totalLabel;

    public MainView() {
        ProductoRepository productoRepository = new InMemoryProductoRepository();
        CarritoRepository carritoRepository = new InMemoryCarritoRepository();
        ClienteRepository clienteRepository = new InMemoryClienteRepository();
        CompraRepository compraRepository = new InMemoryCompraRepository();

        AgregarProductoAlCarritoUseCase agregarProductoUseCase =
                new AgregarProductoAlCarritoUseCase(productoRepository, carritoRepository);

        ConfirmarCompraUseCase confirmarCompraUseCase =
                new ConfirmarCompraUseCase(carritoRepository, productoRepository, compraRepository);

        shoppingCartApp = new ShoppingCartApp(
                productoRepository,
                carritoRepository,
                agregarProductoUseCase,
                confirmarCompraUseCase
        );

        catalogBox = new VBox(10);
        cartBox = new VBox(10);
        totalLabel = new Label("Total: $ 0.0");
    }

    public Scene createScene() {
        VBox catalogPanel = createCatalogPanel();
        VBox cartPanel = createCartPanel();

        HBox content = new HBox(20);
        content.setPadding(new Insets(15));
        content.getChildren().addAll(catalogPanel, cartPanel);

        HBox.setHgrow(catalogPanel, Priority.ALWAYS);
        HBox.setHgrow(cartPanel, Priority.ALWAYS);

        refreshCatalog();
        refreshCart();

        BorderPane root = new BorderPane();
        root.setCenter(content);

        return new Scene(root, 900, 450);
    }

    private VBox createCatalogPanel() {
        Label title = new Label("Catalogo");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox panel = new VBox(10);
        panel.getChildren().addAll(title, catalogBox);
        panel.setPrefWidth(430);
        panel.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");
        return panel;
    }

    private VBox createCartPanel() {
        Label title = new Label("Carrito");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button confirmButton = new Button("Confirmar compra");
        confirmButton.setOnAction(event -> handleConfirmarCompra());

        VBox panel = new VBox(10);
        panel.getChildren().addAll(title, cartBox, totalLabel, confirmButton);
        panel.setPrefWidth(430);
        panel.setStyle("-fx-border-color: lightgray; -fx-padding: 10;");
        return panel;
    }

    private void handleConfirmarCompra() {
        ConfirmarCompraResult result = shoppingCartApp.confirmarCompra(CLIENTE_ID);

        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
            return;
        }

        Compra compra = result.getCompra();
        String resumen = buildOrderSummary(compra);
        showMessage(resumen);

        refreshCatalog();
        refreshCart();
    }

    private String buildOrderSummary(Compra compra) {
        StringBuilder sb = new StringBuilder();
        sb.append("¡Compra confirmada! Orden #").append(compra.getId()).append("\n\n");

        List<ItemCarrito> items = compra.getItems();
        int i;
        for (i = 0; i < items.size(); i++) {
            ItemCarrito item = items.get(i);
            sb.append("- ").append(item.getProducto().getName())
              .append(" x").append(item.getCantidad())
              .append("  $").append(item.calcularSubtotal()).append("\n");
        }

        sb.append("\nTotal pagado: $ ").append(compra.getTotal());
        return sb.toString();
    }

    private void refreshCatalog() {
        catalogBox.getChildren().clear();

        List<Producto> products = shoppingCartApp.getCatalogProducts();
        int i;

        for (i = 0; i < products.size(); i++) {
            Producto producto = products.get(i);
            HBox row = new HBox(10);

            Label nameLabel = new Label(producto.getName());
            Label priceLabel = new Label("$ " + producto.getPrice());
            Label stockLabel = new Label("Disponible: " + producto.getStock());
            Button addButton = new Button("Agregar");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            addButton.setOnAction(event -> {
                String message = shoppingCartApp.addProductToCart(CLIENTE_ID, producto.getId());
                if (!message.equals("")) {
                    showError(message);
                }
                refreshCatalog();
                refreshCart();
            });

            row.getChildren().addAll(nameLabel, priceLabel, stockLabel, spacer, addButton);
            row.setStyle("-fx-padding: 5; -fx-border-color: #DDDDDD;");
            catalogBox.getChildren().add(row);
        }
    }

    private void refreshCart() {
        cartBox.getChildren().clear();

        List<ItemCarrito> items = shoppingCartApp.getCartItems(CLIENTE_ID);
        int i;

        for (i = 0; i < items.size(); i++) {
            ItemCarrito item = items.get(i);
            HBox row = new HBox(10);

            Label nameLabel = new Label(item.getProducto().getName());
            Label quantityLabel = new Label("Cantidad: " + item.getCantidad());
            Label subtotalLabel = new Label("Subtotal: $ " + item.calcularSubtotal());

            row.getChildren().addAll(nameLabel, quantityLabel, subtotalLabel);
            row.setStyle("-fx-padding: 5; -fx-border-color: #DDDDDD;");
            cartBox.getChildren().add(row);
        }

        totalLabel.setText("Total: $ " + shoppingCartApp.getCartTotal(CLIENTE_ID));
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
