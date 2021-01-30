
<img width="130" style="display: inline;" src="https://filedn.com/lssh2fV92SE8dRT5CWJvvSy/libly/resource-loader.png" />


# Resource Loader

Resource Loader gives you the functions to load `resource` files even if you are loading from inside a JARs or outside a JAR. Resource Loader also supports loading shared libraries through `SharedLibraryLoader`.

[![Build Status](https://dev.azure.com/gurpreetishere/Lazysodium/_apis/build/status/libly.resource-loader?branchName=master)](https://dev.azure.com/gurpreetishere/Lazysodium/_build/latest?definitionId=2&branchName=master)


## Installation

This is how to install the library in Gradle based projects. Maven based projects may differ.

```groovy
// Top-level build.gradle
repositories {
    // ...
    jcenter()
}

dependencies {
    // ...
    implementation 'co.libly:resource-loader:1.3.7' // Add this line
}
```

## Usage

### Loading a file

Let's say you have a file in your `resources` folder called `test1.txt`. To get it you simply do the following:

```java
File file = FileLoader.get().load("test1.txt");
```

Resource Loader can also load from paths and directories. Just make sure the top level directory name is somewhat unique:

```java
// The following loads test1.txt from the resources folder
// even if you supply a nested path.
File file2 = FileLoader.get().load("a_unique_top_level_folder/path/test1.txt");

// You can even load whole directories.
File dir = FileLoader.get().load("a_unique_top_level_folder/directory"); 
```

### Loading a shared library
Loading a shared library is just as simple. You can load one by using `loadSystemLibrary` which loads a shared library if it is already installed on the system.

```java
// Load from the system
SharedLibraryLoader.get().loadSystemLibrary("hydrogen", Hydrogen.class);
```

Or you can use `load` which loads a shared library even if it is bundled inside a JAR or not.

```java
// Load from the system
SharedLibraryLoader.get().load("hydrogen", Hydrogen.class);
```

## What problem does Resource Loader solve?
Consider the scenario. You have a project with some files in the `resource` folder. You're loading those files using `getResourceAsStream` and it's working when you test it locally. But when you go to package the project as a JAR and then run it, it fails!

Resource Loader provides developers with a fool-proof way of loading files inside or outside JARs by loading the files in a temporary folder that is deleted when the corresponding Java process that created that temp process has been destroyed.

Here are some features of Resource Loader:
 
- [x] Can be used in Android _**and**_ Java projects.
- [x] Load from _nested_ JARs up to 20 levels deep.
- [x] Resource Loader works with shared libraries (`.dll`, `.so`, `.dylib`) and can initialise them.
- [x] Can extract regular files from the `resources` folder.
- [x] Simple include this library and use the `.load` functions.