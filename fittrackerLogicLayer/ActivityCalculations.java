/*
 * Author: Matthew Dalton
 * Description: Provides calculations for calories burned, pace,
 */
package itcarlow.c00096264.fittrackerLogicLayer;

public class ActivityCalculations {
	// Calculate calories burned For Running
	public static double RunningCaloriesBurned(double distance, double weight, int checkWeight){
		
		double caloriesBurned = 0.0;	
		if (checkWeight == 1)  // 1 meaning weight is in kg 
			weight = weight * 2.20462; // convert to pounds
		
		distance = distance * 0.62137119; // convert km to miles
		// Total Running calories Spent = (Body weight in pounds) x (0.75) x (Distance in miles)
		caloriesBurned = (weight * 0.75) * distance;
		return caloriesBurned;
	}
	// Calculate calories burned For Walking
	public static double WalkingCaloriesBurned(double distance, double weight, int checkWeight){
		
		double caloriesBurned = 0.0;	
		if (checkWeight == 1)  
			weight = weight * 2.20462; 
		
		distance = distance * 0.62137119; 
		// Total Walking calories Spent = (Body weight in pounds) x (0.) x (Distance in miles)
		caloriesBurned = (weight * 0.53) * distance;
		return caloriesBurned;
	}
}
