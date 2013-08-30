This directory contains design documents related to JSR 308 ("Annotations
on Java types").

Also see file README-jsr308.html .

To copy the documents to the website:
1. See the comments in the Makefile for instructions regarding how to
update the jsr308-changes.html file.
2. Run
  make web
3. See the results at
  http://types.cs.washington.edu/jsr308/

To update the repository to a newer version of the upstream OpenJDK, do either:
  hg fetch http://hg.openjdk.java.net/jdk8/tl/langtools
or:
  hg fetch http://hg.openjdk.java.net/jdk8/jdk8/langtools
They sometimes contain different changesets.

Also see the instructions at:
  ../../checker-framework/release/README-maintainers.html
