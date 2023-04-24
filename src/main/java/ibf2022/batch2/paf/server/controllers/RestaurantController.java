package ibf2022.batch2.paf.server.controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.batch2.paf.server.models.Comment;
import ibf2022.batch2.paf.server.models.Restaurant;
import ibf2022.batch2.paf.server.services.RestaurantService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

@RestController
@RequestMapping(path = "/api")
public class RestaurantController {

	@Autowired
	RestaurantService restaurantService;

	// TODO: Task 2 - request handler
	@GetMapping(path = "/cuisines")
	public ResponseEntity<String> getCuisinesList() {
		List<String> cuisines = restaurantService.getCuisines();

		JsonArrayBuilder jsArr = Json.createArrayBuilder();
		for (String c : cuisines) {
			jsArr.add(c);
		}

		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(jsArr.build().toString());
	}

	// TODO: Task 3 - request handler
	@GetMapping(path = "/restaurants/{cuisine}")
	public ResponseEntity<String> selectedCuisine(@PathVariable String cuisine) {
		List<Restaurant> restaurants = restaurantService.getRestaurantsByCuisine(cuisine);

		JsonArrayBuilder jsArr = Json.createArrayBuilder();
		for (Restaurant r : restaurants) {
			jsArr.add(r.toJSONRestaurant());
		}

		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(jsArr.build().toString());
	}

	// TODO: Task 4 - request handler
	@GetMapping(path = "/restaurant/{restaurant_id}")
	public ResponseEntity<String> getRestaurantById(@PathVariable String restaurant_id) {
		Optional<Restaurant> restaurant = restaurantService.getRestaurantById(restaurant_id);

		if (restaurant.isEmpty()) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.contentType(MediaType.APPLICATION_JSON)
					.body(Json.createObjectBuilder().add("Error", "Missing " + restaurant_id)
							.build().toString());
		}

		Restaurant res = restaurant.get();
		res.getComments().stream().forEach(c -> c.setRestaurantId(restaurant_id));
		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(res.toJSONId().toString());
	}

	// TODO: Task 5 - request handler
	@PostMapping(path = "/restaurant/comment", consumes = "application/x-www-form-urlencoded")
	public ResponseEntity<String> postComments(@ModelAttribute Comment comment) {

		Date d = new java.util.Date();
		long epoch = d.getTime();
		comment.setDate(epoch);

		restaurantService.postRestaurantComment(comment);
		// after successful insert
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.contentType(MediaType.APPLICATION_JSON)
				.body("");
	}
}
