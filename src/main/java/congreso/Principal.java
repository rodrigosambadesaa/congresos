package congreso;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clase principal: prueba todos los constructores, getters, setters y métodos.
 */
public final class Principal {

    private static int pruebasCorrectas;

    private Principal() {
    }

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE CONGRESOS ===");

        probarConstructoresCongreso();
        probarConstructoresPonente();
        probarGettersYSetters();
        probarMetodos();
        probarLimites();

        System.out.printf("%nResultado: %d comprobaciones correctas.%n", pruebasCorrectas);
    }

    private static void probarConstructoresCongreso() {
        System.out.println("\n1. Constructores de Congreso");

        Congreso dosArgumentos = new Congreso("Java Hispano", "Madrid");
        comprobar("constructor nombre/lugar", dosArgumentos.getNombre().equals("Java Hispano")
                && dosArgumentos.getLugar().equals("Madrid")
                && dosArgumentos.getNumeroMaximoPonentes() == 500);

        Ponente ana = new Ponente("Ana");
        Congreso todos = new Congreso(
                "POO 2026", "Sevilla", 2_500, List.of(ana), 100);
        comprobar("constructor con todos los argumentos",
                todos.getPresupuesto() == 2_500
                        && todos.getNumeroMaximoPonentes() == 100
                        && todos.participaPonente(ana));

        Congreso sinArgumentos = new Congreso();
        comprobar("constructor sin argumentos",
                sinArgumentos.getNombre().equals("Sin nombre")
                        && sinArgumentos.getLugar().equals("Sin lugar"));

        Congreso sinMaximo = new Congreso(
                "Software Libre", "Granada", 4_000, List.of(ana));
        comprobar("constructor sin máximo",
                sinMaximo.getNumeroMaximoPonentes() == 500
                        && sinMaximo.getPonentes().contains(ana));
    }

    private static void probarConstructoresPonente() {
        System.out.println("\n2. Constructores de Ponente");

        Ponente conNombre = new Ponente("Beatriz");
        comprobar("constructor con nombre", conNombre.getNombre().equals("Beatriz"));

        Congreso congreso = new Congreso("Datos", "Valencia");
        Map<Congreso, Integer> participaciones = new LinkedHashMap<>();
        participaciones.put(congreso, 3);
        Ponente todos = new Ponente("Carlos", List.of(congreso), participaciones);
        comprobar("constructor con todos los argumentos",
                todos.getNumeroTrabajos(congreso) == 3
                        && congreso.participaPonente(todos));

        Ponente sinArgumentos = new Ponente();
        comprobar("constructor sin argumentos",
                sinArgumentos.getNombre().equals("Sin nombre")
                        && sinArgumentos.getCongresos().isEmpty());
    }

    private static void probarGettersYSetters() {
        System.out.println("\n3. Getters y setters");

        Congreso congreso = new Congreso();
        congreso.setNombre("Inteligencia Artificial");
        congreso.setLugar("Barcelona");
        congreso.setPresupuesto(8_500);
        congreso.setNumeroMaximoPonentes(25);

        Ponente diana = new Ponente();
        diana.setNombre("Diana");
        congreso.setPonentes(List.of(diana));

        comprobar("get/set nombre de Congreso",
                congreso.getNombre().equals("Inteligencia Artificial"));
        comprobar("get/set lugar", congreso.getLugar().equals("Barcelona"));
        comprobar("get/set presupuesto", congreso.getPresupuesto() == 8_500);
        comprobar("get/set máximo de ponentes", congreso.getNumeroMaximoPonentes() == 25);
        comprobar("get/set ponentes", congreso.getPonentes().equals(Set.of(diana)));
        comprobar("get/set nombre de Ponente", diana.getNombre().equals("Diana"));

        Congreso redes = new Congreso("Redes", "Málaga");
        diana.setCongresos(List.of(redes));
        diana.setNumeroTrabajosPresentados(Map.of(redes, 2));
        diana.setNumeroTrabajos(redes, 4);
        comprobar("get/set congresos del Ponente",
                diana.getCongresos().contains(redes));
        comprobar("get/set números de trabajos",
                diana.getNumeroTrabajosPresentados().get(redes) == 4);
        comprobar("get/set trabajos por congreso", diana.getNumeroTrabajos(redes) == 4);
    }

    private static void probarMetodos() {
        System.out.println("\n4. Métodos de Congreso y Ponente");

        Congreso poo = new Congreso("POO", "A Coruña");
        Congreso web = new Congreso("Desarrollo Web", "Madrid");
        Congreso seguridad = new Congreso("Ciberseguridad", "Bilbao");

        Ponente eva = new Ponente("Eva");
        Ponente fabio = new Ponente("Fabio");
        Ponente gema = new Ponente("Gema");

        comprobar("alta de ponente", poo.altaPonente(eva));
        comprobar("no duplica ponentes", !poo.altaPonente(eva));
        comprobar("consulta si participa", poo.participaPonente(eva));

        eva.registrarParticipacion(web, 4);
        eva.setNumeroTrabajos(poo, 2);
        fabio.registrarParticipacion(seguridad, 3);
        poo.altaPonente(fabio);
        poo.altaPonente(gema);

        comprobar("total de trabajos", eva.getNumeroTotalTrabajos() == 6);
        comprobar("asistencia por ciudad", eva.haAsistidoEnCiudad("madrid"));
        comprobar("participación en congreso", eva.haParticipadoEn(web));

        Set<Ponente> conOtrosCongresosEnMadrid =
                poo.getPonentesConCongresosEnCiudad("Madrid");
        comprobar("ponentes con otros congresos en una ciudad",
                conOtrosCongresosEnMadrid.equals(Set.of(eva)));

        comprobar("cálculo del presupuesto",
                poo.calcularPresupuesto() == 3 * Congreso.COSTE_POR_PONENTE
                        && poo.getPresupuesto() == 3_000);
    }

    private static void probarLimites() {
        System.out.println("\n5. Validación de límites");

        Congreso conAforoUno = new Congreso("Congreso pequeño", "Lugo",
                0, List.of(), 1);
        conAforoUno.altaPonente(new Ponente("Hugo"));
        comprobarExcepcion("respeta el máximo de ponentes",
                () -> conAforoUno.altaPonente(new Ponente("Irene")));
        comprobarExcepcion("máximo general de 500 ponentes",
                () -> conAforoUno.setNumeroMaximoPonentes(501));

        Ponente julia = new Ponente("Julia");
        Congreso pruebas = new Congreso("Pruebas", "Vigo");
        comprobarExcepcion("máximo de 5 trabajos por congreso",
                () -> julia.registrarParticipacion(pruebas, 6));
    }

    private static void comprobar(String descripcion, boolean condicion) {
        if (!condicion) {
            throw new AssertionError("Fallo: " + descripcion);
        }
        pruebasCorrectas++;
        System.out.println("[OK] " + descripcion);
    }

    private static void comprobarExcepcion(String descripcion, Runnable operacion) {
        try {
            operacion.run();
            throw new AssertionError("Fallo: " + descripcion + " no lanzó una excepción.");
        } catch (IllegalArgumentException | IllegalStateException esperada) {
            comprobar(descripcion, true);
        }
    }
}
