package com.eaton.hopkins;

import com.eaton.hopkins.fleet.truck.FuelTruck;
import com.eaton.hopkins.fleet.truck.RefuelingGasTank;
import com.eaton.hopkins.fleet.vehicle.UtilityVehicle;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CentralMonitoring {
	private final Map<Integer, UtilityVehicle.FuelReport> fuelLevels;
	public static final float LOW_FUEL_GALLON_THRESHOLD = 3;
	private final Dispatch dispatch;
	private boolean receivingMessages;

	public CentralMonitoring(Dispatch dispatch) {
		fuelLevels = Collections.synchronizedMap(new HashMap<>());
		this.dispatch = dispatch;
		receivingMessages = true;
	}

	public synchronized void updateVehicleFuel(int vehicleId, UtilityVehicle.FuelReport fuelReport)  {
		if (receivingMessages) {
			fuelLevels.put(vehicleId, fuelReport);
			trackFuelLevels(vehicleId, fuelReport);
			if (fuelReport.fuelLevel <= LOW_FUEL_GALLON_THRESHOLD) {
				receivingMessages = false;
				alertDispatchLowFuel();
			}
		}
	}

	public void trackFuelLevels(int vehicleId, UtilityVehicle.FuelReport fuelReport) {
		String s = String.format("Message Received: Vehicle Id: %02d, Current Fuel: %.1f, Fuel Needed: %.1f",
				vehicleId, fuelReport.fuelLevel, fuelReport.neededFuel);
		System.out.println(s);
	}

	private void alertDispatchLowFuel()  {
		dispatch.alertFuelLow();
	}

	public UtilityVehicle.FuelReport getFuelReportFor(int vehicleId) {
		return fuelLevels.get(vehicleId);
	}

	public float getTotalFuelNeeded() {
		float totalFuelNeeded = 0;
		for(Map.Entry<Integer, UtilityVehicle.FuelReport> vehicleFuelEntry : fuelLevels.entrySet()) {
			totalFuelNeeded += vehicleFuelEntry.getValue().neededFuel;
		}
		return totalFuelNeeded;
	}

	private FuelTruck appendTruckToList(List<FuelTruck> trucks) {
		var truck = new FuelTruck(trucks.size() + 1);
		truck.refuelingTank.addFuel(RefuelingGasTank.GALLONS_RESERVED);
		trucks.add(truck);
		return truck;
	}
		
	public List<FuelTruck> getFuelTrucksFor(float totalFuelNeeded) {
		List<FuelTruck> fuelTrucks = new LinkedList<>();
		appendTruckToList(fuelTrucks);
		
		fuelLevels.entrySet().stream()
			.sorted(Comparator.comparing(e -> -e.getValue().neededFuel))
			.forEach(e -> {
				var truck = fuelTrucks.get(fuelTrucks.size() - 1);
				var capacity = FuelTruck.MAX_TRUCK_REFUEL - truck.refuelingTank.getFuelLevel();
				if (capacity <= e.getValue().neededFuel)
					truck = appendTruckToList(fuelTrucks);

				truck.assignVehicleToRefuel(e.getKey());
				truck.refuelingTank.addFuel(e.getValue().neededFuel);				
			});

		return fuelTrucks;
	}
}
