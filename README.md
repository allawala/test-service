Test service that demonstrates the service-chassis usage, by using user registration, login and basic crud as example actions.

All users created are stored in a memory map and lasts as long as the application is running

service chassis can be found at https://github.com/allawala/service-chassis

- `sbt clean compile it:test` to see how it all hangs together

- `sbt-run` to have a play around with using postman or curl

  eg

  Register
  ```
  curl -X POST \
    http://localhost:8080/v1/public/users/register \
    -H 'cache-control: no-cache' \
    -H 'content-type: application/json' \
    -H 'origin: http://localhost:8080' \
    -H 'postman-token: 8e4e9f1a-37fa-7307-396a-d355573ea526' \
    -d '{
  	"email": "test@demo.com",
  	"password": "password",
  	"firstName": "test",
  	"lastName": "user"
  }'
  ```

  Login
  ```
  curl -X POST \
    http://localhost:8080/v1/public/users/login \
    -H 'cache-control: no-cache' \
    -H 'content-type: application/json' \
    -H 'origin: http://localhost:8080' \
    -d '{
  	"email": "test@demo.com",
  	"password": "password",
  	"rememberMe": false
  }'
  ```

  **IMPORTANT** use the uuid and token returned in the login for any subsequent requests

  ```
  curl -X GET \
    http://localhost:8080/v1/secure/users/${uuid} \
    -H 'authorization: ${Bearer token}' \
    -H 'cache-control: no-cache'
  ```