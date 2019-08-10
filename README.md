# Resource Loader

Resource Loader gives you the functions to load `resource` files easily inside JARs or outside. Resource Loader also supports loading shared libraries.

### The problem
Consider the scenario. You have a project with some files in the `resource` folder. You're loading those files using `getResourceAsStream` and it's working when you test it locally. But when you go to package the project as a JAR and then run it, it fails!

### The solution
Resource Loader provides developers with a fool-proof way of loading files inside or outside JARs and works with shared libraries (`.dll`, `.so`, `.dylib`). Scouring the Internet to piece together a solution was a labour intensive task with many quirks and pitfalls to look out for. So we thought why not package our solution and provide it in a reusable form.
