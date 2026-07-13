package cl.duoc.consumidor.controllers;

import cl.duoc.consumidor.models.ResumenCompra;
import cl.duoc.consumidor.repositories.ResumenCompraRepository;
import cl.duoc.consumidor.services.ConsumidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/resumenes")
public class ResumenCompraController {

    @Autowired
    private ResumenCompraRepository resumenCompraRepository;

    @Autowired
    private ConsumidorService consumidorService;

    /**
     * GET /resumenes
     * Lista todos los resumenes de compra procesados desde la cola.
     */
    @GetMapping
    public ResponseEntity<List<ResumenCompra>> listarResumenes() {
        System.out.println("[Consumidor] Se recibio llamada a GET /resumenes");
        return ResponseEntity.ok(resumenCompraRepository.findAll());
    }

    /**
     * GET /resumenes/{id}
     * Obtiene un resumen de compra por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResumenCompra> obtenerResumen(@PathVariable Long id) {
        return resumenCompraRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /resumenes/procesar (NUEVO EFT)
     * Consume manualmente los mensajes pendientes de la cola RabbitMQ
     * y los guarda en la BD + S3.
     */
    @PostMapping("/procesar")
    public ResponseEntity<List<ResumenCompra>> procesarCola() {
        List<ResumenCompra> procesados = consumidorService.consumirManualmente();

        if (procesados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(procesados);
    }

    /**
     * GET /
     * Info general del microservicio consumidor.
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("sistema", "MS Consumidor - Sistema de Inscripcion");
        info.put("version", "1.0.0");
        info.put("descripcion", "Consume inscripciones desde RabbitMQ, guarda en Oracle y sube a S3");
        info.put("endpoints", Map.of(
            "GET  /resumenes",          "Listar todos los resumenes de compra",
            "GET  /resumenes/{id}",     "Obtener un resumen por ID",
            "POST /resumenes/procesar", "Consumir cola manualmente y guardar en BD"
        ));
        return ResponseEntity.ok(info);
    }
}