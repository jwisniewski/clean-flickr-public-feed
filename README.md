# Clean Flickr Public Feed

## The use case

A short experiment, an app for Anroid which displays a grid of recent images from the [flickr public feed](https://www.flickr.com/services/feeds/docs/photos_public).

## The architecture

I decided to write enough code to show a simple yet clean and testable architecture, more or less following the [clean architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) principles as defined by Robert C. Martin (Uncle Bob).

I didn't include any of trendy *frameworks* implementing the *clean architecture*. In my opition when Uncle Bob introduced this approach, he wanted to show us how to keep frameworks away of our domain logic. He wanted to show the architecture is not about frameworks, that frameworks should be keept aside. Then what people did? They created clean architecture frameworks;)

The project has been divided into:

### Domain
Business logic related to a photo feed and some additional logic related to tags and filtering.

### Repository
Serves the Flickr public photo feed, implements interfaces defined in domain layer, can be easily replaced by any other source of tagged photos.

### Presentation
Responsible for presentation logic, contains presenters, views and navigators (based on Model View Presenter approach).

### App
Android lifecycle and API related stuff: Activity, Fragments etc.
I allowed myself to use this layer directly to inject all the dependencies,
decided to don't add another library like dagger etc, for a few days long project.
This layer wasn't testable either, according my design, so I didn't lose anything important.

## A few words about unit tests
I used plain junit4, skipped android instrumentation tests, the time I had for the project
didn't allow me to test everything but I managed to show a few tests per layer.
