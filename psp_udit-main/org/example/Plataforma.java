package org.example;
import java.util.ArrayList;

public class Plataforma {

    // ATRIBUTO PRIVADO
    private ArrayList<Episodio> catalogo;

    // CONSTRUCTOR VACÍO (Inicializa la lista)
    public Plataforma() {
        this.catalogo = new ArrayList<>();
    }

    // MÉTODOS
    public void agregarEpisodio(Episodio e) {
        this.catalogo.add(e);
    }

    public void procesarCatalogo() {
        for (Episodio ep : this.catalogo) {
            ep.procesar();
        }
    }
}