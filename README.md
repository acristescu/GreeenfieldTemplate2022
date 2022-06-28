# GreenfieldTemplate (2022 version: Molecule library + Compose)

This is a template that I use for new projects. This the bleeding edge version, if you're interested
in a more mature alternative, please see [this repo](https://github.com/acristescu/GreenfieldTemplate).
It already has some basic stuff setup, such as:

* Jetpack Compose
* Jake Wharton's Molecule [library](https://code.cash.app/the-state-of-managing-state-with-compose)
* Retrofit + OKHttp
* Coroutines
* Flows and StateFlows
* Koin dependency injection
* Unidirectional data-flow with MVI-like architecture (leveraging ViewModels)
* JUnit tests (with Mockito)
* Permission dispatcher library

The app itself connects to Flickr and displays a list of images. You can search for
particular tags or you can sort the images.

Note: there is a bug with the Molecule library that makes text fields behave strangely when the user
types too fast. See this [issue](https://github.com/cashapp/molecule/issues/63)

TODO:
* Migrate Gradle scripts to the Kotlin DSL
* CI Integration
* Maybe extract some usecases
* Compose UI tests