#/bin/sh

# use gradle 4
export PATH=$HOME/bin/gradle-4.10.2/bin:$PATH


# check user at sourceforge
if [ -z $USERSF ] ; then
    echo the var USERSF must be set with the username at source forge
    exit
fi

REL=2.4


# for cartago

gradle cartagoJar

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

scp -r $MAVEN/$REL $USERSF,jacamo@web.sf.net:/home/project-web/jacamo/htdocs/maven2/org/jacamo/cartago

# for Jaca

gradle jacaJar
MAVEN=$HOME/.m2/repository/org/jacamo/jaca

mkdir -p $MAVEN/$REL

cp build/dist/cartago-$REL/lib/jaca-$REL.jar $MAVEN/$REL

# send to SF
POM=$MAVEN/$REL/jaca-$REL.pom
echo '<?xml version="1.0" encoding="UTF-8"?>' > $POM
echo '<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' >> $POM
echo '<modelVersion>4.0.0</modelVersion>' >> $POM
echo '  <groupId>org.jacamo</groupId>' >> $POM
echo '  <artifactId>jaca</artifactId>' >> $POM
echo '  <version>'$REL'</version>' >> $POM
echo '</project>' >> $POM

scp -r $MAVEN/$REL $USERSF,jacamo@web.sf.net:/home/project-web/jacamo/htdocs/maven2/org/jacamo/jaca
