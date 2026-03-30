package com.sofka.optimizador_envios_backend.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "confirmaciones_pedido")
public class ConfirmacionPedidoEntity {

    @Id
    private String id;

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

        protected ConfirmacionPedidoEntity() {
        }

        private ConfirmacionPedidoEntity(
            String id,
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
        this.id = id;
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
        }

        public static ConfirmacionPedidoEntity of(
            String id,
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
            selectedEstimatedDays
        );
        }

    @PrePersist
    void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
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
}