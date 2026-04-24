package isi.shoppingCart.usecases.services;

import isi.shoppingCart.entities.Carrito;
import isi.shoppingCart.entities.Compra;
import isi.shoppingCart.entities.ItemCarrito;
import isi.shoppingCart.entities.Producto;
import isi.shoppingCart.usecases.ports.CarritoRepository;
import isi.shoppingCart.usecases.ports.CompraRepository;
import isi.shoppingCart.usecases.ports.ProductoRepository;

import java.util.List;

public class ConfirmarCompraUseCase {
    private CarritoRepository carritoRepository;
    private ProductoRepository productoRepository;
    private CompraRepository compraRepository;

    public ConfirmarCompraUseCase(CarritoRepository carritoRepository,
                                  ProductoRepository productoRepository,
                                  CompraRepository compraRepository) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.compraRepository = compraRepository;
    }

    public ConfirmarCompraResult execute(int clienteId) {
        Carrito carrito = carritoRepository.obtenerPorCliente(clienteId);

        if (!carrito.puedeConfirmarse()) {
            return ConfirmarCompraResult.failure(
                "El carrito está vacío. Agregue productos antes de confirmar."
            );
        }

        List<ItemCarrito> items = carrito.getItems();
        int i;
        for (i = 0; i < items.size(); i++) {
            ItemCarrito item = items.get(i);
            Producto producto = productoRepository.buscarPorId(item.getProducto().getId());
            if (!producto.tieneStockSuficiente(item.getCantidad())) {
                return ConfirmarCompraResult.failure(
                    "Stock insuficiente para '" + producto.getName() + "': "
                    + "solicitado=" + item.getCantidad() + ", disponible=" + producto.getStock()
                );
            }
        }


        Compra compra = Compra.crearDesde(carrito, clienteId);
        int orderId = compraRepository.nextId();
        compra.setId(orderId);

        for (i = 0; i < items.size(); i++) {
            ItemCarrito item = items.get(i);
            Producto producto = productoRepository.buscarPorId(item.getProducto().getId());
            producto.disminuirStock(item.getCantidad());
            productoRepository.guardar(producto);
        }


        compraRepository.guardar(compra);

        carrito.confirmar();
        carritoRepository.guardar(carrito);

        return ConfirmarCompraResult.success(compra);
    }
}
