package uaemex.ia.proyecto.servidor.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import uaemex.ia.proyecto.compartido.Disco;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String ARCHIVO = "data/coleccion.json";
    private static volatile Database instancia;

    private final Gson gson;
    private final List<Disco> coleccion;

    private Database() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        coleccion = cargarDesdeArchivo();
        System.out.println("[Database] Colección cargada: " + coleccion.size() + " disco(s).");
    }

    // Double-checked locking para singleton thread-safe
    public static Database getInstance() {
        if (instancia == null) {
            synchronized (Database.class) {
                if (instancia == null) {
                    instancia = new Database();
                }
            }
        }
        return instancia;
    }

    public synchronized void guardar(Disco disco) {
        coleccion.add(disco);
        persistir();
        System.out.println("[Database] Disco guardado: " + disco);
    }

    public synchronized List<Disco> obtenerTodos() {
        return new ArrayList<>(coleccion);
    }

    private List<Disco> cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            new File("data").mkdirs();
            return new ArrayList<>();
        }
        try (Reader reader = new FileReader(archivo)) {
            Type tipo = new TypeToken<List<Disco>>() {}.getType();
            List<Disco> lista = gson.fromJson(reader, tipo);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("[Database] Error al cargar: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void persistir() {
        new File("data").mkdirs();
        try (Writer writer = new FileWriter(ARCHIVO)) {
            gson.toJson(coleccion, writer);
        } catch (IOException e) {
            System.err.println("[Database] Error al persistir: " + e.getMessage());
        }
    }
}
