all: pdf eps
pdf: type-tree.pdf type-tree2.pdf type-tree3.pdf
eps: type-tree.eps type-tree2.eps type-tree3.eps

%.pdf : %.eps
	epstopdf $<

%.eps : %.dia
	dia -t eps $<

clean:
	@\rm *.eps
	@\rm *.pdf
