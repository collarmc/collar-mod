# Glue 1.20.1 without Forge  

To make it work, I had to change glue to be a fabric mod, not an arch common module  

If forge will come out for MC 1.19, do it back:

```diff
@@ -8,7 +8,7 @@

architectury{
injectInjectables = false
-    common(false)
+    common()
     }

dependencies {

```
