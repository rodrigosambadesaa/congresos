package congreso;

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Representa un ponente y los trabajos presentados en cada congreso.
 */
public class Ponente {

    public static final int MAXIMO_TRABAJOS_POR_CONGRESO = 5;

    private String nombre;
    private final Set<Congreso> congresos = new LinkedHashSet<>();
    private final Map<Congreso, Integer> numeroTrabajosPresentados = new LinkedHashMap<>();

    public Ponente(String nombre) {
        setNombre(nombre);
    }

    /**
     * Constructor con todos los argumentos. Cada valor del mapa es el número de
     * trabajos presentados en el congreso correspondiente.
     */
    public Ponente(String nombre, Collection<Congreso> congresos,
                   Map<Congreso, Integer> numeroTrabajosPresentados) {
        setNombre(nombre);
        setCongresos(congresos);
        setNumeroTrabajosPresentados(numeroTrabajosPresentados);
    }

    public Ponente() {
        this("Sin nombre");
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public Set<Congreso> getCongresos() {
        return Collections.unmodifiableSet(congresos);
    }

    /**
     * Sustituye los congresos y asigna cero trabajos a las nuevas asistencias.
     */
    public void setCongresos(Collection<Congreso> nuevosCongresos) {
        Objects.requireNonNull(nuevosCongresos,
                "La colección de congresos no puede ser nula.");
        LinkedHashSet<Congreso> copia = new LinkedHashSet<>();
        for (Congreso congreso : nuevosCongresos) {
            copia.add(Objects.requireNonNull(congreso, "Un congreso no puede ser nulo."));
        }
        for (Congreso congreso : copia) {
            if (!congreso.participaPonente(this)
                    && congreso.getPonentes().size() >= congreso.getNumeroMaximoPonentes()) {
                throw new IllegalStateException(
                        "No queda sitio en el congreso " + congreso.getNombre() + ".");
            }
        }

        for (Congreso congresoAnterior : new LinkedHashSet<>(congresos)) {
            if (!copia.contains(congresoAnterior)) {
                congresoAnterior.eliminarPonenteDesdePonente(this);
                numeroTrabajosPresentados.remove(congresoAnterior);
            }
        }
        congresos.clear();
        for (Congreso congreso : copia) {
            congreso.altaPonente(this);
            congresos.add(congreso);
            numeroTrabajosPresentados.putIfAbsent(congreso, 0);
        }
    }

    public Map<Congreso, Integer> getNumeroTrabajosPresentados() {
        return Collections.unmodifiableMap(numeroTrabajosPresentados);
    }

    /**
     * Establece el número de trabajos para todos los congresos ya registrados.
     */
    public void setNumeroTrabajosPresentados(
            Map<Congreso, Integer> nuevosNumerosDeTrabajos) {
        Objects.requireNonNull(nuevosNumerosDeTrabajos,
                "El mapa de trabajos no puede ser nulo.");
        if (!nuevosNumerosDeTrabajos.keySet().equals(congresos)) {
            throw new IllegalArgumentException(
                    "Debe indicarse el número de trabajos de cada congreso.");
        }
        for (Map.Entry<Congreso, Integer> entrada : nuevosNumerosDeTrabajos.entrySet()) {
            validarParticipacion(entrada.getKey(), entrada.getValue());
        }
        numeroTrabajosPresentados.clear();
        numeroTrabajosPresentados.putAll(nuevosNumerosDeTrabajos);
    }

    /**
     * Getter específico para el número de trabajos de un congreso.
     */
    public int getNumeroTrabajos(Congreso congreso) {
        return numeroTrabajosPresentados.getOrDefault(congreso, 0);
    }

    /**
     * Setter específico para el número de trabajos de un congreso.
     */
    public void setNumeroTrabajos(Congreso congreso, int numeroTrabajos) {
        validarParticipacion(congreso, numeroTrabajos);
        if (!congresos.contains(congreso)) {
            congreso.altaPonente(this);
        }
        numeroTrabajosPresentados.put(congreso, numeroTrabajos);
    }

    /**
     * Registra la asistencia y sus trabajos, manteniendo la relación con Congreso.
     */
    public void registrarParticipacion(Congreso congreso, int numeroTrabajos) {
        validarParticipacion(congreso, numeroTrabajos);
        congreso.altaPonente(this);
        congresos.add(congreso);
        numeroTrabajosPresentados.put(congreso, numeroTrabajos);
    }

    void registrarCongresoDesdeCongreso(Congreso congreso) {
        congresos.add(congreso);
        numeroTrabajosPresentados.putIfAbsent(congreso, 0);
    }

    void eliminarCongresoDesdeCongreso(Congreso congreso) {
        congresos.remove(congreso);
        numeroTrabajosPresentados.remove(congreso);
    }

    public int getNumeroTotalTrabajos() {
        int total = 0;
        for (int trabajos : numeroTrabajosPresentados.values()) {
            total += trabajos;
        }
        return total;
    }

    public boolean haAsistidoEnCiudad(String ciudad) {
        return haAsistidoEnCiudad(ciudad, null);
    }

    boolean haAsistidoEnCiudad(String ciudad, Congreso congresoExcluido) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía.");
        }
        for (Congreso congreso : congresos) {
            if (congreso != congresoExcluido
                    && congreso.getLugar().equalsIgnoreCase(ciudad.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean haParticipadoEn(Congreso congreso) {
        return congresos.contains(congreso);
    }

    private static void validarParticipacion(Congreso congreso, Integer numeroTrabajos) {
        Objects.requireNonNull(congreso, "El congreso no puede ser nulo.");
        if (numeroTrabajos == null
                || numeroTrabajos < 0
                || numeroTrabajos > MAXIMO_TRABAJOS_POR_CONGRESO) {
            throw new IllegalArgumentException(
                    "El número de trabajos debe estar entre 0 y 5.");
        }
    }

    @Override
    public String toString() {
        return nombre;
    }
}
