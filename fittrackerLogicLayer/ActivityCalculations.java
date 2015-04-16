/*
 * Author: Matthew Dalton [C00096264]
 * Description: Provides calculations for calories being burned
 * 
 */
package itcarlow.c00096264.fittrackerLogicLayer;

public class ActivityCalculations {
	// Calculate calories burned For Running
	public static double runningCaloriesBurned(double distance, double weight, int checkWeightType){
		
		double caloriesBurned = 0.0;	
		if (checkWeightType == 1)  // 1 meaning weight is in kg 
			weight = weight * 2.20462; // convert to pounds
		
		distance = distance * 0.62137119; // convert km to miles
		// Net Running calories Spent = (Body weight in pounds) x (0.63) x (Distance in miles)
		caloriesBurned = (weight * 0.63) * distance;
		return caloriesBurned;
	}
	// Calculate calories burned For Walking
	public static double walkingCaloriesBurned(double distance, double weight, int checkWeight){
		
		double caloriesBurned = 0.0;	
		if (checkWeight == 1)  
			weight = weight * 2.20462; 
		
		distance = distance * 0.62137119; 
		// Net Walking calories Spent = (Body weight in pounds) x (0.30) x (Distance in miles)
		caloriesBurned = (weight * 0.30) * distance;
		return caloriesBurned;
	}
}
