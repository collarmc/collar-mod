##Forge needs Java 1.8 in IntelliJ

**Set the project SDK to Java 16 in `Project Structure`**

*Then*  
Set `forge-1.12`, `forge-1.12.main` and `forge-1.12.test` JDK to Java 1.8.

###Debug

1. `gradle :forge-1.12:genIntelliJRuns`  
2. Then edit the configuration and set the JDK to 1.8 and the module to `collar-mod.1.12-forge.main`
