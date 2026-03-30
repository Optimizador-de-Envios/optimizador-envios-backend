package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence;

import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity.ConfirmacionPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmacionPedidoJpaRepository extends JpaRepository<ConfirmacionPedidoEntity, String> {
}