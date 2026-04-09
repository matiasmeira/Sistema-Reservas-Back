package com.matiasmeira.back_reservas.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para cargar variables de entorno desde el archivo .env
 * 
 * Esta clase se encarga de cargar automaticamente las variables definidas
 * en el archivo .env de la raíz del proyecto en el entorno de la aplicación.
 */
@Configuration
public class DotenvConfig {

    /**
     * Bloque estático que se ejecuta al cargar la clase.
     * Carga el archivo .env y establece las variables como propiedades del sistema.
     */
    static {
        loadEnvFile();
    }

    /**
     * Carga el archivo .env y registra las variables en el sistema.
     * 
     * El archivo .env debe estar en la raíz del proyecto.
     * Las variables se cargan automáticamente cuando Spring inicia.
     */
    private static void loadEnvFile() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .filename(".env")
                    .load();

            // Establecer todas las variables del .env como propiedades del sistema
            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
            // Si no existe .env, usar valores por defecto (definidos en application.properties)
            System.out.println("⚠️  Archivo .env no encontrado. Usando configuración por defecto de application.properties");
        }
    }
}
