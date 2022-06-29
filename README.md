
# Zinkworks Coding Challenge - ATM Service
The challenge is to replicate how ATM machine works in Cash Withdrawl and balance enquiry
The application should receive the data, process the operations and then output the results, it is
responsible for validating customer account details and performing basic operations as described by
API requirements:
- User (assume any rest client – curl, postman, browser) should be able to request a balance
  check along with maximum withdrawal amount (if any),
- User should be able to request a withdrawal. If successful - details of the notes that would
  be dispensed along with remaining balance,
- If anything goes wrong, user should receive meaningful message, and there should be no
  changes in user’s account,

All the api's are secured endpoints through basic-auth with username/password.

### Technologies Used
- Java 11
- Spring Boot 2.5.4
- Postgres latest docker version
- Docker
- Gradle
- Swagger for documentation.

### Postgres
Postgres is being used as relational database. It is embedded inside the docker.   
During docker compose itself the schema and tables were created, I haven't used any   
migration tools like flyway and liquibase. Typically, to save the time.

### API Details
Method -- POST - /api/cash/withdraw - Allows the user to withdraw cash.
#### Error Codes
- The Account Number =***account-number***, not found. (When account-number not exist in the system)
- The Account Number => ***account-number***, didn't had enough cash to dispense. (When account-number has funds less than the requested amount).
- The provided PIN is not correct - When user provided incorrect PIN for the transaction.
- ATM balance is low. Can't dispense the requested amount. (When ATM is short of cash).
- ATM Doesn't had requested amount in denominations. (When ATM can't dispense the cash, as the denominations are not available)

Method -- GET - /api/balance/{account-number}?pin={pin} - Check Balance from account.
#### Error Codes
- The Account Number =***account-number***, not found. (When account-number not exist in the system).
- The provided PIN is not correct - When user provided incorrect PIN for the transaction.

### Decisions Taken During the implementation
- I have considered the overdraft as a credit. So user can effectively withdraw cash balance + withdraw. At this time the balance will go to negative.
- I have limited the withdrawl amount to 10000 per transaction. If it is more than that the API throws exception.
- The deominations will be dispensed as per the currency value from decreasing order. Its nor random. It properly propages till the smallest currency value.
``` txt
For Ex: Consider at ATM, we have 10 $50, 20 $20, 10 $10, 5 $ 20.
Transaction 1 - Amount requested $175 - Denominations are -> $50 - 3, $20 - 1, $5 - 1.
Transaction 2 - Amount requested $100 - Denominations are -> $50 - 2.
Transaction 3 - Amount requested $150 - Denominations are -> $50 - 3.
Transaction 4 - Amount requested $200 - Denominations are -> $50 - 2, $20 - 5.
Transaction 5 - Amount requested $105 - Denominations are -> $20 - 5, $5 - 1.
Transaction 6 - Amount requested $119 - Error denominations not available.
```
### Test Cases and Coverage
#### Unit test cases
There were unit test cases the coverage is 97%.
#### Integration test cases
The integration test cases are there using `testcontainers`. For API Usages.


### How to Start application.
The application can be deployed in docker container. All its resources will deploy   
along with resources. For simplification, I created two shell scripts
- deploy.sh - which builds and deploys in to docker.
- stop.sh - which stops the application and removes volumes
- Fo to root directory of project.
- First chmod the shell scripts
```shell  
chmod 755 *```  
  
#### To deploy the application  
```shell  
./deploy.sh  
```  
Wait for some time you will application start up message from console.

#### To Stop the application.
```shell  
./stop.sh  
```  

### Swagger
I have used swagger for documentation. Please visit the url http://localhost:9011/swagger-ui.html

### Curl Commands
Please find them under the directory requests in the project directory.  
