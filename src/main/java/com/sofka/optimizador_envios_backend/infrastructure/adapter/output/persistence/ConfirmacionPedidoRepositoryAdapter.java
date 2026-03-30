package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence;

import com.sofka.optimizador_envios_backend.application.port.output.ConfirmacionPedidoRepository;
import com.sofka.optimizador_envios_backend.domain.model.ConfirmacionPedido;
import com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity.ConfirmacionPedidoEntity;
import com.sofka.optimizador_envios_backend.infrastructure.mapper.ConfirmacionPedidoEntityMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ConfirmacionPedidoRepositoryAdapter implements ConfirmacionPedidoRepository {

    private final ConfirmacionPedidoJpaRepository jpaRepository;
    private final ConfirmacionPedidoEntityMapper mapper;

    public ConfirmacionPedidoRepositoryAdapter(
            ConfirmacionPedidoJpaRepository jpaRepository,
            ConfirmacionPedidoEntityMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ConfirmacionPedido guardar(ConfirmacionPedido confirmacionPedido) {
        ConfirmacionPedidoEntity entity = mapper.toEntity(confirmacionPedido);
        ConfirmacionPedidoEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}