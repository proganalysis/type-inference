# Put user-specific changes in your own Makefile.user.
# Make will silently continue if that file does not exist.
-include Makefile.user

jsr308_www ?= /cse/www2/types/jsr308
jsr308_spec = ${jsr308_www}/specification
jsr308_www_online ?= http://types.cs.washington.edu/jsr308

PLUME_LIB ?= ${HOME}/plume-lib
PLUME_BIN ?= ${PLUME_LIB}/bin

RELEASEDATE ?= `date '+%Y%m%d'`

CHECKLINK ?= PERL5LIB=/homes/gws/mernst/bin/src/perl:/homes/gws/mernst/bin/src/perl/share/perl5:/homes/gws/mernst/bin/src/perl/lib64/perl5:/homes/gws/mernst/research/steering/colony-2003/experiment-scripts:/usr/share/perl5/ ${PLUME_BIN}/checklink

export PERLLIB := ${PERLLIB}:/homes/gws/mernst/bin/src/perl/lib/perl5/site_perl/5.10.0
export PERL5LIB := ${PERL5LIB}:/homes/gws/mernst/bin/src/perl/lib/perl5/site_perl/5.10.0

# There is no use of "cp -p" in this Makefile, to avoid problems with
# browser caches.

local: annotation-extensions.pdf annotation-extensions.html design.pdf design.html type-quals.pdf type-quals.html validate

clean:
	rm -f annotation-extensions.aux annotation-extensions.blg annotation-extensions.dvi annotation-extensions.haux annotation-extensions.html annotation-extensions.htoc annotation-extensions.log annotation-extensions.pdf annotation-extensions.toc
	rm -f design.aux design.blg design.dvi design.haux design.html design.htoc design.image.tex design.log design.pdf design.toc design001.gif design001.png design002.gif design002.png design003.gif design003.png
	rm -f figures/type-tree.eps figures/type-tree.pdf figures/type-tree2.eps figures/type-tree2.pdf figures/type-tree3.eps figures/type-tree3.pdf
	rm -f type-quals.aux type-quals.dvi type-quals.html type-quals.log type-quals.pdf

figures-all:
	${MAKE} -C figures all

html: annotation-extensions.html design.html type-quals.html

# Check HTML
validate:
	validate jsr308-webpage.html jsr308-faq.html jsr308-changes.html README-jsr308.html

# Update the main webpage & FAQ only, but not any of the supporting documents.
# Useful to fix a minor typo (but then you might just want to edit both versions, identically).
webpage: ${jsr308_www}/jsr308-webpage.html ${jsr308_www}/jsr308-faq.html
#	$(MAKE) checklinks

# Warning!  "make web" makes a public release!
web: \
${jsr308_www} \
${jsr308_www}/jsr308-webpage.html \
${jsr308_www}/jsr308-faq.html \
${jsr308_www}/README-jsr308.html \
${jsr308_www}/Makefile-jsr308-install \
${jsr308_www}/changelog-jsr308.txt \
${jsr308_www}/favicon-jsr308.png \
${jsr308_www}/java-type-qualifiers.pdf \
${jsr308_www}/java-type-qualifiers.html
	$(MAKE) checklinks

# Make a release of the specification.
# The "web" target is executed with every JSR 308 compiler release.
# The "web-spec" target will be executed less frequently.
#
# Before doing this, update jsr308-changes.html.  Determine changes via log or diff:
#   hg log --style changelog --date '>2009-05-14' design.tex jsr308-faq.html
# where "2009-05-14" is the date of the previous entry in jsr308-changes.html
web-spec: \
${jsr308_www} \
${jsr308_spec} \
${jsr308_spec}/java-annotation-design.pdf \
${jsr308_spec}/java-annotation-design.html \
${jsr308_spec}/java-annotation-extensions.pdf \
${jsr308_spec}/java-annotation-extensions.html \
${jsr308_spec}/jsr308-changes.html
	\cp -f design.html ${jsr308_spec}/java-annotation-design-${RELEASEDATE}.html
	\cp -f design.pdf ${jsr308_spec}/java-annotation-design-${RELEASEDATE}.pdf
	$(MAKE) checklinks



## There are some expected errors, but they should all be suppressed by
## the checklink-args.txt file, so this should complete cleanly.
# * The two errors related to 'annotation_type_element_detail' are the due to
#   a bug in the Javadoc generator when a default value is given to an
#   annotation element.
# * Others are a result of linking our javadocs to the Oracle JDK
#   javadoc.  Any classes that are introduced by JSR 308 (e.g.
#   AnnotatedType tree) or affected by it (e.g. AnnotatedTypeVisitor,
#   AnnotatedTypeScanner, SourceVersion) report an error.  A fix is to
#   distribute our own Javadoc for the compiler.
checklinks: ${PLUME_BIN}
	${CHECKLINK} -q -r -e `cat ${PLUME_BIN}/checklink-args.txt` ${jsr308_www_online}
	${CHECKLINK} -q -r -e `cat ${PLUME_BIN}/checklink-args.txt` ${jsr308_www_online}/../checker-framework


###########################################################################

annotation-extensions.dvi: annotation-extensions.tex
	$(MAKE) -C figures
	latex $<
	bibtex $(basename $<)
	perl -pi -e 's/%\n//' annotation-extensions.bbl
	latex $<

annotation-extensions.pdf: annotation-extensions.tex annotation-extensions.dvi
	pdflatex $<
	bibtex $(basename $<)
	perl -pi -e 's/%\n//' annotation-extensions.bbl
	pdflatex $<

annotation-extensions.html: annotation-extensions.tex annotation-extensions.dvi annotation-extensions.bbl
	hevea -fix -exec xxdate.exe urlhref.hva annotation-extensions.tex
	mv annotation-extensions.html annotation-extensions.html-with-htoc
	hevea-retarget-crossrefs < annotation-extensions.html-with-htoc > annotation-extensions.html
	rm -rf annotation-extensions.html-with-htoc
#	perl -pi -e 's/(be as clear.<\/P><OL )/$$1start="5" /' annotation-extensions.html
#	perl -pi -e 's/(<!--TOC section Introduction-->)/$$1\n\n<p>Contents:<\/p>\n<!-- start toc.  do not edit; run html-update-toc instead -->\n<!-- end toc -->\n\n<br>\n/' annotation-extensions.html
#	html-update-toc annotation-extensions.html

${jsr308_spec}/java-annotation-extensions.pdf: annotation-extensions.pdf ${jsr308_spec} ${PLUME_BIN}
	\cp -f $< $@
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html

${jsr308_spec}/java-annotation-extensions.html: annotation-extensions.html ${jsr308_spec} ${PLUME_BIN}
	\cp -f $< $@
# We don't know which of the two files will be created
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html


design.dvi: design.tex
	$(MAKE) -C figures
	latex $<
	bibtex $(basename $<)
	perl -pi -e 's/%\n//' design.bbl
	latex $<

design.pdf: design.tex design.dvi
	pdflatex $<
	bibtex $(basename $<)
	perl -pi -e 's/%\n//' design.bbl
	pdflatex $<

design.html: design.tex design.dvi design.bbl
	hevea -fix -exec xxdate.exe urlhref.hva design.tex
	mv design.html design.html-with-htoc
	hevea-retarget-crossrefs < design.html-with-htoc > design.html
	rm -rf design.html-with-htoc
#	perl -pi -e 's/(be as clear.<\/P><OL )/$$1start="5" /' design.html
#	perl -pi -e 's/(<!--TOC section Introduction-->)/$$1\n\n<p>Contents:<\/p>\n<!-- start toc.  do not edit; run html-update-toc instead -->\n<!-- end toc -->\n\n<br>\n/' design.html
#	html-update-toc design.html

${jsr308_www}:
	mkdir -p ${jsr308_www}

${jsr308_spec}:
	mkdir -p ${jsr308_spec}

${jsr308_spec}/java-annotation-design.pdf: design.pdf ${jsr308_spec} ${PLUME_BIN}
	\cp -f $< $@
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html

${jsr308_spec}/java-annotation-design.html: design.html ${jsr308_spec} ${PLUME_BIN}
	\cp -f $< $@
# We don't know which of the two files will be created
	-\cp -f design001.gif ${jsr308_spec}
	-\cp -f design001.png ${jsr308_spec}
	-\cp -f design002.gif ${jsr308_spec}
	-\cp -f design002.png ${jsr308_spec}
	-\cp -f design003.gif ${jsr308_spec}
	-\cp -f design003.png ${jsr308_spec}
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html

###########################################################################

type-quals.dvi: type-quals.tex
	latex $<

type-quals.pdf: type-quals.tex type-quals.dvi
	pdflatex $<

type-quals.html: type-quals.tex type-quals.dvi
	hevea -exec xxdate.exe urlhref.hva type-quals.tex
	perl -pi -e 's/(<!--TOC section Introduction-->)/$$1\n\n<p>Contents:<\/p>\n<!-- start toc.  do not edit; run html-update-toc instead -->\n<!-- end toc -->\n\n<br>\n/' type-quals.html
	html-update-toc type-quals.html

${jsr308_www}/java-type-qualifiers.pdf: type-quals.pdf ${jsr308_www} ${PLUME_BIN}
	\cp -f $< $@
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html

${jsr308_www}/java-type-qualifiers.html: type-quals.html ${PLUME_BIN}
	\cp -f $< $@
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html

###########################################################################

${jsr308_www}/jsr308-webpage.html: jsr308-webpage.html ${jsr308_www} ${PLUME_BIN}
	validate $<
	\cp -f $< $@
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html

${jsr308_www}/jsr308-faq.html: jsr308-faq.html ${jsr308_www} ${PLUME_BIN}
	validate $<
	\cp -f $< $@
#	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html

${jsr308_www}/changelog-jsr308.txt: changelog-jsr308.txt ${jsr308_www}
	\cp -f $< $@

${jsr308_www}/README-jsr308.html: README-jsr308.html ${jsr308_www} ${PLUME_BIN}
	validate $<
	\cp -f $< $@
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates README-jsr308.html

${jsr308_spec}/jsr308-changes.html: jsr308-changes.html ${jsr308_spec} ${PLUME_BIN}
	validate $<
	\cp -f $< $@
	cd ${jsr308_www}; ${PLUME_BIN}/html-update-link-dates jsr308-webpage.html

${jsr308_www}/Makefile-jsr308-install: Makefile-jsr308-install ${jsr308_www}
	\cp -f $< $@

${jsr308_www}/favicon-jsr308.png: favicon-jsr308.png ${jsr308_www}
	\cp -f $< $@


###########################################################################

## These targets ensure that the directory exists, but do *not* make
## it up to date.

# These targets don't work!
# The "cd ${PLUME_LIB}/.." doesn't work if the directory doesn't exist.
# Instead, create the directory? Or use /tmp?
#
# ${PLUME_LIB}:
#	cd ${PLUME_LIB}/..; hg clone https://code.google.com/p/plume-lib/ plume-lib

${PLUME_BIN}: # ${PLUME_LIB}



###########################################################################
