package cl.duoc.consumidor.services;

import cl.duoc.consumidor.config.RabbitMQConfig;
import cl.duoc.consumidor.dto.InscripcionMensaje;
import cl.duoc.consumidor.models.ResumenCompra;
import cl.duoc.consumidor.repositories.ResumenCompraRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumidorService {

    @Autowired
    private ResumenCompraRepository resumenCompraRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * CONSUMO MANUAL: Endpoint /resumenes/procesar llama a este metodo.
     * Saca todos los mensajes pendientes de la cola y los procesa.
     */
    public List<ResumenCompra> consumirManualmente() {
        List<ResumenCompra> procesados = new ArrayList<>();

        InscripcionMensaje mensaje;
        while ((mensaje = rabbitTemplate.receiveAndConvert(
                RabbitMQConfig.QUEUE,
                new ParameterizedTypeReference<InscripcionMensaje>() {})) != null) {

            System.out.println(">>> [Consumidor] Mensaje consumido de la cola para inscripcion #" + mensaje.getInscripcionId());
            ResumenCompra resumen = procesarYGuardar(mensaje);
            procesados.add(resumen);
        }

        System.out.println(">>> [Consumidor] Total procesados en este ciclo: " + procesados.size());
        return procesados;
    }

    /**
     * Genera resumen, sube a S3, guarda en Oracle.
     */
    private ResumenCompra procesarYGuardar(InscripcionMensaje mensaje) {
        // 1. Generar el texto del resumen de compra
        String resumenTexto = generarResumenTexto(mensaje);

        // 2. Subir a S3
        String s3Key = s3Service.subirResumen(
                mensaje.getInscripcionId(),
                resumenTexto.getBytes(StandardCharsets.UTF_8)
        );
        System.out.println(">>> [Consumidor] Resumen subido a S3: " + s3Key);

        // 3. Guardar en la tabla RESUMEN_COMPRA de Oracle
        ResumenCompra resumen = new ResumenCompra();
        resumen.setInscripcionId(mensaje.getInscripcionId());
        resumen.setNombreEstudiante(mensaje.getNombreEstudiante());
        resumen.setFechaInscripcion(mensaje.getFechaInscripcion());
        resumen.setResumenTexto(resumenTexto);
        resumen.setTotalPagar(mensaje.getTotalPagar());
        resumen.setS3Key(s3Key);

        ResumenCompra guardado = resumenCompraRepository.save(resumen);
        System.out.println(">>> [Consumidor] Resumen guardado en BD Oracle para inscripcion #" + mensaje.getInscripcionId());

        return guardado;
    }

    /**
     * Genera el texto formateado del resumen de compra.
     */
    private String generarResumenTexto(InscripcionMensaje mensaje) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================================\n");
        sb.append("       RESUMEN DE COMPRA #").append(mensaje.getInscripcionId()).append("\n");
        sb.append("================================================\n\n");
        sb.append("Estudiante     : ").append(mensaje.getNombreEstudiante()).append("\n");
        sb.append("Fecha          : ").append(mensaje.getFechaInscripcion()).append("\n\n");
        sb.append("Cursos inscritos:\n");
        sb.append("------------------------------------------------\n");

        for (InscripcionMensaje.CursoDetalle c : mensaje.getCursos()) {
            sb.append(String.format("  - %-30s | Instructor: %-20s | Duracion: %2dh | Costo: $%.2f\n",
                    c.getNombre(), c.getInstructor(), c.getDuracionHoras(), c.getCosto()));
        }

        sb.append("------------------------------------------------\n");
        sb.append(String.format("TOTAL A PAGAR  : $%.2f\n", mensaje.getTotalPagar()));
        sb.append("================================================\n");

        return sb.toString();
    }
}