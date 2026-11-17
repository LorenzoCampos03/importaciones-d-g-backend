package pe.edu.vallegrande.importaciones_backend.config;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.SimpleFontFamily;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.logging.Logger;

/**
 * Configuración para manejar las fuentes en JasperReports
 * Esta clase configura JasperReports para usar fuentes estándar como
 * alternativa a fuentes personalizadas
 */
@Configuration
public class JasperFontConfig {

     private static final Logger logger = Logger.getLogger(JasperFontConfig.class.getName());

     @PostConstruct
     public void init() {
          // Configuración avanzada para manejo de fuentes en JasperReports
          JasperReportsContext context = DefaultJasperReportsContext.getInstance();

          // Configurar JasperReports para ignorar fuentes faltantes y usar alternativas
          JRPropertiesUtil.getInstance(context)
                    .setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");

          // Registrar mapa de fuentes para reemplazar "Berlin Sans FB" y otras por
          // fuentes estándar
          JRPropertiesUtil.getInstance(context)
                    .setProperty("net.sf.jasperreports.awt.font.mappings", "fontMappings.properties");

          // Establecer para modo headless
          System.setProperty("java.awt.headless", "true");

          // Registrar fuentes estándar como alternativas
          JRPropertiesUtil.getInstance(context)
                    .setProperty("net.sf.jasperreports.default.font.name", "SansSerif");
          JRPropertiesUtil.getInstance(context)
                    .setProperty("net.sf.jasperreports.default.pdf.font.name", "Helvetica");
          JRPropertiesUtil.getInstance(context)
                    .setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
          JRPropertiesUtil.getInstance(context)
                    .setProperty("net.sf.jasperreports.default.font.size", "10");

          // Configuración específica para el reporte TRANSACCIONAL
          JRPropertiesUtil.getInstance(context)
                    .setProperty("net.sf.jasperreports.font.substitution.berlin sans fb", "SansSerif");
          JRPropertiesUtil.getInstance(context)
                    .setProperty("net.sf.jasperreports.font.substitution.berlin sans fb demi", "SansSerif,Bold");

          // Listar fuentes disponibles para diagnóstico
          logger.info("Fuentes disponibles en el sistema:");
          Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
          for (Font font : fonts) {
               logger.info(font.getFontName());
          }
     }
}
