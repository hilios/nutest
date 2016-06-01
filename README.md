Nutest
======

[![Build Status](https://travis-ci.org/hilios/nutest.svg?branch=master)](https://travis-ci.org/hilios/nutest)

A reward system to customers invites.

- [API Documentation](#api-documentation)
- [Web interface](https://hilios.github.io/nutest)
- [Production API](https://nutest.herokuapp.com)

### Development

Run the develpoment server via `sbt` and open your browser at [http://localhost:9000](http://localhost:9000)

```shell
$ sbt run
# ...
[info] p.c.s.NettyServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000

(Server started, use Ctrl+D to stop and go back to the console...)

[info] play.api.Play - Application started (Dev)
```

### Unit test

```shell
$ sbt test
```

## API Documentation

#### GET /

Returns the API name and version.

```json
{
    "name": "nutest",
    "version": "1.0.0"
}
```

#### GET /rewards

Returns the current rewards for each user.

```json
{
    "1": 2.75,
    "2": 0,
    "3": 1,
    "4": 0,
    "5": 0,
    "6": 0
}
```

#### POST /rewards

Overwrite the current invitation list and return the updated rewards list.

**Request:**

Send a `.txt` file through a multipart form:

```html
<form action="/rewards" method="POST" enctype="multipart/form-data">
    <input type="file" name="invites">
    <button>Submit</button>
</form>
```

**Response:**

```json
{
    "1": 1.75,
    "2": 1.5,
    "3": 1,
    "4": 0,
    "5": 0
}
```

#### PUT /rewards

Add a new invitation to the current list and return the updated rewards list.

**Request:**

```json
5 7
```

**Response:**

```json
{
    "1": 2.75,
    "2": 0,
    "3": 1.5,
    "4": 1,
    "5": 0
}
```