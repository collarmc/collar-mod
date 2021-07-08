#Collar mod
//TODO general description about collar-mod

##Forge 1.12 needs Java 1.8
###Set it up in IntelliJ:  

**Set the project SDK to Java 16 in `Project Structure`**

*Then*  
Set `forge-1.12`, `forge-1.12.main` and `forge-1.12.test` JDK to Java 1.8.


Using 1.16 or 1.17 with JDK16 is fine.

###Debug

1. `gradle :forge-1.12:genIntelliJRuns`  
2. Then edit the configuration and set the JDK to 1.8 and the module to `collar-mod.1.12-forge.main`
  
**1.16**  
Forge 1.16 debug won't launch, even if it can compile with JDK16  
  
Fix it by adding this to the launch arguments:  
`--add-exports=java.base/sun.security.util=ALL-UNNAMED --add-opens=java.base/java.util.jar=ALL-UNNAMED`  
~~Or you can just make it to debug with old jdk.~~  
  
   
###Warning:    
Java `ByteBuffer` had some changes after J1.8, the return type.  
Some function, what had `Buffer` return type, has `ByteBuffer` return in J9+ Java.  
These can cause errors (`NoSuchMethodError`), when you compile with J16, and then you use that in J1.8  
(`targetCompatibility = 1.8` does not help)  
You can avoid these by casting the ByteBuffer to `Buffer` before invoking the method.
```java
ByteBuffer buf;

//J1.8: public Buffer position(int newPosition);
//19+ : public ByteBuffer position(int newPosition);
        
((Buffer)buf).position(someInt); 
//It will search for a method, what returns with Buffer. A method what returns with ByteBuffer will be accepted.
        
buf.position(someInt);
//It will search for a function, what returns with ByteBuffer, but there is only one, what returns with Buffer.
```
If you use the first one, it will solve the issue.  
Example in: [[KosmX/emotes]](https://github.com/KosmX/emotes/blob/1911036abcb30b67de4b3cc2609e6414f33d766a/emotesCommon/src/main/java/io/github/kosmx/emotes/common/network/objects/EmoteDataPacket.java#L122)