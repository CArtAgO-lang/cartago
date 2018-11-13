#
# by Jomi
#

cd ..
#gradle renderAsciidoc
gradle javadoc
cd docs
asciidoctor readme.adoc
cp readme.html index.html
scp -r *  $USERSF,cartago@web.sf.net:/home/project-web/jacamo/htdocs/cartago/doc
