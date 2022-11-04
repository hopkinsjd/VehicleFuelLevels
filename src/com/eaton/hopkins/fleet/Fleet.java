package com.eaton.hopkins.fleet;

import com.eaton.hopkins.CentralMonitoring;
import com.eaton.hopkins.fleet.vehicle.UtilityVehicle;

public class Fleet {
	private final Thread[] vehicleThreads;
	public static int UTILITY_VEHICLE_COUNT = 40;

	public Fleet(CentralMonitoring centralMonitoring) {
		vehicleThreads = new Thread[UTILITY_VEHICLE_COUNT];
		for (int i = 0; i < UTILITY_VEHICLE_COUNT; i++) {
			UtilityVehicle utilityVehicle = new UtilityVehicle(i+1, centralMonitoring);
			vehicleThreads[i] = new Thread(utilityVehicle);
		}
	}

	public void sendUtilityVehicles() {
		for (Thread vehicleThread : vehicleThreads) {
			vehicleThread.start();
		}
	}

	public void stopUtilityVehicles() throws InterruptedException {
		for (Thread vehicleThread : vehicleThreads) {
			vehicleThread.interrupt();
		}
	}
}
