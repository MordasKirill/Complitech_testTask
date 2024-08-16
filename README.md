**To launch project, please, update application.properties located in /resources**

1. SQL create tables, SP and insert a few entries is provided in /resorces/sql
2. To modify run configuration (port, sql user, pass, url) update application.properties located in /resources
3. Postman request collection is located in root project folder, file 'Test.postman_collection.json'
4. Main part of endpoints requires auth, use /authenticate to get JWT token, provide username and pass in body
   Sample:
    {
        "username":"test",
        "password":"test"
    }
   Use JWT token in other requests: Authentication-> Auth type ->
