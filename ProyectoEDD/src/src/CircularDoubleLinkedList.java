package src;

import java.util.Iterator;
import java.io.Serializable;

public class CircularDoubleLinkedList<T> implements Iterable<T>, Serializable {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    public CircularDoubleLinkedList() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    public void add(T data) {
        Nodo<T> newNodo = new Nodo<>(data);
        if (cabeza == null) {
            cabeza = newNodo;
            cola = newNodo;
            cabeza.setSiguiente(cabeza);
            cabeza.setAnterior(cola);
        } else {
            cola.setSiguiente(newNodo);
            newNodo.setAnterior(cola);
            newNodo.setSiguiente(cabeza);
            cabeza.setAnterior(newNodo);
            cola = newNodo;
        }
        tamanio++;
    }

    public void remove(T data) {
        Nodo<T> current = cabeza;
        if (current == null) return;
        do {
            if (current.getData().equals(data)) {
                if (current == cabeza && current == cola) {
                    cabeza = null;
                    cola = null;
                } else if (current == cabeza) {
                    cabeza = current.getSiguiente();
                    cabeza.setAnterior(cola);
                    cola.setSiguiente(cabeza);
                } else if (current == cola) {
                    cola = current.getAnterior();
                    cola.setSiguiente(cabeza);
                    cabeza.setAnterior(cola);
                } else {
                    current.getAnterior().setSiguiente(current.getSiguiente());
                    current.getSiguiente().setAnterior(current.getAnterior());
                }
                tamanio--;
                break;
            }
            current = current.getSiguiente();
        } while (current != cabeza);
    }

    public T siguiente() {
        if (cabeza != null) {
            cabeza = cabeza.getSiguiente();
            return cabeza.getData();
        }
        return null;
    }

    public T anterior() {
        if (cola != null) {
            cola = cola.getAnterior();
            return cola.getData();
        }
        return null;
    }

    public int getTamanio() {
        return tamanio;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> current = cabeza;
            private boolean first = true;

            @Override
            public boolean hasNext() {
                return current != null && (first || current != cabeza);
            }

            @Override
            public T next() {
                if (!hasNext()) return null;
                T data = current.getData();
                current = current.getSiguiente();
                first = false;
                return data;
            }
        };
    }
}