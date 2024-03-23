# Task - My Fashion Trunk
Check it out here - [Live version](https://myfashiontrunk.rhoopoe.com)
## Building and running application
You must supply your own AWS credentials if you wish to run this application locally. Your aws directory must contain a credentials file and a config file with your aws region. Keep in mind that that your selected region must support S3 buckets and Rekognition.
### Method A - Docker
- Clone repository
- Run command `docker compose up -d` in repository root
- Application can be accessed on `http://localhost:8080`
### Method B - The hard way
For this method you must have postgreSQL and Java JRE (or JDK) installed on your machine. You must create a database and user whose details match those that are specified in the backend directory application.yml configuration file. 
- Clone repository
- Navigate to frontend directory, which contains a package.json file
- Run command `npm i`
- Run command `npm run dev`. This will start a development server at `http://localhost:5173`
- Navigate to backend directory, run command `./mvnw clean package`, this will create a .jar file in the target directory
- Run command `java -jar myfashiontrunk-0.0.1-SNAPSHOT.jar`. This will start the spring boot application