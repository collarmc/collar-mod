# Collar mod  
//TODO general description about collar-mod  
maybe put the current stuff into a `BUILD.md` and use README as a user-manual

## Compiling submodules with Java 1.8
### Set it up in IntelliJ:

**Set the project SDK to Java 16 in `Project Structure`**

*Then*  
if you want to compile Forge 1.12 with J1.8:  
Set `forge-1.12`, `forge-1.12.main` and `forge-1.12.test` JDK to Java 1.8.  

Apparently every module can be compiled with `JDK 16`, including `forge-1.12`  

## Debug

**1.16**  
Forge 1.16 debug won't launch, even if it can compile with JDK16

Fix it by adding this to the launch arguments:  
`--add-exports=java.base/sun.security.util=ALL-UNNAMED --add-opens=java.base/java.util.jar=ALL-UNNAMED`  
~~Or you can just make it to debug with old jdk.~~

**1.12**  
~~Well, that is a bit complicated. Hope *[The Forge God, LexManos](https://github.com/LexManos)* will fix it.~~  
~~now: [forge-1.12/README](/forge-1.12/README.md)~~   

It has been fixed.  
Just set the debug Java version to Java 1.8 and the module to `collar-mod.forge-1.12.main`  
And press run/debug


### Warning:
Java `ByteBuffer` had some changes after J1.8, mostly the return type.  
Some function, what had `Buffer` return type, has `ByteBuffer` return in J9+.  
These can cause errors (`NoSuchMethodError`), when you compile with J16, and then you use that in J1.8  
(`targetCompatibility = 1.8` does not help)  
You can avoid these by casting the `ByteBuffer` to `Buffer` before invoking the method.
```java
ByteBuffer buf;

//J1.8: public Buffer position(int newPosition);
//19+ : public ByteBuffer position(int newPosition);

        ((Buffer)buf).position(someInt);
//It will search for a method, what returns with Buffer. A method what returns with ByteBuffer will be accepted.

        buf.position(someInt);
//It will search for a function, what returns with ByteBuffer, but there is what returns with Buffer.
```
Using the first one, will solve the issue.  
Example in: [[KosmX/emotes]](https://github.com/KosmX/emotes/blob/1911036abcb30b67de4b3cc2609e6414f33d766a/emotesCommon/src/main/java/io/github/kosmx/emotes/common/network/objects/EmoteDataPacket.java#L122)
