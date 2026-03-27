package com.sofka.optimizador_envios_backend.config;

public class ProveedorConfig {

    private double costoBase;
    private double precioPorKg;
    private double precioPorKm;
    private double kmPorDia;

    public ProveedorConfig() {}

    public ProveedorConfig(double costoBase, double precioPorKg, double precioPorKm, double kmPorDia) {
        this.costoBase = costoBase;
        this.precioPorKg = precioPorKg;
        this.precioPorKm = precioPorKm;
        this.kmPorDia = kmPorDia;
    }

    public double getCostoBase() { return costoBase; }
    public void setCostoBase(double costoBase) { this.costoBase = costoBase; }

    public double getPrecioPorKg() { return precioPorKg; }
    public void setPrecioPorKg(double precioPorKg) { this.precioPorKg = precioPorKg; }

    public double getPrecioPorKm() { return precioPorKm; }
    public void setPrecioPorKm(double precioPorKm) { this.precioPorKm = precioPorKm; }

    public double getKmPorDia() { return kmPorDia; }
    public void setKmPorDia(double kmPorDia) { this.kmPorDia = kmPorDia; }
}
