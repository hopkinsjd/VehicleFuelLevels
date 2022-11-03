package com.eaton.hopkins;

import com.eaton.hopkins.fleet.Fleet;
import com.eaton.hopkins.fleet.truck.FuelTruck;
import com.eaton.hopkins.fleet.vehicle.UtilityVehicle;

import java.util.List;

public class Dispatch {
	private final Fleet vehicleFleet;
	private final CentralMonitoring centralMonitoring;

	public Dispatch() {
		centralMonitoring = new CentralMonitoring(this);
		vehicleFleet = new Fleet(centralMonitoring);
	}

	public void directFleet() {
		vehicleFleet.sendUtilityVehicles();
	}

	public void alertFuelLow() {
		try {
			vehicleFleet.stopUtilityVehicles();
		} catch (InterruptedException e) {
			System.out.println("Dispatch: vehicleFleet.stopUtilityVehicles error: "
					+ e.getMessage());
		}
		float totalFuelNeeded = centralMonitoring.getTotalFuelNeeded();
		assignFuelTrucksFor(totalFuelNeeded);
	}


	public void assignFuelTrucksFor(float totalFuelNeeded) {
		List<FuelTruck> fuelTrucksRequired = centralMonitoring.getFuelTrucksFor(totalFuelNeeded);
		reportFuelTruckAssignments(fuelTrucksRequired);
	}

	private void reportFuelTruckAssignments(List<FuelTruck> fuelTrucks) {
		for (FuelTruck fuelTruck : fuelTrucks) {
			String s1 = String.format("Fuel Truck Id %02d going out with %.1f gallons of fuel for utility vehicles:",
					fuelTruck.getId(), fuelTruck.refuelingTank.getFuelLevel());
			System.out.println(s1);

			for (int i = 0; i < fuelTruck.assignedVehicleSize(); i++ ) {
				Integer uvId = fuelTruck.getAssignedVehicleId(i);
				UtilityVehicle.FuelReport fuelReport = centralMonitoring.getFuelReportFor(uvId);
				String s2 = String.format("	Vehicle Id: %02d, Current Fuel: %.1f, Fuel Needed: %.1f",
						uvId, fuelReport.fuelLevel, fuelReport.neededFuel);
				System.out.println(s2);
			}
		}
	}
}
