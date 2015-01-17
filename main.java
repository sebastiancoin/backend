import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;

import java.util.List;
import java.util.Set;

public class MainClass 
{
	//MongoClient mongoClient = new MongoClient();
	//DB db = mongoClient.getDB("assassin");
	//DBCollection coll = db.getCollection("users");

	// Adds a user to the "users" collection
	// loc is an array containing [latitude, longitude]
	public String addUser(String name, BufferedImage image, double[] loc)
	{
        return User.addUser(name, image, loc);
	}
}
public class User
{
	MongoClient mongoClient = new MongoClient();
	DB db = mongoClient.getDB("assassin");
	DBCollection coll = db.getCollection("users");

	DBObject document;

	//Initializes a user
	public void User(String id)
	{
		document = coll.findOne(new BasicDBObject("_id", id));
	}

	public void getNearby()
	{
		List location = new ArrayList();
		location.add(new double[] {document.get("loc")[0], document.get("loc")[1]});	// Center of circle
		location.add((double)1/138);	// Radius
		BasicDBObject query = new BasicDBObject("loc", new BasicDBObject("$within", new BasicDBObject("$center", circle)));
	}

	public static String addUser(String name, BufferedImage image, double[] loc)
	{
		BasicDBObject user = new BasicDBObject("name", name)
        	.append("image", image)
        	.append("loc", loc);
        coll.insert(user);
        return user.getObjectId("_id");
	}
	public void update(double[] new_loc)
	{
		
	}
}

