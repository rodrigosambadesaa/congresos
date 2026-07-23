# Congreso

Proyecto Java 17 que implementa las clases `Congreso` y `Ponente` dentro del
paquete `congreso`. La clase `Principal` prueba todos sus constructores,
getters, setters, métodos y límites.

## Ejecutar con Docker

Desde este directorio:

```powershell
docker compose up --build
```

También se puede construir y ejecutar sin Compose:

```powershell
docker build -t congresos .
docker run --rm congresos
```

## Ejecutar localmente

Se requiere Java 17 y Maven:

```powershell
mvn clean package
java -jar target/congresos-1.0.0.jar
```

El presupuesto se calcula con la constante `COSTE_POR_PONENTE`, establecida en
1.000 euros por ponente para que la regla se pueda modificar fácilmente.
