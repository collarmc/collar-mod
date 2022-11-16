[![CI](https://github.com/CaptainRexPL/collar-mod/actions/workflows/ci.yaml/badge.svg)](https://github.com/CaptainRexPL/collar-mod/actions/workflows/ci.yaml)
# Collar mod  
//TODO general description about collar-mod  
maybe put the current stuff into a `BUILD.md` and use README as a user-manual



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
