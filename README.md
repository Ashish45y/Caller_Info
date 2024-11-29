# Caller info

Welcome to Caller info, a call info app built with MVVM architecture.

## Major Highlights

- **MVVM architecture** for a clean and scalable codebase
- **Kotlin** and **Kotlin DSL**
- **Dagger Hilt** for efficient dependency injection.
- **Coroutines** and **Flow** for asynchronous programming
- **StateFlow** for streamlined state management
- **Pagination** to efficiently load and display apps
- **Notification** for alerting about the incoming call
- **Receiver**  for listen event from other apps.
- **Services** to show the popview on incomming call.
<p align="center">
<img alt="mvvm-architecture"  src="https://github.com/khushpanchal/NewsApp/blob/master/assets/News_app_architecture.jpeg">
</p>

## Features Implemented

- Show app which has microphone access.
- Filter the app on the basis of background access.
- If micophone background access show in red color otherwise it in green color.
- diplay a popview when you receive incomming call.
- it show the name and number of the caller.
## Dependency Use

- Dagger Hilt for Dependency Injection: Simplifies dependency injection
- Paging : Simplifies the implementation of paginated lists
- Broadcast reciever: listen the event from other apps.
- LifecycleServices : services aware with lifecycle to show the popview.

## How to Run the Project

- Clone the Repository:
```
git clone https://github.com/Ashish45y/Caller_Info.git
cd call_info
- Build and run the call_info.


## The Complete Project Folder Structure

|── CallerInfoApplication.kt
├── di.module
│   ├── ActivityModule.kt
│   ├── ApplicationComponent.kt
├── utils
│   ├── dispatcher
│   │   ├── DefaultDispatcherProvider.kt
│   │   └── DispatcherProvider.kt
│   ├── logger
│   │   ├── AppLogger.kt
│   │   └── Logger.kt
├── model
│   │   ├── AppInfo.kt
│   │   ├── AppInfoUiState.kt
├── di
│   ├── module
│   │   └── ApplicationModule.kt
│   └── qualifiers.kt
├── reciever
│   ├── CallReciever.kt
├── repository
│   ├── AppInfoPagingSource.kt
│   ├── AppInfoRepo.kt
├── service
│   ├── CallInfoService.kt
├── ui
│   ├── adapter
│   │   ├── AppInfoAdapter.kt
│   ├── MainActivity.kt
│   ├── theme
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodels
│       ├── AppInfoViewModel.kt

```

## If this project helps you, show love ❤️ by putting a ⭐ on this project ✌️

## Contribute to the project

Feel free to improve or add features to the project.
Create an issue or find the pending issue. All pull requests are welcome 😄


