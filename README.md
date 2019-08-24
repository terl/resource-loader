# Resource Loader

Resource Loader gives you the functions to load `resource` files even if you are loading from inside a JARs or outside a JAR. Resource Loader also supports loading shared libraries through `SharedLibraryLoader`.

<a href='https://semaphoreci.com/libly/resource-loader'> <img src='https://semaphoreci.com/api/v1/libly/resource-loader/branches/master/badge.svg' alt='Build Status'></a>



## Installation

Resource Loader is only available via Jitpack at the moment. Maven and SBT installation instructions available [here](https://jitpack.io/).

```groovy
// Top-level build.gradle
repositories {
    // ...
    maven { url 'https://jitpack.io' } // Add this line
}

dependencies {
    // ...
    implementation 'com.github.libly:resource-loader:1.1.3' // Add this line
}
```

## Usage

### Loading a file

Let's say you have a file in your `resources` folder called `test1.txt`. To get it you simply do the following:

```java
FileLoader fileLoader = new FileLoader();
File file = fileLoader.load("test1.txt");
```

Resource Loader can also load from paths and directories. Just make sure the top level directory name is somewhat unique:

```java
// The following loads test1.txt from the resources folder
// even if you supply a nested path.
File file2 = fileLoader.load("a_unique_top_level_folder/path/test1.txt");

// You can even load whole directories.
File dir = fileLoader.load("a_unique_top_level_folder/directory"); 
```

## What problem does Resource Loader solve?
Consider the scenario. You have a project with some files in the `resource` folder. You're loading those files using `getResourceAsStream` and it's working when you test it locally. But when you go to package the project as a JAR and then run it, it fails!

Resource Loader provides developers with a fool-proof way of loading files inside or outside JARs by loading the files in a temporary folder that gets deleted when the process closes.
 
Resource Loader works with shared libraries (`.dll`, `.so`, `.dylib`). Scouring the Internet to piece together a solution was a labour intensive task with many quirks and pitfalls to look out for. So we thought why not package our solution and provide it in a reusable form.

