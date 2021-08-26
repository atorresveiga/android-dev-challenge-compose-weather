# Blue Cloud

![Workflow result](https://github.com/atorresveiga/android-dev-challenge-compose-weather/workflows/Check/badge.svg)

## :scroll: Description
Blue Cloud is another weather app that attempts to use the latest cutting edge libraries and tools. As a summary:
 * Entirely written in Kotlin.
 * UI completely written in Jetpack Compose (Sun, Moon, Clouds etc).
 * Uses Kotlin Coroutines throughout.
 * Uses many of the Architecture Components, including: Room, WorkManager, Navigation.
 * Uses Hilt for dependency injection

## :bulb: Motivation and Context
This project started as an Android Dev Challenge that I had not the time to finish. The idea, animations and the and all the crazy things I wanted to try with Jetpack Compose stayed spinning in my head though. Therefore, I decided to keep working on it and also use it to try many other interesting libraries that I had no chance yet to use in a real project.

## :construction: Development Setup
You require the latest Android Studio Arctic Fox release to be able to build the app. This is because the project is written in Jetpack Compose

### API keys
You need to supply API / client key (OpenWeather), user name (GeoNames) and contact email (MetNo) for the various services the app uses:

 * [GeoNames](https://www.geonames.org/)
 * [OpenWeather](https://openweathermap.org/api)
 * [MetNo](https://api.met.no/weatherapi/)

Once you obtain the keys and user, you can set them in your `~/.gradle/gradle.properties`:

```
# Get this from https://openweathermap.org/ 
OpenWeatherAPIKey = "[Open Weather API Key]"

# Create a new user in https://www.geonames.org/
GeoNamesUser = "[GeoNames User]"

# An email address
MetNoContact = "[MetNo Contact]"
```

If you don't want to do this setting, you can always select the fake flavor and run the app with random data

## :camera_flash: Screenshots

### Hourly Navigation
<img src="/results/screenshot_1.gif" width="260">&emsp;<img src="/results/screenshot_3.gif" width="260">

### Active/Inactive/Idle
<img src="/results/screenshot_2.gif" width="260">

### Tablet
<img src="/results/screenshot_4.png" width="260">&emsp;<img src="/results/screenshot_5.png" width="260">

## Contributions
If you've found an error in this sample, please file an issue.

## License
```
Copyright 2020 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```