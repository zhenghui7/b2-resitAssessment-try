package ibf2022.batch2.paf.server.repositories;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ibf2022.batch2.paf.server.models.Comment;
import ibf2022.batch2.paf.server.models.Restaurant;

@Repository
public class RestaurantRepository {
	@Autowired
	MongoTemplate mongoTemplate;

	// TODO: Task 2
	// Do not change the method's signature
	// Write the MongoDB query for this method in the comments below
	// db.restaurants.distinct('cuisine')
	public List<String> getCuisines() {

		Query query = new Query();
		query.with(Sort.by(Sort.Direction.ASC, "cuisine"));

		List<String> cuisineList = mongoTemplate.findDistinct(query, "cuisine", "restaurants", String.class)
				.stream()
				.map(s -> s.replaceAll("/", "_"))
				.toList();
		// System.out.println(cuisineList.toString());
		return cuisineList;
	}

	// TODO: Task 3
	// Do not change the method's signature
	// Write the MongoDB query for this method in the comments below
	// Refer to restaurant_import.txt
	public List<Restaurant> getRestaurantsByCuisine(String cuisine) {

		MatchOperation matchCuisine = Aggregation.match(Criteria.where("cuisine").is(cuisine));
		ProjectionOperation projectInfo = Aggregation
				.project("name", "address.building", "address.street", "address.zipcode", "borough")
				.and("restaurant_id").as("restaurantId")
				.andExpression("{ $replaceAll: { input: \"$cuisine\", find: \"/\", replacement: \"_\" } }")
				.as("cuisine")
				.and("grades").as("comments");

		SortOperation sortByName = Aggregation.sort(Direction.ASC, "name");
		Aggregation pipeline = Aggregation.newAggregation(matchCuisine, projectInfo, sortByName);
		AggregationResults<Document> results = mongoTemplate
				.aggregate(pipeline, "restaurants", Document.class);

		List<Document> docs = results.getMappedResults();
		// System.out.println(docs.toString());
		List<Restaurant> restaurants = docs.stream()
				.map(d -> Restaurant.createFromDoc(d))
				.toList();

		// System.out.println("-------------------------------------------");
		// System.out.println(restaurants.toString());
		return restaurants;
	}

	// TODO: Task 4
	// Do not change the method's signature
	// Write the MongoDB query for this method in the comments below
	// Refer to restaurant_import.txt
	public Optional<Restaurant> getRestaurantById(String id) {

		MatchOperation matchCuisine = Aggregation.match(Criteria.where("restaurant_id").is(id));
		ProjectionOperation projectInfo = Aggregation
				.project("name", "address.building", "address.street", "address.zipcode", "borough")
				.and("restaurant_id").as("restaurantId")
				.andExpression("{ $replaceAll: { input: \"$cuisine\", find: \"/\", replacement: \"_\" } }")
				.as("cuisine")
				.and("grades").as("comments");
		Aggregation pipeline = Aggregation.newAggregation(matchCuisine, projectInfo);
		AggregationResults<Document> results = mongoTemplate
				.aggregate(pipeline, "restaurants", Document.class);

		List<Document> doc = results.getMappedResults();
		if (doc.isEmpty()) {
			return Optional.empty();
		}

		Restaurant restaurant = Restaurant.createFromDoc(doc.get(0));
		return Optional.of(restaurant);
	}

	// TODO: Task 5
	// Do not change the method's signature
	// Write the MongoDB query for this method in the comments below
	//db.comments.insert({restaurant_id: "40356442", name: "David", rating: 9, comment: "A", date: new Date(Date.now())});
	public void insertRestaurantComment(Comment comment) {
		Document doc = new Document();
		doc = Document.parse(comment.toJSONInsert().toString());
		Document newDoc = mongoTemplate.insert(doc, "comments");
		ObjectId id = newDoc.getObjectId("_id");
		System.out.println(id);
	}
}
