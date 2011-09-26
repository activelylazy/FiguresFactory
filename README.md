
# About

This project represents a common pattern:
* Hibernate model objects, all data but no behaviour
* injected "service" dependencies that are more like function libraries
* stateless helper classes that manipulate the data in the model using services

# Why is this bad?

If we were following proper OO the data and the behaviours that act on that data would be together in a single class. Instead, what we have
is a value object, and different "classes" that manipulate that data.

# Where is this from?

This is based on real, production code I have seen - anonymised and simplified to make it easier to work with.

# Where do I start?

* com.example.order.processing.OrderProcessor is an example client of the code in question. In practice, this might be a Controller in a web application
* com.example.order.processing.TradeOrder is a Hibernate model object representing a trade; i.e. buying or selling a specific asset at a certain price
* com.example.order.processing.Figures is a value object representing a price (per share) a number of shares and an amount (price * number of shares)
* com.example.order.processing.FiguresFactory is a helper object that takes a TradeOrder and creates a Figures from it

# What do I have to do?

How could we better factor these four classes to make the system clearer and more maintainable.

# What else is there?

* com.example.model.* contains a collection of dependent model classes, only shown as interfaces. The details of these are not significant
* com.example.service.* contains a collection of service-like dependencies, only shown as interfaces. The details of these are not significant
