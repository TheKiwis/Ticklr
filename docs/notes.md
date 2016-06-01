- Which kind of data structure should be used as primary key in the database?
  + UUID => unique across databases, servers,... but is of 128-bit - 4 times bigger than 32-bit integer.

- What's a better / more flexible way to deal with JWT?
  + see https://developer.atlassian.com/static/connect/docs/latest/concepts/understanding-jwt.html

- [x] Which is the best way to establish a central place for constructing
  resource URL?
  + The most practical solution I can think of is: for each type of resource,
    there is a class, which handles the URL Template and URL resolution with
    parameters. (e.g. UserController has UserURI, BasketController has BasketURI, ...)

- [ ] How to create / update a resource which has a relationship with other
  resource, for example a basket item that points to a product?
  + Include the ID of the product in the request body to create the basket item.
  + Another approach would be to include the whole URL of the referenced resource (the URL of the product)
    in the request body. The server will then extract the internal ID of the product from the URL.
    This is always possible because there is a 1-to-1 relationship between the product and it's reprentation / URL.

- [ ] What about adding allow GET requests to have query parameters specifying which kind of view
  they want / which kind of information they want / How deep the level of information do they want?
  + Example: `GET /events` should return information about the events including
    the associated ticket sets.  But the question is: should it returns the
    information about the ticket sets or just the link to the list.
  + We could offer both of them by enabling the client to choose through query
    parameter like `/events?ticket-sets=false` or  `/events?ticket-sets=false`
    or `/events?expand=ticket-sets,attendees`.

- [ ] Compare the architecture of Ticklr with the architecture presented by UncleBob in [Architecture the Lost Years] (https://www.youtube.com/watch?v=hALFGQNeEnU)
  s

- Authentication

  + More: https://en.wikipedia.org/wiki/Computer_access_control
  + Claims-based identity
  + OWASP

- Basket:

  + Implementing checks for number of available tickets

REST API
--------

## Semantic descriptors:

- Api dashboard

  - A User

    - __List of events__

      - __An event__

        - __List of ticket sets__: 

          - Ticket set: The reason to make ticket set a resource is that other resources, like Basket Items,
            Order Positions will later reference it.

    - __Basket__

      - __List of basket items__

  - authentication token:


