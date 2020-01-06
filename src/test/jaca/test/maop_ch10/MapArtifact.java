package maop_ch10;

import cartago.*;

import com.google.maps.*;
import com.google.maps.model.GeocodingResult;

public class MapArtifact extends Artifact  {

	private GeoApiContext context;
	
	void init(String apiKey) {
		context = new GeoApiContext.Builder().apiKey(apiKey).build();
	}
	
	@OPERATION void getGeoCoordinates(String place, OpFeedbackParam<Double> latit, OpFeedbackParam<Double> longit) {
		try {
			GeocodingResult[] results =  GeocodingApi.geocode(context,place).await();
			latit.set(results[0].geometry.location.lat);
			longit.set(results[0].geometry.location.lng);
		} catch (Exception ex) {}		
	}
}
