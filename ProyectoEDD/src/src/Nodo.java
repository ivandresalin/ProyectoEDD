package src;

public class Nodo<T> implements java.io.Serializable {
    private T data;
    private Nodo<T> siguiente;
    private Nodo<T> anterior;

    public Nodo(T data) {
        this.data = data;
        this.siguiente = null;
        this.anterior = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }

    public Nodo<T> getAnterior() {
        return anterior;
    }

    public void setAnterior(Nodo<T> anterior) {
        this.anterior = anterior;
    }
}