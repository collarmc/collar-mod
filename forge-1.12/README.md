# ~~Issues with forge 1.12:~~
## ~~And how to fix it~~  
# Fixed in latest ForgeGradle.  
I don't know, when...



## The old docs: Jump to `Debugging`
you should be able to do:  
1. `gradle forge-1.12:genIntelliJRuns`  
2. Then edit the configuration and set the JDK to 1.8 and the module to `collar-mod.1.12-forge.main`

But this won't work

gradle throws an error, when you set up the classpath:  
```
Exception in thread "main" java.lang.IllegalArgumentException: Invalid descriptor: Ljava/nio/file/attribute/UserPrincipal;
at org.jetbrains.java.decompiler.struct.consts.LinkConstant.resolveDescriptor(LinkConstant.java:140)
at org.jetbrains.java.decompiler.struct.consts.LinkConstant.initConstant(LinkConstant.java:130)
...
```
Gradle can not decompile Forge with Java 16, what is not an issue for building the mod,  
but a crash when you try to debug and/or see the Forge sources.    
If you didn't see this error, or you can launch Forge 1.12, you probably have a cache already.
### workarounds:  
The decompiled forge should be cached, we only need that cache.  
**Do only one from the followings:**

- **import forge MDK with J1.8:** When you import the default Forge MDK, it will write the cache.  
Downloaded from [files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html)  
Then reload this project, so the classpath will be updated.

- **Import this without J16 required modules, with J1.8** If you import this project using J1.8, that is another working solution.  
After the cache has been generated, re-enable the modules, set gradle JDK to J16, and it will work.

- ~~**Borrow cache from another PC**~~: this is often a very bad idea, but can work.  
*the affected cache files (we'll need to supply these in some ways to work)*:
```
~\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.12.2-14.23.5.2855\forge-1.12.2-14.23.5.2855-decomp.jar
~\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.12.2-14.23.5.2855\forge-1.12.2-14.23.5.2855-decomp.jar.input
~\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.12.2-14.23.5.2855\forge-1.12.2-14.23.5.2855-decomp.jar.sha1
```

## Debugging  
After you have the right classpath, you can debug.  
run `gradle genIntellijRuns`  
Then you'll have the `forge-1.12 runClient` option in the debug configurations.  
### Edit it:
Set the module from `<no module>` to `collar-mod.forge-1.12.main`  
  
If the run JDK is not `Java 1.8`, set it to `java 1.8`  

**And you are ready to go**  
  
  
link-to-forum:  
https://forums.minecraftforge.net/topic/93803-crashing-on-the-1122-fml/  

`1.12 is no longer supported on this forum.`   
well, this is why I don't like Forge.
