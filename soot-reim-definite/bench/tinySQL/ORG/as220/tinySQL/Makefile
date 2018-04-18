#---------------------------------------------------------------
# Makefile
#
# Invoke 'make all' to compile everything
# Invoke 'make jar' if you wish to build a tinySQL.jar distribution file
#
# compile manually with:
#   jikes -nowarn -d ../../../../classes *.java
# compile lex/yacc as well:
#   make clean; rm parser.java sym.java scanner.lex.java; java JLex.Main scanner.lex; java java_cup.Main < tinySQL.cup; jikes -nowarn -d ../../../../classes *.java
#---------------------------------------------------------------

INSTALL_TOP_DIR = ../../../../classes
INSTALL_DIR = $(INSTALL_TOP_DIR)/ORG/as220/tinySQL

# If you have jikes from http://www.alphaworks.ibm.com/tech/jikes
# installed use it (it is 10 times faster than javac):
JAVAC = javac -deprecation -d $(INSTALL_TOP_DIR)
#JAVAC = jikes -nowarn -d $(INSTALL_TOP_DIR)
#JAVAC = jikes -nowarn -d $(INSTALL_TOP_DIR) -classpath=../../..:$(CLASSPATH)
JAVA = java

.SUFFIXES: .java .class

.java.class:
	${JAVAC} $*.java

#all: parser.java scanner parser tinySQL JDBC

all: precond parser.java scanner.lex.java
	$(JAVAC) *.java
	@echo -e "DONE ............ \033[32;40mtinySQL JDBC driver\033[0m";

JDBC: precond tinySQLJDBC textFileDriver dbfFileDriver

parser.java: tinySQL.cup
	${JAVA} java_cup.Main < tinySQL.cup
	@echo -e "DONE ............ \033[32;40mtinySQL tinxSQL.cup parser\033[0m";

parser: precond $(INSTALL_DIR)/parser.class $(INSTALL_DIR)/sym.class

$(INSTALL_DIR)/sym.class: precond sym.java

scanner.lex.java: scanner.lex
	${JAVA} JLex.Main scanner.lex
	@echo -e "DONE ............ \033[32;40mtinySQL scanner.lex lexical analyzer\033[0m";

$(INSTALL_DIR)/scanner.class: $(INSTALL_DIR)/sym.class scanner.lex.java
	${JAVAC} scanner.lex.java

scanner: $(INSTALL_DIR)/scanner.class

precond:
	mkdir -p $(INSTALL_DIR)

tinySQL: $(INSTALL_DIR)/tinySQL.class \
         $(INSTALL_DIR)/tinySQLException.class \
	 $(INSTALL_DIR)/tinySQLTable.class

tinySQLJDBC: $(INSTALL_DIR)/tinySQLResultSet.class \
             $(INSTALL_DIR)/tinySQLConnection.class \
	     $(INSTALL_DIR)/tinySQLResultSetMetaData.class \
	     $(INSTALL_DIR)/tinySQLDriver.class \
	     $(INSTALL_DIR)/tinySQLStatement.class \
	     $(INSTALL_DIR)/tinySqlDatabaseMetaData.class \
	     $(INSTALL_DIR)/Utils.class

textFileDriver: $(INSTALL_DIR)/textFile.class \
                $(INSTALL_DIR)/textFileDriver.class \
		$(INSTALL_DIR)/textFileConnection.class \
		$(INSTALL_DIR)/textFileTable.class \
		$(INSTALL_DIR)/testTextFile.class

dbfFileDriver: $(INSTALL_DIR)/dbfFileTable.class \
               $(INSTALL_DIR)/dbfFile.class \
	       $(INSTALL_DIR)/dbfFileDriver.class \
	       $(INSTALL_DIR)/dbfFileConnection.class \
	       $(INSTALL_DIR)/dbfFileTable.class \
	       $(INSTALL_DIR)/DBFHeader.class

jar: all
	(cd $(INSTALL_TOP_DIR); rm -f tinySQL.jar; jar cvf tinySQL.jar `find ORG -name "*.class"` `find java_cup -name "*.class"`)
	@echo -e "DONE ............ \033[32;40mJAR file ../../../../classes/tinySQL.jar\033[0m";

clean: 
	rm -f *.class 
	rm -f $(INSTALL_DIR)/*.class 
	rm -f $(INSTALL_TOP_DIR)/*.class
	rm -f $(INSTALL_TOP_DIR)/tinySQL.jar

parserclean:
	@echo "Don't do this unless you've installed CUP and JLex"
	@echo "or you won't be able to build tinySQL!!!"
	@echo
	@echo "CTRL+C in 5 seconds to cancel!"
	@sleep 5
	rm -f sym.java parser.java scanner.lex.java 

distclean: clean parserclean
	rm -f *~
	

