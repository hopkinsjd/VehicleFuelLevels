package com.eaton.hopkins.fleet.truck;

import com.eaton.hopkins.fleet.vehicle.GasTank;

public class RefuelingGasTank extends GasTank {
	public static final float GALLONS_RESERVED = 5;

	public RefuelingGasTank(float gallonCapacity) {
		super(gallonCapacity);
	}
}
