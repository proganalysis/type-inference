find src -name '*.java' | xargs sed -i '' 's/([a-zA-Z]*) (@[^ ]*) (\[\])/\2 \1\3/g' 
