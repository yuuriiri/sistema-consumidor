package cl.duoc.consumidor.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO que llega desde la cola RabbitMQ.
 * Debe tener la misma estructura que el DTO del MS Productor
 * para que Jackson pueda deserializarlo correctamente.
 */
public class InscripcionMensaje implements Serializable {

    private Long inscripcionId;
    private String nombreEstudiante;
    private String fechaInscripcion;
    private List<CursoDetalle> cursos;
    private Double totalPagar;

    public InscripcionMensaje() {}

    // ---------- Getters y Setters ----------

    public Long getInscripcionId() { return inscripcionId; }
    public void setInscripcionId(Long inscripcionId) { this.inscripcionId = inscripcionId; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(String fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public List<CursoDetalle> getCursos() { return cursos; }
    public void setCursos(List<CursoDetalle> cursos) { this.cursos = cursos; }

    public Double getTotalPagar() { return totalPagar; }
    public void setTotalPagar(Double totalPagar) { this.totalPagar = totalPagar; }

    // ---------- Clase interna para detalle de curso ----------

    public static class CursoDetalle implements Serializable {

        private Long id;
        private String nombre;
        private String instructor;
        private Integer duracionHoras;
        private Double costo;

        public CursoDetalle() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getInstructor() { return instructor; }
        public void setInstructor(String instructor) { this.instructor = instructor; }

        public Integer getDuracionHoras() { return duracionHoras; }
        public void setDuracionHoras(Integer duracionHoras) { this.duracionHoras = duracionHoras; }

        public Double getCosto() { return costo; }
        public void setCosto(Double costo) { this.costo = costo; }
    }
}
