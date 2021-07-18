## Issues with forge 1.12:
### And how to fix it  

gradle throws an error:  
```
Exception in thread "main" java.lang.IllegalArgumentException: Invalid descriptor: Ljava/nio/file/attribute/UserPrincipal;
at org.jetbrains.java.decompiler.struct.consts.LinkConstant.resolveDescriptor(LinkConstant.java:140)
at org.jetbrains.java.decompiler.struct.consts.LinkConstant.initConstant(LinkConstant.java:130)
...
```
Gradle can not decompile Forge with Java 16, what is not an issue for building the mod.  
But a crash when you try to debug and/or see the Forge sources...  
**workarounds**: The decompiled forge will be cached, we only need that cache.

**import forge MDK with J1.8:** When you import the default Forge MDK, it will write the cache.  
Downloaded from [files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.12.2.html)  
Then reload this project, so the classpath will be updated.

**Import this without J16 required modules, with J1.8** If you import this project using J1.8, that is another working solution.  
After the cache has been generated, re-enable the modules, set gradle JDK to J16, and it will work.

~~**Borrow cache from another PC**~~: this is often a very bad idea, but can work.  
*the affected cache files (we'll need to supply these in some ways to work)*:
```
~\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.12.2-14.23.5.2855\forge-1.12.2-14.23.5.2855-decomp.jar
~\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.12.2-14.23.5.2855\forge-1.12.2-14.23.5.2855-decomp.jar.input
~\.gradle\caches\forge_gradle\minecraft_user_repo\net\minecraftforge\forge\1.12.2-14.23.5.2855\forge-1.12.2-14.23.5.2855-decomp.jar.sha1
```


link-to-forum:  
https://forums.minecraftforge.net/topic/93803-crashing-on-the-1122-fml/

`1.12 is no longer supported on this forum.`   
well, this is why I don't like Forge.