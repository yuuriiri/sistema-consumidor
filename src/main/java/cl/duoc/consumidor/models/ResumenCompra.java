package cl.duoc.consumidor.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tabla nueva: RESUMEN_COMPRA
 * Almacena los resumenes de inscripcion que llegan desde la cola RabbitMQ.
 */
@Entity
@Table(name = "resumen_compra")
public class ResumenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long inscripcionId;

    @Column(nullable = false)
    private String nombreEstudiante;

    @Column(nullable = false)
    private String fechaInscripcion;

    /** Detalle completo del resumen en texto (lo mismo que se sube a S3) */
    @Column(nullable = false, length = 4000)
    private String resumenTexto;

    @Column(nullable = false)
    private Double totalPagar;

    /** Clave del archivo subido a S3 */
    @Column
    private String s3Key;

    @Column(nullable = false)
    private LocalDateTime fechaProcesamiento;

    @PrePersist
    public void prePersist() {
        this.fechaProcesamiento = LocalDateTime.now();
    }

    public ResumenCompra() {}

    // ---------- Getters y Setters ----------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInscripcionId() { return inscripcionId; }
    public void setInscripcionId(Long inscripcionId) { this.inscripcionId = inscripcionId; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(String fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public String getResumenTexto() { return resumenTexto; }
    public void setResumenTexto(String resumenTexto) { this.resumenTexto = resumenTexto; }

    public Double getTotalPagar() { return totalPagar; }
    public void setTotalPagar(Double totalPagar) { this.totalPagar = totalPagar; }

    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }

    public LocalDateTime getFechaProcesamiento() { return fechaProcesamiento; }
    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) { this.fechaProcesamiento = fechaProcesamiento; }
}
