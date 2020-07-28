# short-link 
A link shortening service

## Usage

### Create short link
`POST https://{host}/link?to=https://www.hinze.dev`  
`POST https://{host}/link?to=https://www.hinze.dev&expiration=2020-07-27T20:43:50.737Z`

### Use short link
`GET https://{host}/{#shortLink.id}`


## Required Environment Variables
`DB_HOST=<database URL>`  
`DB_PORT=<port>`  
`DB_USER=<user name>`  
`DB_PASS=<password>`  

## Docker
```
docker run \
  --network=<where the database is> \
  -p <port>:8080 \
  -e DB_HOST=<database URL> \ 
  -e DB_PORT=<port> \
  -e DB_USER=<user name> \
  -e DB_PASSWORD=<password> \
  jhinze/short-link
```
  
_See also docker-compose.yaml_
