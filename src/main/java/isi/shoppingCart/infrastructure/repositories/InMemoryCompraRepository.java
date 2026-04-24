package isi.shoppingCart.infrastructure.repositories;

import isi.shoppingCart.entities.Compra;
import isi.shoppingCart.usecases.ports.CompraRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryCompraRepository implements CompraRepository {
    private List<Compra> compras;
    private int nextId;

    public InMemoryCompraRepository() {
        compras = new ArrayList<Compra>();
        nextId = 1;
    }

    public void guardar(Compra compra) {
        compras.add(compra);
    }

    public int nextId() {
        int id = nextId;
        nextId = nextId + 1;
        return id;
    }

    public List<Compra> findAll() {
        return new ArrayList<>(compras);
    }
}
