find src/com -name '*.java' | xargs sed -i 's/^\([ \t]*\)\([^(]*(\)\(@[a-zA-Z]*\)\([^@]*this[,]\?\)/\1\3This \2/g'
