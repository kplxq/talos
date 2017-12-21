cd ../talos-es-shaded
call mvn clean install -Dmaven.test.skip=true
cd ..
call mvn clean install -Dmaven.test.skip=true
