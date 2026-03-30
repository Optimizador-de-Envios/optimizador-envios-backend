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

    @PrePersist
    void assignIdIfMissing() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getSelectedProviderName() {
        return selectedProviderName;
    }

    public void setSelectedProviderName(String selectedProviderName) {
        this.selectedProviderName = selectedProviderName;
    }

    public double getSelectedCost() {
        return selectedCost;
    }

    public void setSelectedCost(double selectedCost) {
        this.selectedCost = selectedCost;
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(String selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    public int getSelectedEstimatedDays() {
        return selectedEstimatedDays;
    }

    public void setSelectedEstimatedDays(int selectedEstimatedDays) {
        this.selectedEstimatedDays = selectedEstimatedDays;
    }
}