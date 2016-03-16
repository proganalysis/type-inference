grep -Rl --color=never '^[ \t]*[^@]*@[^@,]*\[\]' ./src/* | xargs sed 's/\([a-zA-Z]*\) \(@[^ ]*\) \(\[\]\)/\2 \1\3/g' 
