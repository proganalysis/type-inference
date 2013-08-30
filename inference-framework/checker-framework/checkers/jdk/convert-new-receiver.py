#!/usr/bin/python
# It DOESN'T work for methods that are declared over more than one line.

import os
import re
import sys



def convert(fileName):
    """Change one JSR 308 receiver annotation from old style to new
    style.  Moves the annotation from after the parameter list (in C++
    location) to a new first parameter named "this"."""

    # A stack for keeping class names and curly braces
    stack = []

    # Another stack for keeping the current class
    current_class = []

    # Regex for class declaration
    class_pattern = re.compile(r".*?(\s+|^)(class|interface)\s+([A-Za-z0-9_]+\s*(<[A-Za-z0-9_]+(\s*,\s*[A-Za-z0-9_]+)*>)?)")
    method_pattern = re.compile(r".*?(\s+|^)([A-Za-z0-9_]+?\s*\()([^)]*)(\)\s*(((\/\*)?\s*@[A-Za-z0-9_]+\s*(\*\/)?)+))")

    # Tmp file
    tmpFileName = fileName + '.tmp'

    # Open file
    file = open(fileName)
    tmpFile = open(tmpFileName, 'w')


    # Scan the file line by line
    for line in file:
        class_names = {}
        method_names = {}
        # Match the class declarations and store into a dict: pos -> name
        m = class_pattern.findall(line)
        for match_classes in m:
            class_name = match_classes[2].strip()
            class_names[line.find(class_name)] = class_name

        # Now we look for the method receiver annotation
        for match_methods in  method_pattern.findall(line):
#            print match_methods
            method_name = match_methods[1].strip()
            parameters = match_methods[2].strip()
            be_removed = match_methods[3].strip()
            annotations = match_methods[4].strip()
            method_names[line.find(method_name)] = (method_name, parameters, be_removed, annotations)

        new_line = line

        for i in range(0, len(line)):
            c = line[i]
            if i in class_names:
                stack.append(class_names[i])
                current_class.append(class_names[i])
#                print "push class:", class_names[i]
            elif c == '{':
                stack.append('{')
            elif c == '}':
                stack.pop()
                if len(stack) > 0 and stack[len(stack) - 1] != '{':
                    # In this case, we need to pop the class name
                    stack.pop()
#                    print "pop class:", current_class.pop()
            elif i in method_names:
                # We do the subsititution. This may fail if therer more than
                # one method invocation
                current = current_class[len(current_class) - 1]
#                print "current:", current
                old_method = method_names[i][0]
                parameters = method_names[i][1]
                be_removed = method_names[i][2]
                annotations = method_names[i][3]
                new_method = old_method + annotations + " " + current + " this"
                if parameters != "":
                    new_method = new_method + ", "
                replace_line = new_line[i:]
                replace_line = replace_line.replace(be_removed, ") ").replace(old_method, new_method)
                new_line = new_line[: i] + replace_line
        tmpFile.write(new_line)
#        print new_line,

    file.close()
    tmpFile.close()

    # Rename the file
    os.rename(tmpFileName, fileName)


def main():
    if len(sys.argv) < 1:
        print 'Usage:', sys.argv[0], '<fileName> <fileName> ...'
    else:
        for i in range(1, len(sys.argv)):
            convert(sys.argv[i])

if __name__ == '__main__':
    main()
