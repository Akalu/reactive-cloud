db.createUser({
        user: "dbadmin",
        pwd: "dbadmin",
        roles: [
            {
                role: "readWrite",
                db: "admin"
            },
            {
                role: "readWrite",
                db: "test"
            }
        ]
});

db.createCollection("image");

db.image.insert
(
	{
		"id" : "1",
		"name" : "apple.jpg",
		"owner": "user"
	}
)

db.image.insert
(
	{
		"id" : "2",
		"name" : "orange.jpg",
		"owner": "user"
	}
)

