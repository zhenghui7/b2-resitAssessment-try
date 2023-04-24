package ibf2022.batch2.paf.server.models;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

// Do not change this file
public class Restaurant {

	private String restaurantId;
	private String name;
	private String address;
	private String cuisine;
	private List<Comment> comments = new LinkedList<>();

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getRestaurantId() {
		return this.restaurantId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return this.address;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

	public String getCuisine() {
		return this.cuisine;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Comment> getComments() {
		return this.comments;
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
	}

	@Override
	public String toString() {
		return "Restaurant{restaurantId=%s, name=%s, address=%s, cuisine=%s, comments=%s"
				.formatted(restaurantId, name, address, cuisine, comments);
	}

	public static Restaurant createFromDoc(Document d) {
		Restaurant restaurant = new Restaurant();

		restaurant.setRestaurantId(d.getString("restaurantId"));
		restaurant.setName(d.getString("name"));
		restaurant.setCuisine(d.getString("cuisine"));

		String address = (d.getString("building") + " , " + d.getString("street") + " , " + d.getString("zipcode"));
		// String address = Address.convertFromDoc((Document) d.get("address"));
		String borough = d.getString("borough");
		restaurant.setAddress(address + " , " + borough);

		List<Document> dComments = d.getList("comments", Document.class);
		restaurant.setComments(dComments.stream().map(c -> Comment.convertFromDoc(c)).toList());

		return restaurant;
	}

	public JsonObjectBuilder toJSONRestaurant() {
		return Json.createObjectBuilder()
				.add("restaurantId", this.restaurantId)
				.add("name", this.name);
	}

	public JsonObject toJSONId() {

		JsonArrayBuilder JsArr = Json.createArrayBuilder();
		for (Comment c : comments) {
			JsArr.add(c.toJSONObjectBuilder());
		}

		return Json.createObjectBuilder()
				.add("restaurant_id", this.getRestaurantId())
				.add("name", this.getName())
				.add("cuisine", this.getCuisine())
				.add("address", this.getAddress())
				.add("comments", JsArr)
				.build();
	}

}
