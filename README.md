# VehicleFuelLevels
A work site has 40 utility vehicles used for various purposes. Each vehicle has a 15 gallon gas tank, and you can assume when your application starts that all gas tanks are full.
All vehicles report their current fuel level to a central monitoring application with periodic messages providing the vehicle’s unique ID and the current fuel level. The monitoring application tracks the last known fuel level for all vehicles.
When any one vehicle reaches a minimum fuel remaining threshold of 3 gallons or less, refueling trucks will be sent out to refuel all utility vehicles. The monitoring application should determine the number of refueling trucks that must be sent out, assign trucks to specific utility vehicles, and determine the total amount of fuel that must be included in the refueling truck’s tank to fully fuel up all utility vehicles.
Each refueling truck has a capacity of 40 gallons of fuel. Each refueling truck should reserve 5 gallons of fuel more than what is known to be needed to refill the utlity vehicles, to allow that truck to fully refill vehicles which may use up a bit more fuel before the truck gets to them.
Your application should simulate the central monitoring application, and simulate the messages coming from the utility vehicles. The central monitoring application should keep
track of the latest fuel level of each vehicle until the refueling threshold is reached for any one vehicle. At that point, all messaging should be shut down, the application should produce a report of what refueling trucks are going out to refuel which utility vehicles, and how much fuel they must carry. Once that report is produced, the application should shut down.

##Additional Notes
This just needs to be a simulation. You don’t need to communicate between individual client machines and an actual server machine, or even between separate running applications; simple in-memory messaging simulation all within one running application is perfectly fine.
Fuel consumption need not be realistic. To make test runs of your application run quickly, vehicles should send messages quickly and burn through their entire fuel tanks in less than a minute.
