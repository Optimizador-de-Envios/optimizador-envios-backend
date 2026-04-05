package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "confirmaciones_pedido")
public class ConfirmacionPedidoEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String confirmationToken;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String originName;

    @Column(nullable = false)
    private double originLat;

    @Column(nullable = false)
    private double originLng;

    @Column(nullable = false)
    private String destinationName;

    @Column(nullable = false)
    private double destinationLat;

    @Column(nullable = false)
    private double destinationLng;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private String weightUnit;

    @Column(nullable = false)
    private String priority;

    @Column(nullable = false)
    private double distanceKm;

    @Column(nullable = false)
    private String selectedProviderName;

    @Column(nullable = false)
    private double selectedCost;

    @Column(nullable = false)
    private String selectedCurrency;

    @Column(nullable = false)
    private int selectedEstimatedDays;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

        protected ConfirmacionPedidoEntity() {
        }

        private ConfirmacionPedidoEntity(
            String id,
            String userId,
            String confirmationToken,
            String originName,
            double originLat,
            double originLng,
            String destinationName,
            double destinationLat,
            double destinationLng,
            double weight,
            String weightUnit,
            String priority,
            double distanceKm,
            String selectedProviderName,
            double selectedCost,
            String selectedCurrency,
            int selectedEstimatedDays,
            Instant createdAt
        ) {
        this.id = id;
        this.userId = userId;
        this.confirmationToken = confirmationToken;
        this.originName = originName;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationName = destinationName;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.weight = weight;
        this.weightUnit = weightUnit;
        this.priority = priority;
        this.distanceKm = distanceKm;
        this.selectedProviderName = selectedProviderName;
        this.selectedCost = selectedCost;
        this.selectedCurrency = selectedCurrency;
        this.selectedEstimatedDays = selectedEstimatedDays;
        this.createdAt = createdAt;
        }

        public static ConfirmacionPedidoEntity of(
            String id,
            String confirmationToken,
            String originName,
            double originLat,
            double originLng,
            String destinationName,
            double destinationLat,
            double destinationLng,
            double weight,
            String weightUnit,
            String priority,
            double distanceKm,
            String selectedProviderName,
            double selectedCost,
            String selectedCurrency,
            int selectedEstimatedDays
        ) {
        return new ConfirmacionPedidoEntity(
            id,
            null,
            confirmationToken,
            originName,
            originLat,
            originLng,
            destinationName,
            destinationLat,
            destinationLng,
            weight,
            weightUnit,
            priority,
            distanceKm,
            selectedProviderName,
            selectedCost,
            selectedCurrency,
            selectedEstimatedDays,
            null
        );
        }

        public static ConfirmacionPedidoEntity of(
            String id,
            String userId,
            String confirmationToken,
            String originName,
            double originLat,
            double originLng,
            String destinationName,
            double destinationLat,
            double destinationLng,
            double weight,
            String weightUnit,
            String priority,
            double distanceKm,
            String selectedProviderName,
            double selectedCost,
            String selectedCurrency,
            int selectedEstimatedDays,
            Instant createdAt
        ) {
        return new ConfirmacionPedidoEntity(
            id,
            userId,
            confirmationToken,
            originName,
            originLat,
            originLng,
            destinationName,
            destinationLat,
            destinationLng,
            weight,
            weightUnit,
            priority,
            distanceKm,
            selectedProviderName,
            selectedCost,
            selectedCurrency,
            selectedEstimatedDays,
            createdAt
        );
        }

    @PrePersist
    void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }

        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public String getId() {
        return id;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getOriginName() {
        return originName;
    }

    public double getOriginLat() {
        return originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public double getWeight() {
        return weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public String getPriority() {
        return priority;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public String getSelectedProviderName() {
        return selectedProviderName;
    }

    public double getSelectedCost() {
        return selectedCost;
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public int getSelectedEstimatedDays() {
        return selectedEstimatedDays;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}