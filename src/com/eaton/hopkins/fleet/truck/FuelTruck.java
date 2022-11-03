package com.eaton.hopkins.fleet.truck;

import java.util.LinkedList;
import java.util.List;

public class FuelTruck {
	public final RefuelingGasTank refuelingTank;
	public static final float REFUELING_TANK_GALLON_CAPACITY = 40;
	public static final float MAX_TRUCK_REFUEL =
			(REFUELING_TANK_GALLON_CAPACITY - RefuelingGasTank.GALLONS_RESERVED);
	private final int id;

	private final List<Integer> assignedVehicleIds = new LinkedList<>();

	public FuelTruck(int id) {
		this.id = id;
		refuelingTank = new RefuelingGasTank(REFUELING_TANK_GALLON_CAPACITY);
	}

	public void assignVehicleToRefuel(Integer utilityVehicleId) {
		assignedVehicleIds.add(utilityVehicleId);
	}

	public Integer getAssignedVehicleId(int index) {
		return assignedVehicleIds.get(index);
	}

	public int assignedVehicleSize() {
		return assignedVehicleIds.size();
	}

	public int getId() {
		return id;
	}
}
