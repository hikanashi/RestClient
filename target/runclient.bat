@echo off

set ITEMID=2333
set PROPERTY=jama.properties

java -jar .\rest-client_full-1.2-SNAPSHOT.jar -item:%ITEMID%  -property:%PROPERTY%

pause