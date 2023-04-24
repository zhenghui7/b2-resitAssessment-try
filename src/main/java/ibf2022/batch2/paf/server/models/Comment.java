package ibf2022.batch2.paf.server.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

// Do not change this file
public class Comment {

	private String restaurantId;
	private String name;
	private long date = 0l;
	private String comment;
	private int rating;

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

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return this.comment;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getDate() {
		return this.date;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getRating() {
		return this.rating;
	}

	@Override
	public String toString() {
		return "Comment{restaurantId=%s, name=%s, date=%d, comment=%s, rating=%d"
				.formatted(restaurantId, name, date, comment, rating);
	}

	public static Comment convertFromDoc(Document d) {
		Comment c = new Comment();
		c.setComment(d.getString("grade"));
		c.setDate(d.getDate("date").getTime());
		// c.setName(d.getString("name"));
		c.setRating(d.getInteger("score"));

		return c;
	}

	public JsonObjectBuilder toJSONObjectBuilder() {

		Instant instant = Instant.ofEpochMilli(this.getDate());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = localDateTime.format(formatter);

		if (this.getName() == null) {
			return Json.createObjectBuilder()
					.add("date", formattedDateTime)
					.add("comment", this.getComment())
					.add("rating", this.getRating());
		}
		return Json.createObjectBuilder()
				.add("name", this.getName())
				.add("date", formattedDateTime)
				.add("comment", this.getComment())
				.add("rating", this.getRating());

	}

	public jakarta.json.JsonObject toJSONInsert() {
		return Json.createObjectBuilder()
				.add("RestaurantId", this.getRestaurantId())
				.add("Name", this.getName())
				.add("Rating", this.getRating())
				.add("Comment", this.getComment())
				.add("Date", this.getDate())
				.build();
	}
}
