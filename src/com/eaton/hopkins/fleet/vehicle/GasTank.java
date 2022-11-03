package com.eaton.hopkins.fleet.vehicle;

public class GasTank {
	private final float gallonCapacity;
	private float fuelLevel;
	
	public GasTank(float gallonCapacity) {
		this.gallonCapacity = gallonCapacity;
		fuelLevel = 0f;
	}
	
	public float getFuelLevel() {
		return fuelLevel;
	}
	
	public float getGallonCapacity() {
		return gallonCapacity;
	}
	
	public void addFuel(float gallons) {
		if (gallons + fuelLevel > gallonCapacity)
			fuelLevel = gallonCapacity;
		else 
			fuelLevel += gallons;
	}
	
	public void useFuel(float gallons) {
		if (fuelLevel - gallons < 0f) {
			fuelLevel = 0f;
		} else {
			fuelLevel -= gallons;
		}
	}

	public float getNeededFuel() {
		return getGallonCapacity() - getFuelLevel();
	}
}
