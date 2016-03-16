grep -Rl --color=never '^[ \t]*[^@]*@[^@,]*this' ./src/* | xargs  sed 's/^\([ \t]*\)\([^(]*(\)\(@[a-zA-Z]*\)\([^@,]*this[,]\?\)/\1\3This \2/g' 
