#/bin/sh

REL=3.2-SNAPSHOT

# for cartago

./gradlew cartagoJar

MAVEN=$HOME/.m2/repository/org/jacamo/cartago

mkdir -p $MAVEN/$REL

cp build/dist/cartago-$REL/lib/cartago-$REL.jar $MAVEN/$REL

# send to SF
POM=$MAVEN/$REL/cartago-$REL.pom
echo '<?xml version="1.0" encoding="UTF-8"?>' > $POM
echo '<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' >> $POM
echo '<modelVersion>4.0.0</modelVersion>' >> $POM
echo '  <groupId>org.jacamo</groupId>' >> $POM
echo '  <artifactId>cartago</artifactId>' >> $POM
echo '  <version>'$REL'</version>' >> $POM
echo '</project>' >> $POM

LOCALGIT=$HOME/pro/jacamo-mvn-repo/org/jacamo/cartago
mkdir -p $LOCALGIT
cp -r $MAVEN/$REL $LOCALGIT

# for Jaca

./gradlew jacaJar
MAVEN=$HOME/.m2/repository/org/jacamo/jaca

mkdir -p $MAVEN/$REL

cp build/dist/cartago-$REL/lib/jaca-$REL.jar $MAVEN/$REL

POM=$MAVEN/$REL/jaca-$REL.pom
echo '<?xml version="1.0" encoding="UTF-8"?>' > $POM
echo '<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' >> $POM
echo '<modelVersion>4.0.0</modelVersion>' >> $POM
echo '  <groupId>org.jacamo</groupId>' >> $POM
echo '  <artifactId>jaca</artifactId>' >> $POM
echo '  <version>'$REL'</version>' >> $POM
echo '</project>' >> $POM

LOCALGIT=$HOME/pro/jacamo-mvn-repo/org/jacamo/jaca
mkdir -p $LOCALGIT
cp -r $MAVEN/$REL $LOCALGIT

cd $LOCALGIT
cd ..
git pull
git add .
git commit -a -m "new cartago $REL"
git push
