package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    // Create a Router
    Router router = Router.router(vertx);

    // Mount the handler for all incoming requests at every path and HTTP method
    router.route().handler(context -> {
      // Get the address of the request
      String address = context.request().connection().remoteAddress().toString();
      // Get the query parameter "name"
      MultiMap queryParams = context.queryParams();
      String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";
      // Write a json response
      context.json(
        new JsonObject()
          .put("name", name)
          .put("address", address)
          .put("message", "Hello " + name + " connected from " + address)
      );
    });

    // Create the HTTP server
    vertx.createHttpServer()
      // Handle every request using the router
      .requestHandler(router)
      // Start listening
      .listen(8888)
      // Print the port
      .onSuccess(server ->
        System.out.println(
          "HTTP server started on port " + server.actualPort()
        )
      );


    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(3306)
      .setHost("localhost")
      .setDatabase("socialnetwork")
      .setUser("root")
      .setPassword("ikraane133");

// Pool options
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

// Create the pooled client
    MySQLPool client = MySQLPool.pool(vertx, connectOptions, poolOptions);

    client
      .query("SELECT * FROM users WHERE username='ahmad'")
      .execute(ar -> {
        if (ar.succeeded()) {
          RowSet<Row> result = ar.result();
          System.out.println("Got " + result.size() + " rows ");
          for (Row row : result) {
            System.out.println("User" + row.getString(0) + " " + row.getString(1) + " " + row.getString(2) + " " + row.getString(3));
          }
        } else {
          System.out.println("Failure: " + ar.cause().getMessage());
        }
      });
    //testar git

  }
}
