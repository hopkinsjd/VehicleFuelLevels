package com.eaton.hopkins;

import com.eaton.hopkins.fleet.truck.FuelTruck;
import com.eaton.hopkins.fleet.truck.RefuelingGasTank;
import com.eaton.hopkins.fleet.vehicle.UtilityVehicle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

	public List<FuelTruck> getFuelTrucksFor(float totalFuelNeeded) {
		List<FuelTruck> fuelTrucks = new LinkedList<>();
		Iterator<Map.Entry<Integer, UtilityVehicle.FuelReport>> fuelLevelEntries =
				fuelLevels.entrySet().iterator();
		Map.Entry<Integer, UtilityVehicle.FuelReport> curEntry = null;
		if (fuelLevelEntries.hasNext())
			curEntry = fuelLevelEntries.next();

		float truckRefuelSum = 0;
		int fuelTruckId = 1;
		FuelTruck fuelTruck = new FuelTruck(fuelTruckId);
		boolean done = false;
		while (!done && curEntry != null) {
			float curNeededFuel = curEntry.getValue().neededFuel;
			float potentialTruckRefuelSum = curNeededFuel + truckRefuelSum;
			if (potentialTruckRefuelSum < FuelTruck.MAX_TRUCK_REFUEL) {
				truckRefuelSum += curNeededFuel;
				fuelTruck.assignVehicleToRefuel(curEntry.getKey());
				fuelTruck.refuelingTank.addFuel(curNeededFuel);
				totalFuelNeeded -= curNeededFuel;
				if (fuelLevelEntries.hasNext())
					curEntry = fuelLevelEntries.next();
				else
					done = true;
			} else {
				fuelTruck.refuelingTank.addFuel(RefuelingGasTank.GALLONS_RESERVED);
				fuelTrucks.add(fuelTruck);
				fuelTruck = new FuelTruck(++fuelTruckId);
				truckRefuelSum = 0;
			}
		}
		if (fuelTruck.assignedVehicleSize() > 0) {
			fuelTruck.refuelingTank.addFuel(RefuelingGasTank.GALLONS_RESERVED);
			fuelTrucks.add(fuelTruck);
		}
		return fuelTrucks;
	}

}
