package congreso;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa un congreso y los ponentes que participan en él.
 */
public class Congreso {

    public static final int MAXIMO_PONENTES_PERMITIDO = 500;
    public static final double COSTE_POR_PONENTE = 1_000.0;

    private String nombre;
    private String lugar;
    private double presupuesto;
    private final Set<Ponente> ponentes = new LinkedHashSet<>();
    private int numeroMaximoPonentes;

    /**
     * Crea un congreso con capacidad para 500 ponentes.
     */
    public Congreso(String nombre, String lugar) {
        this(nombre, lugar, 0.0, Collections.emptySet(), MAXIMO_PONENTES_PERMITIDO);
    }

    /**
     * Constructor con todos los argumentos.
     */
    public Congreso(String nombre, String lugar, double presupuesto,
                    Collection<Ponente> ponentes, int numeroMaximoPonentes) {
        setNombre(nombre);
        setLugar(lugar);
        setPresupuesto(presupuesto);
        setNumeroMaximoPonentes(numeroMaximoPonentes);
        setPonentes(ponentes);
    }

    /**
     * Crea un congreso vacío con capacidad para 500 ponentes.
     */
    public Congreso() {
        this("Sin nombre", "Sin lugar");
    }

    /**
     * Constructor con todos los argumentos salvo el número máximo de ponentes.
     */
    public Congreso(String nombre, String lugar, double presupuesto,
                    Collection<Ponente> ponentes) {
        this(nombre, lugar, presupuesto, ponentes, MAXIMO_PONENTES_PERMITIDO);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarTexto(nombre, "El nombre");
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = validarTexto(lugar, "El lugar");
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        if (!Double.isFinite(presupuesto) || presupuesto < 0) {
            throw new IllegalArgumentException("El presupuesto no puede ser negativo.");
        }
        this.presupuesto = presupuesto;
    }

    /**
     * Devuelve una vista de solo lectura para evitar saltarse el límite de aforo.
     */
    public Set<Ponente> getPonentes() {
        return Collections.unmodifiableSet(ponentes);
    }

    public void setPonentes(Collection<Ponente> nuevosPonentes) {
        Objects.requireNonNull(nuevosPonentes, "La colección de ponentes no puede ser nula.");
        LinkedHashSet<Ponente> copia = new LinkedHashSet<>();
        for (Ponente ponente : nuevosPonentes) {
            copia.add(Objects.requireNonNull(ponente, "Un ponente no puede ser nulo."));
        }
        if (copia.size() > numeroMaximoPonentes) {
            throw new IllegalArgumentException(
                    "El congreso no admite más de " + numeroMaximoPonentes + " ponentes.");
        }

        for (Ponente ponenteAnterior : new LinkedHashSet<>(ponentes)) {
            if (!copia.contains(ponenteAnterior)) {
                ponenteAnterior.eliminarCongresoDesdeCongreso(this);
            }
        }
        ponentes.clear();
        for (Ponente ponente : copia) {
            altaPonente(ponente);
        }
    }

    public int getNumeroMaximoPonentes() {
        return numeroMaximoPonentes;
    }

    /**
     * El máximo de un congreso debe estar entre 1 y el máximo general (500).
     */
    public void setNumeroMaximoPonentes(int numeroMaximoPonentes) {
        if (numeroMaximoPonentes < 1
                || numeroMaximoPonentes > MAXIMO_PONENTES_PERMITIDO) {
            throw new IllegalArgumentException(
                    "El número máximo de ponentes debe estar entre 1 y 500.");
        }
        if (numeroMaximoPonentes < ponentes.size()) {
            throw new IllegalArgumentException(
                    "El máximo no puede ser menor que el número actual de ponentes.");
        }
        this.numeroMaximoPonentes = numeroMaximoPonentes;
    }

    /**
     * Da de alta al ponente si todavía no participa y queda sitio.
     *
     * @return true si se ha añadido; false si ya estaba dado de alta.
     */
    public boolean altaPonente(Ponente ponente) {
        Objects.requireNonNull(ponente, "El ponente no puede ser nulo.");
        if (ponentes.contains(ponente)) {
            return false;
        }
        if (ponentes.size() >= numeroMaximoPonentes) {
            throw new IllegalStateException("Se ha alcanzado el número máximo de ponentes.");
        }

        ponentes.add(ponente);
        ponente.registrarCongresoDesdeCongreso(this);
        return true;
    }

    void eliminarPonenteDesdePonente(Ponente ponente) {
        ponentes.remove(ponente);
    }

    public boolean participaPonente(Ponente ponente) {
        return ponentes.contains(ponente);
    }

    /**
     * Obtiene los ponentes de este congreso que también han asistido a algún
     * congreso celebrado en la ciudad indicada.
     */
    public Set<Ponente> getPonentesConCongresosEnCiudad(String ciudad) {
        validarTexto(ciudad, "La ciudad");
        Set<Ponente> resultado = new LinkedHashSet<>();
        for (Ponente ponente : ponentes) {
            if (ponente.haAsistidoEnCiudad(ciudad, this)) {
                resultado.add(ponente);
            }
        }
        return Collections.unmodifiableSet(resultado);
    }

    /**
     * Calcula, guarda y devuelve el presupuesto a razón de 1.000 € por ponente.
     */
    public double calcularPresupuesto() {
        presupuesto = ponentes.size() * COSTE_POR_PONENTE;
        return presupuesto;
    }

    private static String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return texto.trim();
    }

    @Override
    public String toString() {
        return nombre + " (" + lugar + ")";
    }
}
