package pe.edu.vallegrande.importaciones_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final DataSource dataSource;

    public byte[] generateReportSale(Long saleId) throws JRException, IOException, SQLException {
        log.info("Generando reporte de venta con ID: {}", saleId);
        Connection conn = null;
        try {
            InputStream jasperStream = new ClassPathResource("reports/TRANSACCIONAL.jasper").getInputStream();
            HashMap<String, Object> params = new HashMap<>();
            params.put("SALE_ID", saleId);

            conn = dataSource.getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, params, conn);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.error("Error al generar reporte de venta: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error al cerrar conexión: {}", e.getMessage());
                }
            }
        }
    }

    public byte[] generateReportProduct() throws JRException, IOException, SQLException {
        log.info("Generando reporte de productos");
        Connection conn = null;
        try {
            InputStream jasperStream = new ClassPathResource("reports/ProductListReport.jasper").getInputStream();
            HashMap<String, Object> params = new HashMap<>();

            conn = dataSource.getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, params, conn);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.error("Error al generar reporte de productos: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error al cerrar conexión: {}", e.getMessage());
                }
            }
        }
    }

    public byte[] generateReportPurchase(Long purchaseId) throws JRException, IOException, SQLException {
        log.info("Generando reporte de compra con ID: {}", purchaseId);
        Connection conn = null;
        try {
            InputStream jasperStream = new ClassPathResource("reports/ReporteCompras.jasper").getInputStream();
            HashMap<String, Object> params = new HashMap<>();

            // CAMBIAR DE "PURCHASE_ID" A "purchase_id" (minúsculas)
            params.put("purchase_id", purchaseId.intValue());

            conn = dataSource.getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, params, conn);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            log.error("Error al generar reporte de compra: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("Error al cerrar conexión: {}", e.getMessage());
                }
            }
        }
    }

    // Todos los métodos están usando directamente
    // JasperExportManager.exportReportToPdf
}
