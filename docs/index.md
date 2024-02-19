## Krypt

[![Integration](https://github.com/mehdiyari/krypt/actions/workflows/Integration.yml/badge.svg)](https://github.com/mehdiyari/krypt/actions/workflows/Integration.yml) ![](https://img.shields.io/badge/License-Apache%20-green?style=plastic) <br>

Krypt is a safe place on Android phones where we can store encrypted photos, videos, voices, texts,
and
files without concern about privacy.

![](assets/krypt.jpg)

## Architecture

Currently, we use Domain, Data and UI layer architecture. For the UI layer, we use
Model-View-ViewModel (MVVM) as presentation layer architecture, a popular design pattern used in
software development, particularly in building user interfaces. MVVM aims to separate the concerns
of data management, user interface rendering, and user interactions in a clean and maintainable way.
Also, we follow the repository patterns for the Data layer, and our Domain layer, which contains use
cases, is optional.

## Tests

We write unit tests for all functionalities, including repositories, use cases, data sources,
utility classes and view models with mockk, Junit and some standard libraries for testing in
Android.

## Stack

![](https://img.shields.io/badge/Kotlin-%20-blue) ![](https://img.shields.io/badge/Hilt-%20-green) ![](https://img.shields.io/badge/Compose-%20-blue) ![](https://img.shields.io/badge/Gradle-%20-yellowgreen) ![](https://img.shields.io/badge/MVVM-%20-blue) ![](https://img.shields.io/badge/NavComponent-%20-brightgreen) ![](https://img.shields.io/badge/ExoPlayer-%20-lightgrey) ![](https://img.shields.io/badge/Mockk-%20-lightblue) ![](https://img.shields.io/badge/Coroutines-%20-yellowgreen) ![](https://img.shields.io/badge/Moshi-%20-orange) ![](https://img.shields.io/badge/Room-%20-purple) ![](https://img.shields.io/badge/Espresso-%20-yellow) ![](https://img.shields.io/badge/Junit-%20-green)
