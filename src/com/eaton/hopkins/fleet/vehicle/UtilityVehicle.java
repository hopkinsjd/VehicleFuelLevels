package com.eaton.hopkins.fleet.vehicle;

import com.eaton.hopkins.CentralMonitoring;

public class UtilityVehicle implements Runnable{
	public final GasTank gasTank;
	public static final float GAS_TANK_GALLON_CAPACITY = 15;
	private final int id;
	private static final float MILES_PER_GALLON = 8;
	private static final float MILES_PER_DRIVE = 3 * MILES_PER_GALLON;
	private static final int DRIVE_TIME_MILLISECONDS = 500; // <---------------------

	private final CentralMonitoring centralFuelMonitoring;

	public UtilityVehicle(int id, CentralMonitoring centralMonitoring) {
		gasTank = new GasTank(GAS_TANK_GALLON_CAPACITY);
		gasTank.addFuel(GAS_TANK_GALLON_CAPACITY);
		this.id = id;
		this.centralFuelMonitoring = centralMonitoring;
		if (centralFuelMonitoring == null)
			throw new IllegalArgumentException(
					"Utility Vehicle: No central fuel monitoring provided for utility vehicle "
							+ id);
		else
			reportFuelLevel();
	}

	public int getId() {
		return id;
	}

	public void drive(float miles) {
		float fuelAvailable = gasTank.getFuelLevel();
		float gallonsNeeded = miles / MILES_PER_GALLON;

		if (fuelAvailable > 0) {
			gasTank.useFuel(Math.min(gallonsNeeded, fuelAvailable));
		}
	}

	public synchronized void reportFuelLevel() {
		FuelReport fuelReport = new FuelReport(gasTank.getFuelLevel(),
				gasTank.getNeededFuel());
		centralFuelMonitoring.updateVehicleFuel(getId(), fuelReport);
	}

	public static class FuelReport {
		FuelReport(float fuelLevel, float neededFuel) {
			this.fuelLevel = fuelLevel;
			this.neededFuel = neededFuel;
		}
		public float fuelLevel;
		public float neededFuel;
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method <code>run</code> is that it may
	 * take any action whatsoever.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		var gasTankThread = new Thread(gasTank); // <---------------------
		gasTankThread.start(); // <---------------------

		while (gasTank.getFuelLevel() > CentralMonitoring.LOW_FUEL_GALLON_THRESHOLD) {
			drive(MILES_PER_DRIVE);
			try {
				Thread.sleep(DRIVE_TIME_MILLISECONDS);
			} catch (InterruptedException e) {
				//System.out.println("Utility Vehicle " + getId() + ": stopping");
				break;
			}
			reportFuelLevel();
		}

		gasTankThread.interrupt(); // <---------------------
	}
}
