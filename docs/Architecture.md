# Architecture

Currently, we use Domain, Data and UI layer architecture. For the UI layer, we use
Model-View-ViewModel (MVVM) as presentation layer architecture, a popular design pattern used in
software development, particularly in building user interfaces. MVVM aims to separate the concerns
of data management, user interface rendering, and user interactions in a clean and maintainable way.
Also, we follow the repository patterns for the Data layer, and our Domain layer, which contains use
cases, is optional.