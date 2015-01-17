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
	public ObjectId addUser(String name, BufferedImage image, double[] loc)
	{
        return User.addUser(name, image, loc);
	}
	public BufferedImage getImage(String id)
	{
		User tempUser = new User(id);
		return tempUser.getImage();
	}
}

public class User
{
	MongoClient mongoClient = new MongoClient();
	DB db = mongoClient.getDB("assassin");
	DBCollection coll = db.getCollection("users");

	DBObject document;

	// Static method allows for creating new users and adding them to the database
	public static String addUser(String name, BufferedImage image, double[] loc)
	{
		BasicDBObject user = new BasicDBObject("name", name)
        	.append("image", image)
        	.append("huntId", null)
        	.append("preyId", null)
        	.append("loc", loc)
        	.append("dir", null);
        coll.insert(user);
        return user.get("_id").toString();
	}
	public BufferedImage getImage()
	{
		return document.get("image");
	}
	//Initializes a user
	public void User(String id)
	{
		id = ObjectId(id);
		document = coll.findOne(new BasicDBObject("_id", id));
	}

	// Get a list of all other users within 1/2 mile
	private BasicDBObject getNearby()
	{
		List location = new ArrayList();
		location.add(new double[] {document.get("loc")[0], document.get("loc")[1]});	// Center of circle
		location.add((double)1/138);	// Radius
		BasicDBObject query = new BasicDBObject("loc", new BasicDBObject("$within", new BasicDBObject("$center", circle)));
		return query;
	}

	// Used to update a User's location after movement
	// Also checks if there are users around to compete with
	// Returns the userId of the opponent to hunt
	public String update(double[] new_loc)
	{
		// Update 
		document.put("loc", new_loc);

		BasicBDObject foes = getNearby();
		BDCursor cursor = coll.find(query);
		if(document.get("huntId") == null)
		{
			while(cursor.hasNext())
			{
				DBObject val = cursor.next();
				if(val.get("preyId") != null)
				{
					// If user does not have a target and a target is near
					// and that target does not have someone hunting him, return new target Id
					return val.get("_id").toString();		
				}
			}
			return null;		// If user does not have a target and none is near, return null
		}
		return document.get("huntId").toString();		// If user already has a target, return that same target's ID
	}
}

